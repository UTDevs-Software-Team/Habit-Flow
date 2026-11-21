/**
 * Codigo anadido por Felipe Denis
 *
 * Modelos de perfil global y Multi-Meta (maximo 3 metas activas simultaneas para evitar
 * paralisis de indecision segun Temporal Motivation Theory).
 */
package com.utdevs.habitflow.domain.model

const val MAX_ACTIVE_GOALS = 3

enum class GoalStatus {
    ACTIVE,
    PAUSED,
    MAINTENANCE
}

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val nickname: String = "",
    val nicknameConfigured: Boolean = false,
    val totalXp: Int = 0,
    val currentRank: String = "Semilla Latente",
    val globalStreak: Int = 0,
    val bestStreak: Int = 0,
    val streakShields: Int = 0,
    val unlockedAchievements: Set<String> = emptySet(),
    val migratedToMultiGoal: Boolean = false
)

data class GoalSprint(
    val goalId: String = "",
    val title: String = "",
    val status: GoalStatus = GoalStatus.ACTIVE,
    val goalStreak: Int = 0,
    val goalXp: Int = 0,
    val createdAtIso: String = "",
    val sprint: UserSprintState = UserSprintState()
)
