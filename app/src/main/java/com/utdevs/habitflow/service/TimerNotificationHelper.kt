/**
 * Codigo anadido por Felipe Denis
 *
 * Utilidad aislada para crear el canal de notificaciones y construir las notificaciones
 * persistentes del ForegroundService.
 */
package com.utdevs.habitflow.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.utdevs.habitflow.MainActivity
import com.utdevs.habitflow.domain.model.BlockType

object TimerNotificationHelper {

    const val CHANNEL_ID = "habitflow_focus_timer"
    const val NOTIFICATION_ID = 1042

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Temporizador HabitFlow",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el tiempo restante del bloque de enfoque o descanso."
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun buildOngoingNotification(
        context: Context,
        state: TimerUiState
    ): Notification {
        val block = state.currentBlock
        val phaseLabel = when (block?.type) {
            BlockType.FOCUS -> "ENFOQUE"
            BlockType.BREAK -> "DESCANSO"
            null -> "SESION"
        }
        val totalSec = (state.remainingMillis / 1000L).coerceAtLeast(0L)
        val mins = totalSec / 60L
        val secs = totalSec % 60L
        val timeText = String.format("%02d:%02d", mins, secs)
        val statusText = if (state.status == TimerStatus.PAUSED) "Pausado" else "En curso"

        val openAppIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleActionIntent = PendingIntent.getService(
            context,
            1,
            Intent(context, FocusTimerService::class.java).apply {
                action = if (state.status == TimerStatus.RUNNING) {
                    FocusTimerService.ACTION_PAUSE
                } else {
                    FocusTimerService.ACTION_RESUME
                }
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleTitle = if (state.status == TimerStatus.RUNNING) "Pausar" else "Reanudar"

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("HabitFlow: $phaseLabel ($statusText)")
            .setContentText("Restante: $timeText | Bloque ${state.currentBlockIndex + 1}/${state.blocks.size}")
            .setContentIntent(openAppIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, toggleTitle, toggleActionIntent)
            .build()
    }
}
