/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box basado en Sirois & Pychyl (2013):
 * Determina el estado de salud visual de la planta sin culpa punitiva.
 * Si el ultimo dia previo fue MISSED y el actual aun no termina -> NEEDS_WATER (arcilla recuperable).
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.UserSprintState

class EvaluatePlantHealthUseCase {

    operator fun invoke(sprint: UserSprintState, justCompletedToday: Boolean = false): PlantHealthState {
        val current = sprint.currentDay.coerceIn(1, 21)
        val hasRecentMiss = (current - 1) in sprint.missedDays
        return when {
            justCompletedToday && hasRecentMiss -> PlantHealthState.RESTORED
            hasRecentMiss && current !in sprint.completedDays -> PlantHealthState.NEEDS_WATER
            else -> PlantHealthState.THRIVING
        }
    }
}
