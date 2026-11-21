/**
 * Codigo anadido por Felipe Denis
 *
 * Modelos Data-Driven para el Motor de Dopamina, salud de planta y logros conductuales.
 */
package com.utdevs.habitflow.domain.model

enum class PlantHealthState {
    THRIVING,
    NEEDS_WATER,
    RESTORED
}

data class XpRules(
    val prepCheckXp: Int = 5,
    val punctualStartXp: Int = 10,
    val dayCompletionBaseXp: Int = 25,
    val xpPerFocusMinute: Int = 1,
    val phoenixRecoveryBonusXp: Int = 20,
    val surpriseRpeBonusXp: Int = 15,
    val surpriseRpeChancePercent: Int = 30,
    val streakDaysPerShield: Int = 5,
    val maxShields: Int = 2
)

data class RankSpec(
    val minXp: Int,
    val title: String
)

data class AchievementSpec(
    val id: String,
    val title: String,
    val description: String,
    val bonusXp: Int
)

data class DopamineMessages(
    val onPunctualStart: List<String> = emptyList(),
    val onMidBlock: List<String> = emptyList(),
    val onRecoveryAfterMiss: List<String> = emptyList(),
    val onSurpriseRpe: List<String> = emptyList()
)

data class DopamineCatalog(
    val xpRules: XpRules = XpRules(),
    val ranks: List<RankSpec> = listOf(
        RankSpec(0, "Semilla Latente"),
        RankSpec(100, "Brote Constante"),
        RankSpec(300, "Tallo Firme"),
        RankSpec(600, "Copa en Flor"),
        RankSpec(1000, "Roble Autonomo"),
        RankSpec(1500, "Guardian del Bosque")
    ),
    val achievements: List<AchievementSpec> = emptyList(),
    val messages: DopamineMessages = DopamineMessages()
)

data class DayRewardOutcome(
    val baseAndMinuteXp: Int,
    val phoenixBonusXp: Int,
    val surpriseRpeBonusXp: Int,
    val achievementBonusXp: Int,
    val totalXpEarned: Int,
    val newTotalXp: Int,
    val previousRank: String,
    val newRank: String,
    val previousPlantStage: PlantStage,
    val newPlantStage: PlantStage,
    val leveledUp: Boolean,
    val shieldEarned: Boolean,
    val newlyUnlockedAchievements: List<AchievementSpec>,
    val rewardMessage: String
)
