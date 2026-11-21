/**
 * Codigo anadido por Felipe Denis
 *
 * Modelos para metricas objetivas, comparativa por fase y veredicto automatico del Dia 21.
 */
package com.utdevs.habitflow.domain.model

enum class VerdictTier {
    HIGH_MASTERY,
    MODERATE_STABILITY,
    RECALIBRATION_NEEDED
}

data class PhaseComparisonRow(
    val phaseId: Int,
    val phaseName: String,
    val completedDays: Int,
    val totalDays: Int,
    val accumulatedMinutes: Int,
    val targetMinutes: Int
)

data class SprintMetricsSummary(
    val completedDaysCount: Int,
    val compliancePercentage: Double,
    val accumulatedFocusMinutes: Int,
    val targetTotalMinutes: Int,
    val punctualityPercentage: Double,
    val completedSessionsCount: Int,
    val phaseComparison: List<PhaseComparisonRow>,
    val verdictTier: VerdictTier,
    val recommendedActionTitle: String,
    val recommendedActionDescription: String
)

data class SprintHistorySnapshot(
    val cycleNumber: Int,
    val compliancePercentage: Double,
    val verdictTier: VerdictTier,
    val totalMinutes: Int,
    val archivedAtIso: String
)

data class VerdictResolutionResult(
    val nextState: UserSprintState,
    val historySnapshot: SprintHistorySnapshot?
)
