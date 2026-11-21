/**
 * Codigo anadido por Felipe Denis
 *
 * Programador Black Box de disparadores horarios locales (triggerTime - 2m y triggerTime).
 * Usa setAndAllowWhileIdle para evitar SecurityException de SCHEDULE_EXACT_ALARM en API 34+.
 */
package com.utdevs.habitflow.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.abs

class GoalReminderScheduler(
    private val context: Context
) {

    fun scheduleGoalReminders(goal: GoalSprint) {
        if (goal.status == GoalStatus.PAUSED) {
            cancelGoalReminders(goal.goalId)
            return
        }
        val parts = goal.sprint.triggerTime.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val scheduledTime = LocalTime.of(hour, minute)
        val prepTime = scheduledTime.minusMinutes(2)

        scheduleTrigger(
            requestCode = abs(goal.goalId.hashCode()) * 10 + 1,
            time = prepTime,
            title = "Prepara tu espacio: ${goal.title}",
            body = "En 2 min inicia tu bloque en ${goal.sprint.triggerLocation}. Bloqueo: ${goal.sprint.blockingRule}"
        )
        scheduleTrigger(
            requestCode = abs(goal.goalId.hashCode()) * 10 + 2,
            time = scheduledTime,
            title = "Disparador activo: ${goal.title}",
            body = "Hora fijada (${goal.sprint.triggerTime}). Inicia ahora para sumar +10 XP puntuales."
        )
    }

    fun cancelGoalReminders(goalId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        listOf(1, 2).forEach { offset ->
            val code = abs(goalId.hashCode()) * 10 + offset
            val pi = PendingIntent.getBroadcast(
                context,
                code,
                Intent(context, GoalReminderReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pi != null) {
                alarmManager.cancel(pi)
            }
        }
    }

    private fun scheduleTrigger(
        requestCode: Int,
        time: LocalTime,
        title: String,
        body: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var targetDateTime = LocalDateTime.of(LocalDate.now(), time)
        if (!targetDateTime.isAfter(LocalDateTime.now())) {
            targetDateTime = targetDateTime.plusDays(1)
        }
        val triggerMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, GoalReminderReceiver::class.java).apply {
            putExtra(GoalReminderReceiver.EXTRA_TITLE, title)
            putExtra(GoalReminderReceiver.EXTRA_BODY, body)
            putExtra(GoalReminderReceiver.EXTRA_NOTIF_ID, requestCode)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMillis, pi)
    }
}
