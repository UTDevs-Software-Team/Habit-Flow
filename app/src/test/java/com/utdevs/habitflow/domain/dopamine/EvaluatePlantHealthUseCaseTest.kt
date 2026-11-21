/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para EvaluatePlantHealthUseCase (estado recuperable Cero Culpa).
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.UserSprintState
import org.junit.Assert.assertEquals
import org.junit.Test

class EvaluatePlantHealthUseCaseTest {

    private val useCase = EvaluatePlantHealthUseCase()

    @Test
    fun `missed previous day puts plant in NEEDS_WATER then RESTORED on completion`() {
        val sprint = UserSprintState(currentDay = 4, completedDays = setOf(1, 2), missedDays = setOf(3))
        assertEquals(PlantHealthState.NEEDS_WATER, useCase(sprint, justCompletedToday = false))
        assertEquals(PlantHealthState.RESTORED, useCase(sprint, justCompletedToday = true))
    }
}
