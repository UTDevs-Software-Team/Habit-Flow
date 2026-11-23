/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box para generar micro-metas accionables adaptadas a la meta,
 * Tarea Ancla, regla del dia y minutos del bloque.
 */
package com.utdevs.habitflow.domain.repository

data class MicroStepContext(
    val goalTitle: String,
    val anchorTask: String,
    val focusMinutes: Int,
    val dayNumber: Int,
    val dayRule: String,
    val successCriteria: String,
    val triggerLocation: String,
    val previousClosure: String?,
    val forTomorrow: Boolean
)

data class MicroStepSuggestion(
    val steps: List<String>,
    val fromAi: Boolean,
    val modelUsed: String
)

interface MicroGoalGenerator {
    suspend fun suggestMicroSteps(context: MicroStepContext): Result<MicroStepSuggestion>
}
