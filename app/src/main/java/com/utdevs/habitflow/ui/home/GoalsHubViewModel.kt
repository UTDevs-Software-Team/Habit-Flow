/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para GoalsHubScreen ("Bienvenido / + Nueva Meta / Tus Metas").
 * Aplica ValidateActiveGoalsLimitUseCase (max 3 activas) y EvaluatePlantHealthUseCase.
 */
package com.utdevs.habitflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.data.reminder.GoalReminderScheduler
import com.utdevs.habitflow.domain.dopamine.EvaluatePlantHealthUseCase
import com.utdevs.habitflow.domain.dopamine.ValidateActiveGoalsLimitUseCase
import com.utdevs.habitflow.domain.model.AchievementSpec
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.MAX_ACTIVE_GOALS
import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.PlantStage
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.repository.DopamineCatalogSource
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.domain.usecase.ComputePlantStageUseCase
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GoalHubItemUi(
    val goal: GoalSprint,
    val plantStage: PlantStage,
    val healthState: PlantHealthState,
    val progressFraction: Float
)

data class GoalsHubUiState(
    val profile: UserProfile = UserProfile(),
    val items: List<GoalHubItemUi> = emptyList(),
    val allAchievements: List<AchievementSpec> = emptyList(),
    val nextRankTitle: String = "",
    val nextRankMinXp: Int = 100,
    val activeGoalsCount: Int = 0,
    val maxActiveGoals: Int = MAX_ACTIVE_GOALS,
    val canActivateMore: Boolean = true,
    val feedbackMessage: String? = null
)

class GoalsHubViewModel(
    private val uid: String,
    private val email: String,
    private val goalsRepository: GoalsRepository,
    private val dopamineCatalogSource: DopamineCatalogSource,
    private val computePlantStageUseCase: ComputePlantStageUseCase,
    private val evaluatePlantHealthUseCase: EvaluatePlantHealthUseCase,
    private val validateActiveGoalsLimitUseCase: ValidateActiveGoalsLimitUseCase,
    private val reminderScheduler: GoalReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsHubUiState())
    val uiState: StateFlow<GoalsHubUiState> = _uiState.asStateFlow()

    fun bind(profile: UserProfile, goals: List<GoalSprint>) {
        val limit = validateActiveGoalsLimitUseCase(goals)
        val catalog = dopamineCatalogSource.getCatalog()
        val nextRank = catalog.ranks.sortedBy { it.minXp }.firstOrNull { it.minXp > profile.totalXp }
        val uiItems = goals.map { g ->
            val stage = computePlantStageUseCase(g.sprint.completedDays.size)
            val health = evaluatePlantHealthUseCase(g.sprint, justCompletedToday = false)
            val frac = (g.sprint.completedDays.size / 21f).coerceIn(0f, 1f)
            GoalHubItemUi(
                goal = g,
                plantStage = stage,
                healthState = health,
                progressFraction = frac
            )
        }
        _uiState.update {
            it.copy(
                profile = profile,
                items = uiItems,
                allAchievements = catalog.achievements,
                nextRankTitle = nextRank?.title ?: "Rango Maximo",
                nextRankMinXp = nextRank?.minXp ?: profile.totalXp.coerceAtLeast(1),
                activeGoalsCount = limit.activeCount,
                canActivateMore = limit.canActivateMore
            )
        }
    }

    fun createGoal(
        title: String,
        setup: DayZeroParams,
        onCreated: (String) -> Unit
    ) {
        if (title.isBlank() || !setup.isValid()) {
            _uiState.update { it.copy(feedbackMessage = "Completa el nombre de la meta y los 4 parametros.") }
            return
        }
        val initialStatus = if (_uiState.value.canActivateMore) GoalStatus.ACTIVE else GoalStatus.PAUSED
        viewModelScope.launch {
            goalsRepository.createGoal(
                uid = uid,
                email = email,
                title = title,
                setup = setup,
                startDateIso = LocalDate.now().toString(),
                initialStatus = initialStatus
            ).onSuccess { goalId ->
                if (initialStatus == GoalStatus.ACTIVE) {
                    val created = GoalSprint(
                        goalId = goalId,
                        title = title.trim(),
                        status = initialStatus,
                        sprint = com.utdevs.habitflow.domain.model.UserSprintState(
                            anchorTask = setup.anchorTask.trim(),
                            triggerTime = setup.triggerTime.trim(),
                            triggerLocation = setup.triggerLocation.trim(),
                            blockingRule = setup.blockingRule.trim()
                        )
                    )
                    reminderScheduler.scheduleGoalReminders(created)
                    onCreated(goalId)
                } else {
                    _uiState.update {
                        it.copy(
                            feedbackMessage = "Meta creada en Pausa: ya tienes $MAX_ACTIVE_GOALS metas activas para evitar paralisis."
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update { it.copy(feedbackMessage = err.message ?: "Error al crear meta.") }
            }
        }
    }

    fun toggleGoalPause(goal: GoalSprint) {
        val targetStatus = if (goal.status == GoalStatus.PAUSED) {
            if (!_uiState.value.canActivateMore) {
                _uiState.update {
                    it.copy(feedbackMessage = "Limite alcanzado ($MAX_ACTIVE_GOALS activas). Pausa otra meta antes de activar esta.")
                }
                return
            }
            if (goal.sprint.inMaintenanceMode) GoalStatus.MAINTENANCE else GoalStatus.ACTIVE
        } else {
            GoalStatus.PAUSED
        }

        viewModelScope.launch {
            goalsRepository.setGoalStatus(uid, goal.goalId, targetStatus)
                .onSuccess {
                    if (targetStatus == GoalStatus.PAUSED) {
                        reminderScheduler.cancelGoalReminders(goal.goalId)
                    } else {
                        reminderScheduler.scheduleGoalReminders(goal.copy(status = targetStatus))
                    }
                }
        }
    }

    fun updateNickname(newNickname: String) {
        if (newNickname.trim().length < 2) return
        viewModelScope.launch {
            goalsRepository.saveNickname(uid, email, newNickname.trim())
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    class Factory(
        private val uid: String,
        private val email: String,
        private val goalsRepository: GoalsRepository,
        private val dopamineCatalogSource: DopamineCatalogSource,
        private val computePlantStageUseCase: ComputePlantStageUseCase,
        private val evaluatePlantHealthUseCase: EvaluatePlantHealthUseCase,
        private val validateActiveGoalsLimitUseCase: ValidateActiveGoalsLimitUseCase,
        private val reminderScheduler: GoalReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            GoalsHubViewModel(
                uid = uid,
                email = email,
                goalsRepository = goalsRepository,
                dopamineCatalogSource = dopamineCatalogSource,
                computePlantStageUseCase = computePlantStageUseCase,
                evaluatePlantHealthUseCase = evaluatePlantHealthUseCase,
                validateActiveGoalsLimitUseCase = validateActiveGoalsLimitUseCase,
                reminderScheduler = reminderScheduler
            ) as T
    }
}
