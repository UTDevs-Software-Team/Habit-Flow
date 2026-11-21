/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box basado en Temporal Motivation Theory (Steel, 2007):
 * Protege la Expectativa limitando a MAX_ACTIVE_GOALS (3) metas activas simultaneas
 * para evitar paralisis de indecision y procrastinacion productiva.
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.MAX_ACTIVE_GOALS

data class ActiveGoalsLimitCheck(
    val activeCount: Int,
    val maxAllowed: Int,
    val canActivateMore: Boolean
)

class ValidateActiveGoalsLimitUseCase {

    operator fun invoke(goals: List<GoalSprint>, excludingGoalId: String? = null): ActiveGoalsLimitCheck {
        val activeCount = goals.count {
            it.goalId != excludingGoalId &&
                (it.status == GoalStatus.ACTIVE || it.status == GoalStatus.MAINTENANCE)
        }
        return ActiveGoalsLimitCheck(
            activeCount = activeCount,
            maxAllowed = MAX_ACTIVE_GOALS,
            canActivateMore = activeCount < MAX_ACTIVE_GOALS
        )
    }
}
