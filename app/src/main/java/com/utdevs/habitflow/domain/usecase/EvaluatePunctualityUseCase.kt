/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Evalua si el inicio real de la sesion cumple la ventana de puntualidad
 * respecto a triggerTime (5 min para Dia 9, 10 min para el resto).
 */
package com.utdevs.habitflow.domain.usecase

import java.time.Duration
import java.time.LocalTime

class EvaluatePunctualityUseCase {

    /**
     * @param triggerTimeHHmm Hora configurada en Dia 0 en formato "HH:mm".
     * @param actualStartTime Hora local real en que el usuario pulso Iniciar.
     * @param windowMinutes Margen maximo de retraso permitido (5 o 10 min).
     */
    operator fun invoke(
        triggerTimeHHmm: String,
        actualStartTime: LocalTime,
        windowMinutes: Int
    ): Boolean {
        val parts = triggerTimeHHmm.split(":")
        if (parts.size != 2) return false
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false
        val scheduled = LocalTime.of(hour, minute)

        val diffMinutes = Duration.between(scheduled, actualStartTime).toMinutes()
        return diffMinutes in -15L..windowMinutes.toLong()
    }
}
