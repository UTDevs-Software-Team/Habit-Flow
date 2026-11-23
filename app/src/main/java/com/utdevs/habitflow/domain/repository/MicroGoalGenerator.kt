/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box del dominio para generar explicaciones conceptuales breves
 * (nivel principiante absoluto) y micro-metas accionables adaptadas a cada sesion.
 */
package com.utdevs.habitflow.domain.repository

/**
 * Contexto completo de la sesion actual usado para generar la guia sin friccion.
 *
 * @property goalTitle Nombre principal de la meta que el usuario quiere lograr o aprender.
 * @property anchorTask Unidad minima diaria configurada en el Dia 0.
 * @property focusMinutes Minutos efectivos del bloque de enfoque objetivo.
 * @property dayNumber Dia actual dentro del sprint de 21 dias (1..21).
 * @property dayRule Regla operativa definida para el dia en el plan.
 * @property successCriteria Criterio de exito verificable de la jornada.
 * @property triggerLocation Espacio fisico habitual donde se ejecuta la sesion.
 * @property previousClosure Ultima micro-tarea cerrada en la sesion previa, si existe.
 * @property forTomorrow Indica si los pasos deben orientarse a hoy (`false`) o a manana (`true`).
 */
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

/**
 * Resultado estructurado de sugerencia para el usuario principiante.
 *
 * @property conceptBrief Explicacion breve y en lenguaje llano del concepto clave del bloque.
 * @property steps Lista de hasta 3 micro-metas verificables y secuenciales.
 * @property fromAi `true` si proviene de un modelo Gemini remoto; `false` si uso respaldo offline.
 * @property modelUsed Identificador del modelo que respondio o `"offline"`.
 */
data class MicroStepSuggestion(
    val conceptBrief: String = "",
    val steps: List<String>,
    val fromAi: Boolean,
    val modelUsed: String
)

/**
 * Puerto Black Box para generadores de micro-metas contextuales.
 */
interface MicroGoalGenerator {
    /**
     * Genera una explicacion conceptual breve y 3 micro-metas accionables para [context].
     */
    suspend fun suggestMicroSteps(context: MicroStepContext): Result<MicroStepSuggestion>
}
