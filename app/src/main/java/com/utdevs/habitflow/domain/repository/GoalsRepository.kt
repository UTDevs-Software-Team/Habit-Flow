/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box Multi-Meta para perfil global, metas independientes y economia de dopamina.
 */
package com.utdevs.habitflow.domain.repository

import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.DayRewardOutcome
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.MaintenanceSessionRecord
import com.utdevs.habitflow.domain.model.SprintHistorySnapshot
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.model.UserSprintState
import kotlinx.coroutines.flow.Flow

interface GoalsRepository {
    fun observeUserProfile(uid: String): Flow<UserProfile>
    fun observeGoals(uid: String): Flow<List<GoalSprint>>
    fun observeGoal(uid: String, goalId: String): Flow<GoalSprint?>
    fun observeMaintenanceSessions(uid: String, goalId: String): Flow<List<MaintenanceSessionRecord>>

    suspend fun ensureMigrated(uid: String, email: String): Result<Unit>
    suspend fun saveNickname(uid: String, email: String, nickname: String): Result<Unit>

    suspend fun createGoal(
        uid: String,
        email: String,
        title: String,
        setup: DayZeroParams,
        startDateIso: String,
        initialStatus: GoalStatus
    ): Result<String>

    suspend fun setGoalStatus(uid: String, goalId: String, status: GoalStatus): Result<Unit>
    suspend fun updateGoalParams(uid: String, goalId: String, title: String, setup: DayZeroParams): Result<Unit>

    suspend fun saveCompletedDayWithReward(
        uid: String,
        goalId: String,
        log: DailyLogEntry,
        nextDay: Int,
        reward: DayRewardOutcome,
        newGlobalStreak: Int,
        newBestStreak: Int,
        newGoalStreak: Int,
        newShields: Int
    ): Result<Unit>

    suspend fun markDaysMissed(
        uid: String,
        goalId: String,
        missedDays: Set<Int>,
        newCurrentDay: Int,
        shieldConsumed: Boolean,
        remainingShields: Int
    ): Result<Unit>

    suspend fun applyVerdictTransition(
        uid: String,
        goalId: String,
        newState: UserSprintState,
        newGoalStatus: GoalStatus,
        archiveSnapshot: SprintHistorySnapshot?
    ): Result<Unit>

    suspend fun updateSecondaryAnchorTask(uid: String, goalId: String, secondaryTask: String): Result<Unit>
    suspend fun saveMaintenanceSession(uid: String, goalId: String, record: MaintenanceSessionRecord): Result<Unit>
    suspend fun setMaintenanceModeActive(uid: String, goalId: String, active: Boolean): Result<Unit>
    suspend fun seedDemoScenario(uid: String, goalId: String, goal: GoalSprint): Result<Unit>
    suspend fun resetGoalProgress(uid: String, goalId: String): Result<Unit>
}
