/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para el Dashboard de una Meta (Bento Grid, Planta Viva,
 * Barras Semanales, Donuts de Puntualidad/Cumplimiento y Resolucion de 21 Dias).
 */
package com.utdevs.habitflow.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.data.reminder.GoalReminderScheduler
import com.utdevs.habitflow.domain.dopamine.EvaluatePlantHealthUseCase
import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.DayStatus
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.PhaseSpec
import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.PlantStage
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.domain.repository.PlanDataSource
import com.utdevs.habitflow.domain.usecase.ComputeDayBlocksUseCase
import com.utdevs.habitflow.domain.usecase.ComputeMetricsAndVerdictUseCase
import com.utdevs.habitflow.domain.usecase.ComputePlantStageUseCase
import com.utdevs.habitflow.domain.usecase.ResolveDayStatesUseCase
import com.utdevs.habitflow.ui.components.charts.BarDatum
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardDayCardModel(
    val day: Int,
    val phaseId: Int,
    val effectiveFocusMinutes: Int,
    val status: DayStatus
)

data class DashboardPhaseSection(
    val phaseSpec: PhaseSpec,
    val dayCards: List<DashboardDayCardModel>
)

data class DashboardUiState(
    val goal: GoalSprint = GoalSprint(),
    val plantStage: PlantStage = PlantStage.SEED,
    val plantHealth: PlantHealthState = PlantHealthState.THRIVING,
    val progressPercent: Int = 0,
    val punctualityPercent: Int = 0,
    val accumulatedMinutes: Int = 0,
    val targetTotalMinutes: Int = 785,
    val activeDay: Int = 1,
    val weeklyBars: List<BarDatum> = emptyList(),
    val phases: List<DashboardPhaseSection> = emptyList(),
    val feedbackMessage: String? = null
)

class DashboardViewModel(
    private val uid: String,
    private val goalId: String,
    private val goalsRepository: GoalsRepository,
    private val planDataSource: PlanDataSource,
    private val resolveDayStatesUseCase: ResolveDayStatesUseCase,
    private val computePlantStageUseCase: ComputePlantStageUseCase,
    private val computeDayBlocksUseCase: ComputeDayBlocksUseCase,
    private val computeMetricsAndVerdictUseCase: ComputeMetricsAndVerdictUseCase,
    private val evaluatePlantHealthUseCase: EvaluatePlantHealthUseCase,
    private val reminderScheduler: GoalReminderScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun bindState(goal: GoalSprint, profile: UserProfile) {
        val state = goal.sprint
        val resolution = resolveDayStatesUseCase(state)
        if (resolution.newlyMissedDays.isNotEmpty()) {
            viewModelScope.launch {
                val shieldAvailable = profile.streakShields > 0
                val remaining = if (shieldAvailable) (profile.streakShields - 1).coerceAtLeast(0) else 0
                goalsRepository.markDaysMissed(
                    uid = uid,
                    goalId = goalId,
                    missedDays = resolution.newlyMissedDays,
                    newCurrentDay = resolution.effectiveCurrentDay,
                    shieldConsumed = shieldAvailable,
                    remainingShields = remaining
                )
            }
        }

        val plant = computePlantStageUseCase(state.completedDays.size)
        val health = evaluatePlantHealthUseCase(state, justCompletedToday = false)
        val metrics = computeMetricsAndVerdictUseCase(state)
        val pct = metrics.compliancePercentage.toInt()
        val punctPct = metrics.punctualityPercentage.toInt()

        val allPhases = planDataSource.getPhases()
        val sections = allPhases.map { phase ->
            val cards = phase.days.map { daySpec ->
                val blocks = computeDayBlocksUseCase(daySpec, state.durationFactor)
                val focusMins = blocks.filter { it.type == BlockType.FOCUS }.sumOf { it.durationMinutes }
                DashboardDayCardModel(
                    day = daySpec.day,
                    phaseId = phase.phaseId,
                    effectiveFocusMinutes = if (daySpec.day == 20) {
                        ceil(daySpec.focusMinutes * state.durationFactor).toInt().coerceAtLeast(1)
                    } else {
                        focusMins
                    },
                    status = resolution.statuses[daySpec.day] ?: DayStatus.LOCKED
                )
            }
            DashboardPhaseSection(phaseSpec = phase, dayCards = cards)
        }

        val activePhaseIndex = ((resolution.effectiveCurrentDay - 1) / 7).coerceIn(0, 2)
        val barData = allPhases.getOrNull(activePhaseIndex)?.days?.map { d ->
            BarDatum(
                label = "D${d.day}",
                value = ceil(d.focusMinutes * state.durationFactor).toInt().coerceAtLeast(1),
                completed = d.day in state.completedDays
            )
        } ?: emptyList()

        _uiState.update {
            it.copy(
                goal = goal,
                plantStage = plant,
                plantHealth = health,
                progressPercent = pct,
                punctualityPercent = punctPct,
                accumulatedMinutes = metrics.accumulatedFocusMinutes,
                targetTotalMinutes = metrics.targetTotalMinutes,
                activeDay = resolution.effectiveCurrentDay,
                weeklyBars = barData,
                phases = sections
            )
        }
    }

    fun updateGoalParams(title: String, params: DayZeroParams) {
        if (title.isBlank() || !params.isValid()) return
        viewModelScope.launch {
            goalsRepository.updateGoalParams(uid, goalId, title, params)
                .onSuccess {
                    val updated = _uiState.value.goal.copy(
                        title = title.trim(),
                        sprint = _uiState.value.goal.sprint.copy(
                            anchorTask = params.anchorTask.trim(),
                            triggerTime = params.triggerTime.trim(),
                            triggerLocation = params.triggerLocation.trim(),
                            blockingRule = params.blockingRule.trim()
                        )
                    )
                    reminderScheduler.scheduleGoalReminders(updated)
                    _uiState.update { it.copy(feedbackMessage = "Parametros actualizados.") }
                }
                .onFailure { err ->
                    _uiState.update { it.copy(feedbackMessage = err.message ?: "Error al actualizar.") }
                }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    class Factory(
        private val uid: String,
        private val goalId: String,
        private val goalsRepository: GoalsRepository,
        private val planDataSource: PlanDataSource,
        private val resolveDayStatesUseCase: ResolveDayStatesUseCase,
        private val computePlantStageUseCase: ComputePlantStageUseCase,
        private val computeDayBlocksUseCase: ComputeDayBlocksUseCase,
        private val computeMetricsAndVerdictUseCase: ComputeMetricsAndVerdictUseCase,
        private val evaluatePlantHealthUseCase: EvaluatePlantHealthUseCase,
        private val reminderScheduler: GoalReminderScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DashboardViewModel(
                uid = uid,
                goalId = goalId,
                goalsRepository = goalsRepository,
                planDataSource = planDataSource,
                resolveDayStatesUseCase = resolveDayStatesUseCase,
                computePlantStageUseCase = computePlantStageUseCase,
                computeDayBlocksUseCase = computeDayBlocksUseCase,
                computeMetricsAndVerdictUseCase = computeMetricsAndVerdictUseCase,
                evaluatePlantHealthUseCase = evaluatePlantHealthUseCase,
                reminderScheduler = reminderScheduler
            ) as T
    }
}
