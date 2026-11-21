/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Calcula KPIs objetivos, comparativa por fase y el veredicto del Dia 21.
 */
package com.utdevs.habitflow.domain.usecase

import com.utdevs.habitflow.domain.model.PhaseComparisonRow
import com.utdevs.habitflow.domain.model.SprintMetricsSummary
import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.model.VerdictTier
import com.utdevs.habitflow.domain.repository.PlanDataSource
import kotlin.math.ceil

class ComputeMetricsAndVerdictUseCase(
    private val planDataSource: PlanDataSource
) {

    operator fun invoke(state: UserSprintState): SprintMetricsSummary {
        val phases = planDataSource.getPhases()
        val completedCount = state.completedDays.size
        val compliancePct = (completedCount / 21.0) * 100.0

        val accumulatedMinutes = state.completedDays.sumOf { day ->
            val logged = state.dailyLogs[day]?.effectiveFocusMinutes
            if (logged != null && logged > 0) {
                logged
            } else {
                val base = planDataSource.getDaySpec(day).focusMinutes
                ceil(base * state.durationFactor).toInt().coerceAtLeast(1)
            }
        }

        val targetTotalMinutes = ceil(
            planDataSource.getTotalTargetMinutes() * state.durationFactor
        ).toInt().coerceAtLeast(1)

        val punctualCount = state.completedDays.count { day ->
            state.dailyLogs[day]?.punctual == true
        }
        val punctualityPct = if (completedCount == 0) {
            0.0
        } else {
            (punctualCount.toDouble() / completedCount.toDouble()) * 100.0
        }

        val phaseRows = phases.map { phase ->
            val phaseDayNumbers = phase.days.map { it.day }.toSet()
            val doneInPhase = state.completedDays.intersect(phaseDayNumbers)
            val minsInPhase = doneInPhase.sumOf { day ->
                val logged = state.dailyLogs[day]?.effectiveFocusMinutes
                if (logged != null && logged > 0) {
                    logged
                } else {
                    ceil(planDataSource.getDaySpec(day).focusMinutes * state.durationFactor)
                        .toInt()
                        .coerceAtLeast(1)
                }
            }
            val targetPhaseMins = ceil(phase.targetFocusMinutes * state.durationFactor)
                .toInt()
                .coerceAtLeast(1)

            PhaseComparisonRow(
                phaseId = phase.phaseId,
                phaseName = phase.name,
                completedDays = doneInPhase.size,
                totalDays = phase.days.size,
                accumulatedMinutes = minsInPhase,
                targetMinutes = targetPhaseMins
            )
        }

        val tier = when {
            compliancePct >= 80.0 -> VerdictTier.HIGH_MASTERY
            compliancePct >= 50.0 -> VerdictTier.MODERATE_STABILITY
            else -> VerdictTier.RECALIBRATION_NEEDED
        }

        val (title, description) = when (tier) {
            VerdictTier.HIGH_MASTERY ->
                "Dominio Alto: Habito Base Instalado" to
                    "Superaste el umbral del 80%. Puedes activar el Modo Mantenimiento (45/10 o 50/10) y agregar una segunda Tarea Ancla."
            VerdictTier.MODERATE_STABILITY ->
                "Estabilidad Moderada: Refuerzo Requerido" to
                    "Cumplimiento entre 50% y 79%. El sistema recomienda repetir unicamente la Fase 3 (Dias 15 al 21) antes de graduar el habito."
            VerdictTier.RECALIBRATION_NEEDED ->
                "Recalibracion Necesaria: Sobrecarga Detectada" to
                    "Cumplimiento menor al 50%. El sistema reiniciara en el Dia 1 reduciendo automaticamente todos los bloques al 50% (factor 0.5x)."
        }

        return SprintMetricsSummary(
            completedDaysCount = completedCount,
            compliancePercentage = compliancePct,
            accumulatedFocusMinutes = accumulatedMinutes,
            targetTotalMinutes = targetTotalMinutes,
            punctualityPercentage = punctualityPct,
            completedSessionsCount = completedCount,
            phaseComparison = phaseRows,
            verdictTier = tier,
            recommendedActionTitle = title,
            recommendedActionDescription = description
        )
    }
}
