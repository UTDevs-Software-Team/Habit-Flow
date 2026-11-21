/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Evalua logros conductuales desbloqueados tras cerrar una sesion diaria.
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.AchievementSpec
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.DopamineCatalog
import com.utdevs.habitflow.domain.model.GoalSprint

class EvaluateAchievementsUseCase {

    operator fun invoke(
        catalog: DopamineCatalog,
        goalBeforeDay: GoalSprint,
        completedLog: DailyLogEntry,
        isPhoenixRecovery: Boolean
    ): List<AchievementSpec> {
        val newlyCompleted = goalBeforeDay.sprint.completedDays + completedLog.day
        val unlockedIds = mutableSetOf<String>()

        if (completedLog.day == 1) unlockedIds.add("first_spark")
        if (completedLog.day == 9 && completedLog.punctual) unlockedIds.add("punctual_sniper")
        if (isPhoenixRecovery) unlockedIds.add("phoenix_return")
        if ((1..7).all { it in newlyCompleted }) unlockedIds.add("week_one_survivor")
        if (completedLog.day == 15) unlockedIds.add("deep_diver")
        if (completedLog.day == 21 && (newlyCompleted.size / 21.0) >= 0.80) {
            unlockedIds.add("sprint_master")
        }

        return catalog.achievements.filter { it.id in unlockedIds }
    }
}
