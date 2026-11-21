/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Produce la transicion de estado y el snapshot historico
 * al ejecutar la resolucion recomendada del Veredicto del Dia 21.
 */
package com.utdevs.habitflow.domain.usecase

import com.utdevs.habitflow.domain.model.SprintHistorySnapshot
import com.utdevs.habitflow.domain.model.SprintMetricsSummary
import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.model.VerdictResolutionResult
import com.utdevs.habitflow.domain.model.VerdictTier
import java.time.Instant
import java.time.LocalDate

class ApplyVerdictResolutionUseCase {

    operator fun invoke(
        currentState: UserSprintState,
        metrics: SprintMetricsSummary,
        today: LocalDate = LocalDate.now(),
        nowIso: String = Instant.now().toString()
    ): VerdictResolutionResult {
        val snapshot = SprintHistorySnapshot(
            cycleNumber = currentState.cycleNumber,
            compliancePercentage = metrics.compliancePercentage,
            verdictTier = metrics.verdictTier,
            totalMinutes = metrics.accumulatedFocusMinutes,
            archivedAtIso = nowIso
        )

        val nextState = when (metrics.verdictTier) {
            VerdictTier.HIGH_MASTERY -> currentState.copy(
                inMaintenanceMode = true,
                maintenanceUnlocked = true,
                activeRepeatRangeStart = null
            )

            VerdictTier.MODERATE_STABILITY -> {
                val keptDays = currentState.completedDays.filter { it < 15 }.toSet()
                val keptMissed = currentState.missedDays.filter { it < 15 }.toSet()
                val keptLogs = currentState.dailyLogs.filterKeys { it < 15 }
                currentState.copy(
                    currentDay = 15,
                    completedDays = keptDays,
                    missedDays = keptMissed,
                    dailyLogs = keptLogs,
                    activeRepeatRangeStart = 15,
                    cycleNumber = currentState.cycleNumber + 1,
                    sprintStartDateIso = today.minusDays(14).toString()
                )
            }

            VerdictTier.RECALIBRATION_NEEDED -> currentState.copy(
                currentDay = 1,
                completedDays = emptySet(),
                missedDays = emptySet(),
                dailyLogs = emptyMap(),
                nextMicroTask = "",
                durationFactor = 0.5,
                activeRepeatRangeStart = null,
                cycleNumber = currentState.cycleNumber + 1,
                sprintStartDateIso = today.toString()
            )
        }

        return VerdictResolutionResult(
            nextState = nextState,
            historySnapshot = snapshot
        )
    }
}
