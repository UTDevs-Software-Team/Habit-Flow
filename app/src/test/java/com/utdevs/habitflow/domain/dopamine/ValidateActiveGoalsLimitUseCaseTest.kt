/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para ValidateActiveGoalsLimitUseCase (maximo 3 metas activas).
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateActiveGoalsLimitUseCaseTest {

    private val useCase = ValidateActiveGoalsLimitUseCase()

    @Test
    fun `allows up to 3 active goals and blocks the 4th`() {
        val twoActiveOnePaused = listOf(
            GoalSprint(goalId = "1", status = GoalStatus.ACTIVE),
            GoalSprint(goalId = "2", status = GoalStatus.MAINTENANCE),
            GoalSprint(goalId = "3", status = GoalStatus.PAUSED)
        )
        assertTrue(useCase(twoActiveOnePaused).canActivateMore)

        val threeActive = listOf(
            GoalSprint(goalId = "1", status = GoalStatus.ACTIVE),
            GoalSprint(goalId = "2", status = GoalStatus.ACTIVE),
            GoalSprint(goalId = "3", status = GoalStatus.MAINTENANCE)
        )
        assertFalse(useCase(threeActive).canActivateMore)
    }
}
