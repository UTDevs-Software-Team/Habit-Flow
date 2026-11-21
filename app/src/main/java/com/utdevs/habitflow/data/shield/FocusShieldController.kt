/**
 * Codigo anadido por Felipe Denis
 *
 * Controlador Black Box de Bloqueo Real en Android:
 * - Activa Modo No Molestar (DND) real durante bloques FOCUS si el usuario otorgo permiso.
 * - Restaura automaticamente el estado previo al entrar en DESCANSO o finalizar la sesion.
 */
package com.utdevs.habitflow.data.shield

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings

class FocusShieldController(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var previousFilter: Int? = null

    fun hasDndAccess(): Boolean = notificationManager.isNotificationPolicyAccessGranted

    fun buildDndSettingsIntent(): Intent =
        Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    fun activateFocusDnd() {
        if (!hasDndAccess()) return
        runCatching {
            if (previousFilter == null) {
                previousFilter = notificationManager.currentInterruptionFilter
            }
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }

    fun restorePreviousDnd() {
        if (!hasDndAccess()) return
        runCatching {
            val prev = previousFilter
            if (prev != null) {
                notificationManager.setInterruptionFilter(prev)
                previousFilter = null
            } else if (notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }
}
