/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para ComputeMetricsAndVerdictUseCase y los 3 tiers del Dia 21.
 */
package com.utdevs.habitflow.domain

import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.model.VerdictTier
import com.utdevs.habitflow.domain.plan.HabitFlow21DayPlan
import com.utdevs.habitflow.domain.usecase.ComputeMetricsAndVerdictUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputeMetricsAndVerdictUseCaseTest {

    private val useCase = ComputeMetricsAndVerdictUseCase(HabitFlow21DayPlan)

    @Test
    fun `17 completed days triggers HIGH_MASTERY`() {
        val state = UserSprintState(completedDays = (1..17).toSet())
        val summary = useCase(state)
        assertEquals(VerdictTier.HIGH_MASTERY, summary.verdictTier)
    }

    @Test
    fun `11 to 16 completed days triggers MODERATE_STABILITY`() {
        val state11 = UserSprintState(completedDays = (1..11).toSet())
        val state16 = UserSprintState(completedDays = (1..16).toSet())
        assertEquals(VerdictTier.MODERATE_STABILITY, useCase(state11).verdictTier)
        assertEquals(VerdictTier.MODERATE_STABILITY, useCase(state16).verdictTier)
    }

    @Test
    fun `10 or fewer completed days triggers RECALIBRATION_NEEDED`() {
        val state10 = UserSprintState(completedDays = (1..10).toSet())
        assertEquals(VerdictTier.RECALIBRATION_NEEDED, useCase(state10).verdictTier)
    }
}
