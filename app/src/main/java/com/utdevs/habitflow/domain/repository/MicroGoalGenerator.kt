/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box para generar micro-metas accionables adaptadas a la Tarea Ancla
 * y a los minutos del bloque del dia.
 */
package com.utdevs.habitflow.domain.repository

interface MicroGoalGenerator {
    suspend fun suggestMicroSteps(
        anchorTask: String,
        focusMinutes: Int,
        previousClosure: String?,
        dayNumber: Int
    ): Result<List<String>>
}
