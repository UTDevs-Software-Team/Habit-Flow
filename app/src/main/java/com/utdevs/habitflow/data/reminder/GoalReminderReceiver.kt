/**
 * Codigo anadido por Felipe Denis
 *
 * BroadcastReceiver que muestra recordatorios puntuales para reducir friccion de arranque (TMT).
 */
package com.utdevs.habitflow.data.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.utdevs.habitflow.MainActivity

class GoalReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "habitflow_goal_reminders"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"
        const val EXTRA_NOTIF_ID = "extra_notif_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Disparadores de Metas",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "HabitFlow: Momento de empezar"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Solo 5 minutos para romper la inercia."
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, 2001)

        val openIntent = PendingIntent.getActivity(
            context,
            notifId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(notifId, notif)
    }
}
