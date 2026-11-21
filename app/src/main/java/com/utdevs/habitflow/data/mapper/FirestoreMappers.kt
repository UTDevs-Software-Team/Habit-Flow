/**
 * Codigo anadido por Felipe Denis
 *
 * Mappers puros entre Cloud Firestore (users/{uid} y users/{uid}/goals/{goalId})
 * y los modelos de dominio.
 */
package com.utdevs.habitflow.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.utdevs.habitflow.domain.model.DailyCheckState
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.MaintenanceSessionRecord
import com.utdevs.habitflow.domain.model.SprintHistorySnapshot
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.model.UserSprintState

object FirestoreMappers {

    fun toUserProfile(doc: DocumentSnapshot?, fallbackUid: String): UserProfile {
        if (doc == null || !doc.exists()) {
            return UserProfile(uid = fallbackUid)
        }
        val achievements = (doc.get("unlockedAchievements") as? List<*>)
            ?.mapNotNull { it as? String }
            ?.toSet()
            ?: emptySet()

        return UserProfile(
            uid = doc.getString("uid") ?: doc.id,
            email = doc.getString("email") ?: "",
            nickname = doc.getString("nickname") ?: "",
            nicknameConfigured = doc.getBoolean("nicknameConfigured") ?: false,
            totalXp = (doc.getLong("totalXp") ?: 0L).toInt(),
            currentRank = doc.getString("currentRank") ?: "Semilla Latente",
            globalStreak = (doc.getLong("globalStreak") ?: 0L).toInt(),
            bestStreak = (doc.getLong("bestStreak") ?: 0L).toInt(),
            streakShields = (doc.getLong("streakShields") ?: 0L).toInt(),
            unlockedAchievements = achievements,
            migratedToMultiGoal = doc.getBoolean("migratedToMultiGoal") ?: false
        )
    }

