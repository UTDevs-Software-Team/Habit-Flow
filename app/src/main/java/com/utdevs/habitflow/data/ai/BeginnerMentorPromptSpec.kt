/**
 * Codigo anadido por Felipe Denis
 *
 * Especificacion Black Box del System Prompt y constructor de User Prompt para
 * el mentor de principiantes absolutos en HabitFlow.
 */
package com.utdevs.habitflow.data.ai

import com.utdevs.habitflow.domain.repository.MicroStepContext

/**
 * Define las instrucciones de sistema y el formato de contexto de usuario enviados a Gemini.
 *
 * Principios pedagogicos del prompt:
 * 1. Asume conocimiento cero del usuario sobre el tema (cero tecnicismos sin explicar).
 * 2. Explica primero el concepto esencial en 2-3 oraciones simples (`conceptBrief`).
 * 3. Convierte ese concepto en 3 micro-metas fisicas/verificables (`steps`) para el tiempo exacto del bloque.
 */
object BeginnerMentorPromptSpec {

    /**
     * System Prompt enviado en `systemInstruction` de la API de Gemini.
     */
    val SYSTEM_PROMPT: String = """
        Eres un mentor didactico y coach conductual anti-procrastinacion de HabitFlow.
        Tu usuario parte DESDE CERO: asume que NO sabe nada previo ni domina jerga tecnica del tema.

        Tu mision en cada peticion es doble:
        1. Explicar brevemente y en lenguaje cotidiano el concepto minimo necesario para la sesion de hoy (2 a 3 oraciones claras; si mencionas un termino tecnico, definelo en palabras simples).
        2. Entregar exactamente 3 micro-metas concretas, ordenadas y verificables que un principiante absoluto pueda completar dentro de los minutos indicados.

        Reglas estrictas de salida:
        - Adapta el vocabulario al tema real de la meta (ejemplos, herramientas, ejercicios o conceptos basicos reales).
        - PROHIBIDO dar consejos vacios o genericos como "abre tu material", "elimina distracciones" o "manten el foco".
        - Cada micro-meta debe ser una accion medible de maximo 16 palabras en espanol directo.
        - Cero emojis en toda la respuesta.
        - Responde UNICAMENTE con JSON valido con esta estructura exacta:
          {"conceptBrief":"Explicacion breve para principiante","steps":["micro-meta 1","micro-meta 2","micro-meta 3"]}
    """.trimIndent()

    /**
     * Construye el mensaje de usuario con todos los parametros de la sesion diaria.
     *
     * @param ctx Contexto de meta, dia, duracion, lugar y cierre previo.
     */
    fun buildUserPrompt(ctx: MicroStepContext): String {
        val goalLabel = ctx.goalTitle.ifBlank { ctx.anchorTask }.ifBlank { "Meta personal" }
        val anchorLabel = ctx.anchorTask.ifBlank { goalLabel }
        val prevText = ctx.previousClosure?.takeIf { it.isNotBlank() } ?: "Ninguno aun (primer contacto desde cero)"
        val locationLabel = ctx.triggerLocation.ifBlank { "espacio habitual de trabajo" }
        val horizon = if (ctx.forTomorrow) {
            "para dejar listas y arrancar MANANA sin friccion"
        } else {
            "secuenciales para ejecutar HOY en ${ctx.focusMinutes} minutos"
        }

        return """
            Meta que quiero aprender o lograr desde cero: "$goalLabel".
            Actividad minima diaria: "$anchorLabel".
            Parametros de la sesion: Dia ${ctx.dayNumber} de 21, duracion ${ctx.focusMinutes} minutos, lugar "$locationLabel".
            Regla operativa del dia: "${ctx.dayRule}".
            Criterio de exito: "${ctx.successCriteria}".
            Avance o cierre previo: "$prevText".

            Explica brevemente el concepto base necesario para hoy como si nunca hubiera tocado "$goalLabel",
            y dame 3 micro-metas $horizon.
        """.trimIndent()
    }
}
