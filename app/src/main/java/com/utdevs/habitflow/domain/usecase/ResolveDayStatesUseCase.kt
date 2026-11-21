/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Resuelve el estado operativo (COMPLETED, ACTIVE, MISSED, LOCKED)
 * de los 21 dias manteniendo bloqueo estricto hacia adelante y sincronia con fecha de inicio.
 */
package com.utdevs.habitflow.domain.usecase

import com.utdevs.habitflow.domain.model.DayStatus
import com.utdevs.habitflow.domain.model.UserSprintState
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DayResolutionOutcome(
    val statuses: Map<Int, DayStatus>,
    val newlyMissedDays: Set<Int>,
    val effectiveCurrentDay: Int
)

class ResolveDayStatesUseCase {

    /**
     * @param state Estado actual del sprint del usuario.
     * @param today Fecha local actual para detectar dias vencidos sin completar.
     */
    operator fun invoke(
        state: UserSprintState,
        today: LocalDate = LocalDate.now()
    ): DayResolutionOutcome {
        val startDate = runCatching { LocalDate.parse(state.sprintStartDateIso) }.getOrNull()
        val calendarDayIndex = if (startDate != null) {
            (ChronoUnit.DAYS.between(startDate, today).toInt() + 1).coerceIn(1, 21)
        } else {
            state.currentDay.coerceIn(1, 21)
        }

        val minDayAllowed = state.activeRepeatRangeStart ?: 1
        val targetBoundary = maxOf(state.currentDay, calendarDayIndex).coerceIn(1, 21)

        val newlyMissed = mutableSetOf<Int>()
        for (day in minDayAllowed until targetBoundary) {
            if (day !in state.completedDays && day !in state.missedDays) {
                newlyMissed.add(day)
            }
        }

        val allMissed = state.missedDays + newlyMissed
        val nextActive = (minDayAllowed..21).firstOrNull { day ->
            day !in state.completedDays && day !in allMissed
        } ?: 21

        val statuses = (1..21).associateWith { day ->
            when {
                day in state.completedDays -> DayStatus.COMPLETED
                day in allMissed -> DayStatus.MISSED
                day == nextActive -> DayStatus.ACTIVE
                else -> DayStatus.LOCKED
            }
        }

        return DayResolutionOutcome(
            statuses = statuses,
            newlyMissedDays = newlyMissed,
            effectiveCurrentDay = nextActive
        )
    }
}
