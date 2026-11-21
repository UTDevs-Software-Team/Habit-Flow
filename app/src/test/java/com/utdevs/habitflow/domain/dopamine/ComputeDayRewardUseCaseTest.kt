/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para ComputeDayRewardUseCase (RPE Positivo, Fenix, subida de nivel y escudos).
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.AchievementSpec
import com.utdevs.habitflow.domain.model.DailyCheckState
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.DopamineCatalog
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.PlantStage
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.usecase.ComputePlantStageUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeDayRewardUseCaseTest {

    private val catalog = DopamineCatalog(
        achievements = listOf(
            AchievementSpec("first_spark", "Primera Chispa", "Dia 1", 15),
            AchievementSpec("phoenix_return", "Efecto Fenix", "Retomar", 20)
        )
    )
    private val useCase = ComputeDayRewardUseCase(
        computePlantStageUseCase = ComputePlantStageUseCase(),
        evaluateAchievementsUseCase = EvaluateAchievementsUseCase()
    )

    @Test
    fun `completing day 4 triggers plant level up from SEED to SPROUT`() {
        val goal = GoalSprint(
            goalId = "g1",
            sprint = UserSprintState(completedDays = setOf(1, 2, 3), currentDay = 4)
        )
        val log = DailyLogEntry(
            day = 4,
            completed = true,
            punctual = true,
            effectiveFocusMinutes = 15,
            checks = DailyCheckState(true, true, true)
        )

        val outcome = useCase(
            catalog = catalog,
            profile = UserProfile(totalXp = 50),
            goalBeforeDay = goal,
            completedLog = log,
            newGoalStreak = 4,
            randomRoll = 90 // sin bono sorpresa
        )

        assertEquals(PlantStage.SEED, outcome.previousPlantStage)
        assertEquals(PlantStage.SPROUT, outcome.newPlantStage)
        assertTrue(outcome.leveledUp)
        assertEquals(0, outcome.surpriseRpeBonusXp)
    }

    @Test
    fun `completing day after missed day grants phoenix bonus and surprise RPE when roll under 30`() {
        val goal = GoalSprint(
            goalId = "g1",
            sprint = UserSprintState(completedDays = setOf(1), missedDays = setOf(2), currentDay = 3)
        )
        val log = DailyLogEntry(
            day = 3,
            completed = true,
            punctual = false,
            effectiveFocusMinutes = 10,
            checks = DailyCheckState(true, true, true)
        )

        val outcome = useCase(
            catalog = catalog,
            profile = UserProfile(totalXp = 150),
            goalBeforeDay = goal,
            completedLog = log,
            newGoalStreak = 5,
            randomRoll = 10 // activa RPE sorpresa (<30)
        )

        assertEquals(20, outcome.phoenixBonusXp)
        assertEquals(15, outcome.surpriseRpeBonusXp)
        assertTrue(outcome.shieldEarned)
        assertFalse(outcome.leveledUp)
    }
}
