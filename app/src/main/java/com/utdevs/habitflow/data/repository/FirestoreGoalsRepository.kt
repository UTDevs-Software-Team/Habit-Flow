/**
 * Codigo anadido por Felipe Denis
 *
 * Implementacion Black Box de GoalsRepository sobre Cloud Firestore (users/{uid}/goals/{goalId}).
 * Incluye migracion retrocompatible sin perdida desde users/{uid} hacia goals/goal_primary.
 */
package com.utdevs.habitflow.data.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.utdevs.habitflow.data.mapper.FirestoreMappers
import com.utdevs.habitflow.data.remote.FirestorePaths
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.DayRewardOutcome
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.MaintenanceSessionRecord
import com.utdevs.habitflow.domain.model.SprintHistorySnapshot
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.model.UserSprintState
import com.utdevs.habitflow.domain.repository.GoalsRepository
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class FirestoreGoalsRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : GoalsRepository {

    private fun userDoc(uid: String) =
        db.collection(FirestorePaths.USERS).document(uid)

    private fun goalsCol(uid: String) =
        userDoc(uid).collection(FirestorePaths.GOALS)

    private fun goalDoc(uid: String, goalId: String) =
        goalsCol(uid).document(goalId)

    private fun goalLogsCol(uid: String, goalId: String) =
        goalDoc(uid, goalId).collection(FirestorePaths.DAILY_LOGS)

    private fun goalMaintenanceCol(uid: String, goalId: String) =
        goalDoc(uid, goalId).collection(FirestorePaths.MAINTENANCE_SESSIONS)

    private fun goalHistoryCol(uid: String, goalId: String) =
        goalDoc(uid, goalId).collection(FirestorePaths.SPRINT_HISTORY)

    override fun observeUserProfile(uid: String): Flow<UserProfile> = callbackFlow {
        val reg = userDoc(uid).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            trySend(FirestoreMappers.toUserProfile(snap, uid))
        }
        awaitClose { reg.remove() }
    }

    override fun observeGoals(uid: String): Flow<List<GoalSprint>> = callbackFlow {
        val reg = goalsCol(uid).addSnapshotListener { snap, err ->
            if (err != null) {
                close(err)
                return@addSnapshotListener
            }
            val list = snap?.documents
                ?.mapNotNull { doc -> FirestoreMappers.toGoalSprint(doc, emptyMap(), uid) }
                ?.sortedBy { it.createdAtIso }
                ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    override fun observeGoal(uid: String, goalId: String): Flow<GoalSprint?> {
        val docFlow = callbackFlow {
            val reg = goalDoc(uid, goalId).addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                trySend(snap)
            }
            awaitClose { reg.remove() }
        }
        val logsFlow = callbackFlow {
            val reg = goalLogsCol(uid, goalId).addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val map = snap?.documents
                    ?.mapNotNull { FirestoreMappers.toDailyLogEntry(it) }
                    ?.associateBy { it.day }
                    ?: emptyMap()
                trySend(map)
            }
            awaitClose { reg.remove() }
        }
        return combine(docFlow, logsFlow) { snap, logs ->
            if (snap == null || !snap.exists()) null
            else FirestoreMappers.toGoalSprint(snap, logs, uid)
        }
    }

    override fun observeMaintenanceSessions(
        uid: String,
        goalId: String
    ): Flow<List<MaintenanceSessionRecord>> = callbackFlow {
        val reg = goalMaintenanceCol(uid, goalId)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val list = snap?.documents
                    ?.mapNotNull { FirestoreMappers.toMaintenanceSession(it) }
                    ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun ensureMigrated(uid: String, email: String): Result<Unit> = runCatching {
        val uSnap = userDoc(uid).get().await()
        if (!uSnap.exists()) {
            val initialProfile = UserProfile(
                uid = uid,
                email = email,
                migratedToMultiGoal = true
            )
            userDoc(uid).set(FirestoreMappers.toUserProfileMap(initialProfile), SetOptions.merge()).await()
            return@runCatching
        }

        val alreadyMigrated = uSnap.getBoolean("migratedToMultiGoal") ?: false
        val legacySetupDone = uSnap.getBoolean("setupCompleted") ?: false
        if (alreadyMigrated) return@runCatching

        val batch = db.batch()
        if (legacySetupDone) {
            val legacyLogsSnap = userDoc(uid).collection(FirestorePaths.DAILY_LOGS).get().await()
            val legacyState = FirestoreMappers.toUserSprintState(uSnap, emptyMap())
            if (legacyState != null) {
                val goalId = "goal_primary"
                val goal = GoalSprint(
                    goalId = goalId,
                    title = legacyState.anchorTask.ifBlank { "Meta Principal" },
                    status = if (legacyState.inMaintenanceMode) GoalStatus.MAINTENANCE else GoalStatus.ACTIVE,
                    createdAtIso = Instant.now().toString(),
                    sprint = legacyState.copy(uid = uid)
                )
                batch.set(goalDoc(uid, goalId), FirestoreMappers.toGoalDocumentMap(goal))
                legacyLogsSnap.documents.forEach { logDoc ->
                    logDoc.data?.let { data ->
                        batch.set(goalLogsCol(uid, goalId).document(logDoc.id), data)
                    }
                }
            }
        }

        batch.set(
            userDoc(uid),
            mapOf(
                "uid" to uid,
                "email" to email,
                "migratedToMultiGoal" to true,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        )
        batch.commit().await()
        Unit
    }

    override suspend fun saveNickname(uid: String, email: String, nickname: String): Result<Unit> =
        runCatching {
            userDoc(uid).set(
                mapOf(
                    "uid" to uid,
                    "email" to email,
                    "nickname" to nickname.trim(),
                    "nicknameConfigured" to true,
                    "migratedToMultiGoal" to true,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
            Unit
        }

    override suspend fun createGoal(
        uid: String,
        email: String,
        title: String,
        setup: DayZeroParams,
        startDateIso: String,
        initialStatus: GoalStatus
    ): Result<String> = runCatching {
        val goalId = "goal_${UUID.randomUUID().toString().take(8)}"
        val goal = GoalSprint(
            goalId = goalId,
            title = title.trim(),
            status = initialStatus,
            createdAtIso = Instant.now().toString(),
            sprint = UserSprintState(
                uid = uid,
                email = email,
                setupCompleted = true,
                anchorTask = setup.anchorTask.trim(),
                triggerTime = setup.triggerTime.trim(),
                triggerLocation = setup.triggerLocation.trim(),
                blockingRule = setup.blockingRule.trim(),
                sprintStartDateIso = startDateIso,
                currentDay = 1
            )
        )
        goalDoc(uid, goalId).set(FirestoreMappers.toGoalDocumentMap(goal)).await()
        goalId
    }

    override suspend fun setGoalStatus(uid: String, goalId: String, status: GoalStatus): Result<Unit> =
        runCatching {
            goalDoc(uid, goalId).set(
                mapOf(
                    "status" to status.name,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            ).await()
            Unit
        }

    override suspend fun updateGoalParams(
        uid: String,
        goalId: String,
        title: String,
        setup: DayZeroParams
    ): Result<Unit> = runCatching {
        goalDoc(uid, goalId).set(
            mapOf(
                "title" to title.trim(),
                "anchorTask" to setup.anchorTask.trim(),
                "triggerTime" to setup.triggerTime.trim(),
                "triggerLocation" to setup.triggerLocation.trim(),
                "blockingRule" to setup.blockingRule.trim(),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
        Unit
    }

    override suspend fun saveCompletedDayWithReward(
        uid: String,
        goalId: String,
        log: DailyLogEntry,
        nextDay: Int,
        reward: DayRewardOutcome,
        newGlobalStreak: Int,
        newBestStreak: Int,
        newGoalStreak: Int,
        newShields: Int
    ): Result<Unit> = runCatching {
        val batch = db.batch()
        batch.set(goalLogsCol(uid, goalId).document(log.day.toString()), FirestoreMappers.toDailyLogMap(log))

        batch.set(
            goalDoc(uid, goalId),
            mapOf(
                "completedDays" to FieldValue.arrayUnion(log.day),
                "currentDay" to nextDay.coerceIn(1, 21),
                "nextMicroTask" to log.closureTask.trim(),
                "goalStreak" to newGoalStreak,
                "goalXp" to FieldValue.increment(reward.totalXpEarned.toLong()),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        )

        val profileUpdates = mutableMapOf<String, Any>(
            "uid" to uid,
            "totalXp" to reward.newTotalXp,
            "currentRank" to reward.newRank,
            "globalStreak" to newGlobalStreak,
            "bestStreak" to newBestStreak,
            "streakShields" to newShields,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (reward.newlyUnlockedAchievements.isNotEmpty()) {
            val ids = reward.newlyUnlockedAchievements.map { it.id }.toTypedArray()
            profileUpdates["unlockedAchievements"] = FieldValue.arrayUnion(*ids)
        }
        batch.set(userDoc(uid), profileUpdates, SetOptions.merge())
        batch.commit().await()
        Unit
    }

    override suspend fun markDaysMissed(
        uid: String,
        goalId: String,
        missedDays: Set<Int>,
        newCurrentDay: Int,
        shieldConsumed: Boolean,
        remainingShields: Int
    ): Result<Unit> = runCatching {
        if (missedDays.isEmpty()) return@runCatching
        val batch = db.batch()
        missedDays.forEach { day ->
            val log = DailyLogEntry(day = day, completed = false, missed = true)
            batch.set(goalLogsCol(uid, goalId).document(day.toString()), FirestoreMappers.toDailyLogMap(log))
        }
        val goalUpdates = mutableMapOf<String, Any>(
            "missedDays" to FieldValue.arrayUnion(*missedDays.toTypedArray()),
            "currentDay" to newCurrentDay.coerceIn(1, 21),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!shieldConsumed) {
            goalUpdates["goalStreak"] = 0
        }
        batch.set(goalDoc(uid, goalId), goalUpdates, SetOptions.merge())

        if (shieldConsumed) {
            batch.set(
                userDoc(uid),
                mapOf("streakShields" to remainingShields, "updatedAt" to FieldValue.serverTimestamp()),
                SetOptions.merge()
            )
        }
        batch.commit().await()
        Unit
    }

    override suspend fun applyVerdictTransition(
        uid: String,
        goalId: String,
        newState: UserSprintState,
        newGoalStatus: GoalStatus,
        archiveSnapshot: SprintHistorySnapshot?
    ): Result<Unit> = runCatching {
        val existingLogs = goalLogsCol(uid, goalId).get().await()
        val batch = db.batch()

        if (archiveSnapshot != null) {
            batch.set(
                goalHistoryCol(uid, goalId).document("cycle_${archiveSnapshot.cycleNumber}"),
                FirestoreMappers.toHistorySnapshotMap(archiveSnapshot)
            )
        }

        existingLogs.documents.forEach { doc ->
            val dayNum = doc.id.toIntOrNull()
            if (dayNum != null && !newState.dailyLogs.containsKey(dayNum)) {
                batch.delete(doc.reference)
            }
        }

        val updates = FirestoreMappers.toUserDocumentMap(newState).toMutableMap()
        updates["status"] = newGoalStatus.name
        batch.set(goalDoc(uid, goalId), updates, SetOptions.merge())
        batch.commit().await()
        Unit
    }

    override suspend fun updateSecondaryAnchorTask(
        uid: String,
        goalId: String,
        secondaryTask: String
    ): Result<Unit> = runCatching {
        goalDoc(uid, goalId).set(
            mapOf(
                "secondaryAnchorTask" to secondaryTask.trim(),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
        Unit
    }

    override suspend fun saveMaintenanceSession(
        uid: String,
        goalId: String,
        record: MaintenanceSessionRecord
    ): Result<Unit> = runCatching {
        goalMaintenanceCol(uid, goalId).add(FirestoreMappers.toMaintenanceSessionMap(record)).await()
        Unit
    }

    override suspend fun setMaintenanceModeActive(
        uid: String,
        goalId: String,
        active: Boolean
    ): Result<Unit> = runCatching {
        val status = if (active) GoalStatus.MAINTENANCE else GoalStatus.ACTIVE
        goalDoc(uid, goalId).set(
            mapOf(
                "inMaintenanceMode" to active,
                "status" to status.name,
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        ).await()
        Unit
    }

    override suspend fun seedDemoScenario(
        uid: String,
        goalId: String,
        goal: GoalSprint
    ): Result<Unit> = runCatching {
        val existingLogs = goalLogsCol(uid, goalId).get().await()
        val batch = db.batch()
        existingLogs.documents.forEach { batch.delete(it.reference) }
        goal.sprint.dailyLogs.values.forEach { log ->
            batch.set(goalLogsCol(uid, goalId).document(log.day.toString()), FirestoreMappers.toDailyLogMap(log))
        }
        batch.set(goalDoc(uid, goalId), FirestoreMappers.toGoalDocumentMap(goal))
        batch.commit().await()
        Unit
    }

    override suspend fun resetGoalProgress(uid: String, goalId: String): Result<Unit> = runCatching {
        val logs = goalLogsCol(uid, goalId).get().await()
        val maint = goalMaintenanceCol(uid, goalId).get().await()
        val batch = db.batch()
        logs.documents.forEach { batch.delete(it.reference) }
        maint.documents.forEach { batch.delete(it.reference) }
        batch.delete(goalDoc(uid, goalId))
        batch.commit().await()
        Unit
    }
}
