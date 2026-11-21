/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box que integra:
 * - Schultz (2016) RPE positivo (Bono Sorpresa acotado + refuerzo al disparador).
 * - Sirois & Pychyl (2013) Cero Culpa (Bono Fenix al retomar tras dia omitido).
 * - Deteccion de subida de nivel de planta o rango para disparar vibracion larga + confeti.
 */
package com.utdevs.habitflow.domain.dopamine

import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.DayRewardOutcome
import com.utdevs.habitflow.domain.model.DopamineCatalog
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.usecase.ComputePlantStageUseCase

class ComputeDayRewardUseCase(
    private val computePlantStageUseCase: ComputePlantStageUseCase,
    private val evaluateAchievementsUseCase: EvaluateAchievementsUseCase
) {

    /**
     * @param randomRoll Valor [0..99] inyectable para pruebas deterministicas de Bono Sorpresa RPE.
     */
    operator fun invoke(
        catalog: DopamineCatalog,
        profile: UserProfile,
        goalBeforeDay: GoalSprint,
        completedLog: DailyLogEntry,
        newGoalStreak: Int,
        randomRoll: Int = (0..99).random()
    ): DayRewardOutcome {
        val rules = catalog.xpRules
        val prepXp = if (completedLog.checks.checkPrep) rules.prepCheckXp else 0
        val punctualXp = if (completedLog.punctual) rules.punctualStartXp else 0
        val minuteXp = completedLog.effectiveFocusMinutes * rules.xpPerFocusMinute
        val baseAndMinute = rules.dayCompletionBaseXp + prepXp + punctualXp + minuteXp

        val prevDay = completedLog.day - 1
        val isPhoenixRecovery = prevDay >= 1 && prevDay in goalBeforeDay.sprint.missedDays
        val phoenixXp = if (isPhoenixRecovery) rules.phoenixRecoveryBonusXp else 0

        val isSurpriseRpe = randomRoll in 0 until rules.surpriseRpeChancePercent
        val surpriseXp = if (isSurpriseRpe) rules.surpriseRpeBonusXp else 0

        val candidates = evaluateAchievementsUseCase(
            catalog = catalog,
            goalBeforeDay = goalBeforeDay,
            completedLog = completedLog,
            isPhoenixRecovery = isPhoenixRecovery
        ).filter { it.id !in profile.unlockedAchievements }
        val achievementXp = candidates.sumOf { it.bonusXp }

        val totalEarned = baseAndMinute + phoenixXp + surpriseXp + achievementXp
        val newTotalXp = profile.totalXp + totalEarned

        val previousRank = rankForXp(catalog, profile.totalXp)
        val newRank = rankForXp(catalog, newTotalXp)

        val prevCount = goalBeforeDay.sprint.completedDays.size
        val nextCount = (goalBeforeDay.sprint.completedDays + completedLog.day).size
        val prevStage = computePlantStageUseCase(prevCount)
        val newStage = computePlantStageUseCase(nextCount)

        val leveledUp = (newStage.stageNumber > prevStage.stageNumber) || (newRank != previousRank)
        val shieldEarned = newGoalStreak > 0 &&
            (newGoalStreak % rules.streakDaysPerShield == 0) &&
            profile.streakShields < rules.maxShields

        val message = when {
            isPhoenixRecovery ->
                catalog.messages.onRecoveryAfterMiss.firstOrNull()
                    ?: "Efecto Fenix activado: retomaste el habito sin culpa."
            isSurpriseRpe ->
                catalog.messages.onSurpriseRpe.firstOrNull()
                    ?: "Bono Sorpresa RPE Positivo (+15 XP) desbloqueado."
            completedLog.punctual ->
                catalog.messages.onPunctualStart.firstOrNull()
                    ?: "Arranque puntual y sesion completada."
            else -> "Dia ${completedLog.day} completado. Cada bloque refuerza tu circuito."
        }

        return DayRewardOutcome(
            baseAndMinuteXp = baseAndMinute,
            phoenixBonusXp = phoenixXp,
            surpriseRpeBonusXp = surpriseXp,
            achievementBonusXp = achievementXp,
            totalXpEarned = totalEarned,
            newTotalXp = newTotalXp,
            previousRank = previousRank,
            newRank = newRank,
            previousPlantStage = prevStage,
            newPlantStage = newStage,
            leveledUp = leveledUp,
            shieldEarned = shieldEarned,
            newlyUnlockedAchievements = candidates,
            rewardMessage = message
        )
    }

    fun rankForXp(catalog: DopamineCatalog, xp: Int): String =
        catalog.ranks
            .sortedBy { it.minXp }
            .lastOrNull { xp >= it.minXp }
            ?.title
            ?: "Semilla Latente"
}
