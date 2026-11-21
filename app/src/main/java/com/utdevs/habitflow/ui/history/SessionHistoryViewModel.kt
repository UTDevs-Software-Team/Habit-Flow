/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para SessionHistoryScreen (Donut 785m + Timeline cronologico).
 */
package com.utdevs.habitflow.ui.history

import androidx.lifecycle.ViewModel
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.usecase.ComputeMetricsAndVerdictUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SessionHistoryUiState(
    val goalTitle: String = "",
    val phase1Minutes: Int = 0,
    val phase2Minutes: Int = 0,
    val phase3Minutes: Int = 0,
    val targetTotalMinutes: Int = 785,
    val logs: List<DailyLogEntry> = emptyList()
)

class SessionHistoryViewModel(
    private val computeMetricsAndVerdictUseCase: ComputeMetricsAndVerdictUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionHistoryUiState())
    val uiState: StateFlow<SessionHistoryUiState> = _uiState.asStateFlow()

    fun bindState(goal: GoalSprint) {
        val summary = computeMetricsAndVerdictUseCase(goal.sprint)
        val p1 = summary.phaseComparison.firstOrNull { it.phaseId == 1 }?.accumulatedMinutes ?: 0
        val p2 = summary.phaseComparison.firstOrNull { it.phaseId == 2 }?.accumulatedMinutes ?: 0
        val p3 = summary.phaseComparison.firstOrNull { it.phaseId == 3 }?.accumulatedMinutes ?: 0
        val sortedLogs = goal.sprint.dailyLogs.values.sortedByDescending { it.day }

        _uiState.update {
            it.copy(
                goalTitle = goal.title,
                phase1Minutes = p1,
                phase2Minutes = p2,
                phase3Minutes = p3,
                targetTotalMinutes = summary.targetTotalMinutes,
                logs = sortedLogs
            )
        }
    }
}
