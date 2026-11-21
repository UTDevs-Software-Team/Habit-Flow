/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para ResolveDayStatesUseCase (bloqueo hacia adelante y dias omitidos).
 */
package com.utdevs.habitflow.domain

import com.utdevs.habitflow.domain.model.DayStatus
import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.usecase.ResolveDayStatesUseCase
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ResolveDayStatesUseCaseTest {

    private val useCase = ResolveDayStatesUseCase()

    @Test
    fun `elapsed calendar days without completion become MISSED and advance active day`() {
        val start = LocalDate.of(2026, 11, 1)
        val today = LocalDate.of(2026, 11, 4) // dia 4 del calendario
        val state = UserSprintState(
            setupCompleted = true,
            sprintStartDateIso = start.toString(),
            currentDay = 2,
            completedDays = setOf(1)
        )

        val outcome = useCase(state, today)
        assertEquals(setOf(2, 3), outcome.newlyMissedDays)
        assertEquals(4, outcome.effectiveCurrentDay)
        assertEquals(DayStatus.COMPLETED, outcome.statuses[1])
        assertEquals(DayStatus.MISSED, outcome.statuses[2])
        assertEquals(DayStatus.MISSED, outcome.statuses[3])
        assertEquals(DayStatus.ACTIVE, outcome.statuses[4])
        assertEquals(DayStatus.LOCKED, outcome.statuses[5])
    }
}
