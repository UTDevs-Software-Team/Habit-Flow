/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para Modo Mantenimiento por goalId.
 */
package com.utdevs.habitflow.ui.maintenance

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.data.feedback.HapticAndToneController
import com.utdevs.habitflow.data.theme.ThemePreferences
import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.MaintenanceSessionRecord
import com.utdevs.habitflow.domain.model.SessionBlock
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.service.FocusTimerService
import com.utdevs.habitflow.service.TimerStateHolder
import com.utdevs.habitflow.service.TimerStatus
import com.utdevs.habitflow.service.TimerUiState
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MaintenanceFormat(val label: String, val focusMin: Int, val breakMin: Int) {
    F45_10("45m Enfoque / 10m Descanso", 45, 10),
    F50_10("50m Enfoque / 10m Descanso", 50, 10)
}

data class MaintenanceUiState(
    val goal: GoalSprint = GoalSprint(),
    val selectedFormat: MaintenanceFormat = MaintenanceFormat.F45_10,
    val useSecondaryAnchor: Boolean = false,
    val secondaryAnchorDraft: String = "",
    val recentSessions: List<MaintenanceSessionRecord> = emptyList(),
    val feedbackMessage: String? = null
)

class MaintenanceViewModel(
    private val uid: String,
    private val goalId: String,
    private val goalsRepository: GoalsRepository,
    private val timerStateHolder: TimerStateHolder,
    private val hapticController: HapticAndToneController,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val sessionTag = "goal_${goalId}_maintenance"
    private val _uiState = MutableStateFlow(MaintenanceUiState())
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()
    val timerState: StateFlow<TimerUiState> = timerStateHolder.state

    init {
        viewModelScope.launch {
            goalsRepository.observeMaintenanceSessions(uid, goalId).collect { list ->
                _uiState.update { it.copy(recentSessions = list) }
            }
        }
        viewModelScope.launch {
            timerStateHolder.state.collect { tState ->
                if (tState.sessionTag == sessionTag && tState.status == TimerStatus.FINISHED) {
                    saveCompletedMaintenanceSession()
                    timerStateHolder.reset()
                }
            }
        }
    }

    fun bindState(goal: GoalSprint) {
        _uiState.update {
            it.copy(
                goal = goal,
                secondaryAnchorDraft = if (it.secondaryAnchorDraft.isBlank()) {
                    goal.sprint.secondaryAnchorTask.orEmpty()
                } else {
                    it.secondaryAnchorDraft
                }
            )
        }
    }

    fun selectFormat(format: MaintenanceFormat) = _uiState.update { it.copy(selectedFormat = format) }
    fun selectAnchor(useSecondary: Boolean) = _uiState.update { it.copy(useSecondaryAnchor = useSecondary) }
    fun onSecondaryDraftChange(text: String) = _uiState.update { it.copy(secondaryAnchorDraft = text) }

    fun saveSecondaryAnchor() {
        val draft = _uiState.value.secondaryAnchorDraft.trim()
        if (draft.isEmpty()) return
        viewModelScope.launch {
            goalsRepository.updateSecondaryAnchorTask(uid, goalId, draft).onSuccess {
                _uiState.update { it.copy(feedbackMessage = "Segunda Tarea Ancla guardada.") }
            }
        }
    }

    fun startMaintenanceSession(context: Context) {
        val fmt = _uiState.value.selectedFormat
        val blocks = listOf(
            SessionBlock(BlockType.FOCUS, fmt.focusMin),
            SessionBlock(BlockType.BREAK, fmt.breakMin)
        )
        FocusTimerService.startSession(context, sessionTag, blocks)
    }

    fun pauseTimer(context: Context) = FocusTimerService.sendAction(context, FocusTimerService.ACTION_PAUSE)
    fun resumeTimer(context: Context) = FocusTimerService.sendAction(context, FocusTimerService.ACTION_RESUME)
    fun skipTo5s(context: Context) = FocusTimerService.sendAction(context, FocusTimerService.ACTION_SKIP_TO_5S)

    fun exitMaintenanceToSprint() {
        viewModelScope.launch {
            goalsRepository.setMaintenanceModeActive(uid, goalId, active = false)
        }
    }

    private fun saveCompletedMaintenanceSession() {
        val current = _uiState.value
        val sprint = current.goal.sprint
        val anchor = if (current.useSecondaryAnchor && !sprint.secondaryAnchorTask.isNullOrBlank()) {
            sprint.secondaryAnchorTask
        } else {
            sprint.anchorTask
        }
        val record = MaintenanceSessionRecord(
            anchorTaskUsed = anchor,
            formatLabel = current.selectedFormat.label,
            focusMinutes = current.selectedFormat.focusMin,
            completedAtIso = Instant.now().toString()
        )
        viewModelScope.launch {
            goalsRepository.saveMaintenanceSession(uid, goalId, record).onSuccess {
                hapticController.triggerLevelUpCelebration(themePreferences.settings.value.soundEnabled)
                _uiState.update { it.copy(feedbackMessage = "Sesion de mantenimiento registrada.") }
            }
        }
    }

    class Factory(
        private val uid: String,
        private val goalId: String,
        private val goalsRepository: GoalsRepository,
        private val timerStateHolder: TimerStateHolder,
        private val hapticController: HapticAndToneController,
        private val themePreferences: ThemePreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MaintenanceViewModel(
                uid = uid,
                goalId = goalId,
                goalsRepository = goalsRepository,
                timerStateHolder = timerStateHolder,
                hapticController = hapticController,
                themePreferences = themePreferences
            ) as T
    }
}
