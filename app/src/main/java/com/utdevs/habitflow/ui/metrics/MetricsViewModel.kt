/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para Metricas Objetivas, Curva de Enfoque y Veredicto del Dia 21 por goalId.
 */
package com.utdevs.habitflow.ui.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.SprintMetricsSummary
import com.utdevs.habitflow.domain.model.VerdictTier
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.domain.repository.PlanDataSource
import com.utdevs.habitflow.domain.usecase.ApplyVerdictResolutionUseCase
import com.utdevs.habitflow.domain.usecase.ComputeMetricsAndVerdictUseCase
import kotlin.math.ceil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MetricsUiState(
    val goal: GoalSprint = GoalSprint(),
    val summary: SprintMetricsSummary? = null,
    val dailySeriesMinutes: List<Int> = emptyList(),
    val dailyCompletedFlags: List<Boolean> = emptyList(),
    val isApplying: Boolean = false,
    val errorMessage: String? = null
)

class MetricsViewModel(
    private val uid: String,
    private val goalId: String,
    private val goalsRepository: GoalsRepository,
    private val planDataSource: PlanDataSource,
    private val computeMetricsAndVerdictUseCase: ComputeMetricsAndVerdictUseCase,
    private val applyVerdictResolutionUseCase: ApplyVerdictResolutionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetricsUiState())
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()

    fun bindState(goal: GoalSprint) {
        val state = goal.sprint
        val summary = computeMetricsAndVerdictUseCase(state)
        val allDays = planDataSource.getPhases().flatMap { it.days }
        val series = allDays.map { d ->
            val logged = state.dailyLogs[d.day]?.effectiveFocusMinutes ?: 0
            if (logged > 0) logged
            else ceil(d.focusMinutes * state.durationFactor).toInt().coerceAtLeast(1)
        }
        val flags = allDays.map { d -> d.day in state.completedDays }

        _uiState.update {
            it.copy(
                goal = goal,
                summary = summary,
                dailySeriesMinutes = series,
                dailyCompletedFlags = flags
            )
        }
    }

    fun executeRecommendedAction(onNavigateAfterAction: (VerdictTier) -> Unit) {
        val current = _uiState.value
        val summary = current.summary ?: return
        val resolution = applyVerdictResolutionUseCase(current.goal.sprint, summary)
        val nextStatus = if (summary.verdictTier == VerdictTier.HIGH_MASTERY) {
            GoalStatus.MAINTENANCE
        } else {
            GoalStatus.ACTIVE
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isApplying = true, errorMessage = null) }
            goalsRepository.applyVerdictTransition(
                uid = uid,
                goalId = goalId,
                newState = resolution.nextState,
                newGoalStatus = nextStatus,
                archiveSnapshot = resolution.historySnapshot
            ).onSuccess {
                _uiState.update { it.copy(isApplying = false) }
                onNavigateAfterAction(summary.verdictTier)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(isApplying = false, errorMessage = err.message ?: "Error al aplicar resolucion.")
                }
            }
        }
    }

    class Factory(
        private val uid: String,
        private val goalId: String,
        private val goalsRepository: GoalsRepository,
        private val planDataSource: PlanDataSource,
        private val computeMetricsAndVerdictUseCase: ComputeMetricsAndVerdictUseCase,
        private val applyVerdictResolutionUseCase: ApplyVerdictResolutionUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MetricsViewModel(
                uid = uid,
                goalId = goalId,
                goalsRepository = goalsRepository,
                planDataSource = planDataSource,
                computeMetricsAndVerdictUseCase = computeMetricsAndVerdictUseCase,
                applyVerdictResolutionUseCase = applyVerdictResolutionUseCase
            ) as T
    }
}