    fun toUserProfileMap(profile: UserProfile): Map<String, Any?> = mapOf(
        "uid" to profile.uid,
        "email" to profile.email,
        "nickname" to profile.nickname,
        "nicknameConfigured" to profile.nicknameConfigured,
        "totalXp" to profile.totalXp,
        "currentRank" to profile.currentRank,
        "globalStreak" to profile.globalStreak,
        "bestStreak" to profile.bestStreak,
        "streakShields" to profile.streakShields,
        "unlockedAchievements" to profile.unlockedAchievements.sorted(),
        "migratedToMultiGoal" to profile.migratedToMultiGoal,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toGoalSprint(
        doc: DocumentSnapshot,
        logs: Map<Int, DailyLogEntry>,
        ownerUid: String
    ): GoalSprint? {
        if (!doc.exists()) return null
        val sprintState = toUserSprintState(doc, logs)?.copy(uid = ownerUid) ?: return null
        val rawStatus = doc.getString("status") ?: GoalStatus.ACTIVE.name
        val status = runCatching { GoalStatus.valueOf(rawStatus) }.getOrDefault(GoalStatus.ACTIVE)

        return GoalSprint(
            goalId = doc.id,
            title = doc.getString("title") ?: sprintState.anchorTask.ifBlank { "Meta Principal" },
            status = status,
            goalStreak = (doc.getLong("goalStreak") ?: 0L).toInt(),
            goalXp = (doc.getLong("goalXp") ?: 0L).toInt(),
            createdAtIso = doc.getString("createdAtIso") ?: "",
            sprint = sprintState
        )
    }

    fun toGoalDocumentMap(goal: GoalSprint): Map<String, Any?> {
        val base = toUserDocumentMap(goal.sprint).toMutableMap()
        base["goalId"] = goal.goalId
        base["title"] = goal.title
        base["status"] = goal.status.name
        base["goalStreak"] = goal.goalStreak
        base["goalXp"] = goal.goalXp
        base["createdAtIso"] = goal.createdAtIso
        return base
    }

    fun toUserSprintState(
        doc: DocumentSnapshot,
        logs: Map<Int, DailyLogEntry>
    ): UserSprintState? {
        if (!doc.exists()) return null
        val completed = (doc.get("completedDays") as? List<*>)
            ?.mapNotNull { (it as? Number)?.toInt() }
            ?.toSet()
            ?: emptySet()
        val missed = (doc.get("missedDays") as? List<*>)
            ?.mapNotNull { (it as? Number)?.toInt() }
            ?.toSet()
            ?: emptySet()

        return UserSprintState(
            uid = doc.getString("uid") ?: doc.id,
            email = doc.getString("email") ?: "",
            setupCompleted = doc.getBoolean("setupCompleted") ?: false,
            anchorTask = doc.getString("anchorTask") ?: "",
            secondaryAnchorTask = doc.getString("secondaryAnchorTask"),
            triggerTime = doc.getString("triggerTime") ?: "",
            triggerLocation = doc.getString("triggerLocation") ?: "",
            blockingRule = doc.getString("blockingRule") ?: "",
            sprintStartDateIso = doc.getString("sprintStartDateIso") ?: "",
            currentDay = (doc.getLong("currentDay") ?: 1L).toInt().coerceIn(1, 21),
            completedDays = completed,
            missedDays = missed,
            nextMicroTask = doc.getString("nextMicroTask") ?: "",
            durationFactor = doc.getDouble("durationFactor") ?: 1.0,
            inMaintenanceMode = doc.getBoolean("inMaintenanceMode") ?: false,
            maintenanceUnlocked = doc.getBoolean("maintenanceUnlocked") ?: false,
            activeRepeatRangeStart = doc.getLong("activeRepeatRangeStart")?.toInt(),
            cycleNumber = (doc.getLong("cycleNumber") ?: 1L).toInt(),
            dailyLogs = logs
        )
    }

    fun toUserDocumentMap(state: UserSprintState): Map<String, Any?> = mapOf(
        "uid" to state.uid,
        "email" to state.email,
        "setupCompleted" to state.setupCompleted,
        "anchorTask" to state.anchorTask,
        "secondaryAnchorTask" to state.secondaryAnchorTask,
        "triggerTime" to state.triggerTime,
        "triggerLocation" to state.triggerLocation,
        "blockingRule" to state.blockingRule,
        "sprintStartDateIso" to state.sprintStartDateIso,
        "currentDay" to state.currentDay.coerceIn(1, 21),
        "completedDays" to state.completedDays.sorted(),
        "missedDays" to state.missedDays.sorted(),
        "nextMicroTask" to state.nextMicroTask,
        "durationFactor" to state.durationFactor,
        "inMaintenanceMode" to state.inMaintenanceMode,
        "maintenanceUnlocked" to state.maintenanceUnlocked,
        "activeRepeatRangeStart" to state.activeRepeatRangeStart,
        "cycleNumber" to state.cycleNumber,
        "updatedAt" to FieldValue.serverTimestamp()
    )

    fun toDailyLogEntry(doc: DocumentSnapshot): DailyLogEntry? {
        if (!doc.exists()) return null
        val checksMap = doc.get("checks") as? Map<*, *>
        val checks = DailyCheckState(
            checkPrep = checksMap?.get("check_prep") as? Boolean ?: false,
            checkExecution = checksMap?.get("check_execution") as? Boolean ?: false,
            checkClosure = checksMap?.get("check_closure") as? Boolean ?: false
        )
        return DailyLogEntry(
            day = (doc.getLong("day") ?: doc.id.toLongOrNull() ?: 1L).toInt(),
            completed = doc.getBoolean("completed") ?: false,
            missed = doc.getBoolean("missed") ?: false,
            startTimeIso = doc.getString("startTimeIso"),
            completedAtIso = doc.getString("completedAtIso"),
            punctual = doc.getBoolean("punctual") ?: false,
            effectiveFocusMinutes = (doc.getLong("effectiveFocusMinutes") ?: 0L).toInt(),
            checks = checks,
            closureTask = doc.getString("closureTask") ?: "",
            specialInput = doc.getString("specialInput"),
            selectedFormatId = doc.getString("selectedFormatId")
        )
    }

    fun toDailyLogMap(entry: DailyLogEntry): Map<String, Any?> = mapOf(
        "day" to entry.day,
        "completed" to entry.completed,
        "missed" to entry.missed,
        "startTimeIso" to entry.startTimeIso,
        "completedAtIso" to entry.completedAtIso,
        "punctual" to entry.punctual,
        "effectiveFocusMinutes" to entry.effectiveFocusMinutes,
        "checks" to mapOf(
            "check_prep" to entry.checks.checkPrep,
            "check_execution" to entry.checks.checkExecution,
            "check_closure" to entry.checks.checkClosure
        ),
        "closureTask" to entry.closureTask,
        "specialInput" to entry.specialInput,
        "selectedFormatId" to entry.selectedFormatId
    )

    fun toMaintenanceSession(doc: DocumentSnapshot): MaintenanceSessionRecord? {
        if (!doc.exists()) return null
        return MaintenanceSessionRecord(
            id = doc.id,
            anchorTaskUsed = doc.getString("anchorTaskUsed") ?: "",
            formatLabel = doc.getString("formatLabel") ?: "",
            focusMinutes = (doc.getLong("focusMinutes") ?: 0L).toInt(),
            completedAtIso = doc.getString("completedAtIso") ?: ""
        )
    }

    fun toMaintenanceSessionMap(record: MaintenanceSessionRecord): Map<String, Any?> = mapOf(
        "anchorTaskUsed" to record.anchorTaskUsed,
        "formatLabel" to record.formatLabel,
        "focusMinutes" to record.focusMinutes,
        "completedAtIso" to record.completedAtIso,
        "completedAt" to Timestamp.now()
    )

    fun toHistorySnapshotMap(snapshot: SprintHistorySnapshot): Map<String, Any?> = mapOf(
        "cycleNumber" to snapshot.cycleNumber,
        "compliancePercentage" to snapshot.compliancePercentage,
        "verdictTier" to snapshot.verdictTier.name,
        "totalMinutes" to snapshot.totalMinutes,
        "archivedAtIso" to snapshot.archivedAtIso,
        "archivedAt" to FieldValue.serverTimestamp()
    )
}
