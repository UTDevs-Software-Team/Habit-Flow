/**
 * Codigo anadido por Felipe Denis
 *
 * Parser Black Box aislado para extraer `conceptBrief` y `steps` desde respuestas JSON
 * de la API REST de Gemini (`generateContent`).
 */
package com.utdevs.habitflow.data.ai

import org.json.JSONObject

/**
 * Resultado parseado de una respuesta de Gemini antes de empaquetarse en el dominio.
 *
 * @property conceptBrief Explicacion conceptual breve para principiantes.
 * @property steps Lista de 1 a 3 micro-metas accionables.
 */
data class ParsedMicroGoal(
    val conceptBrief: String,
    val steps: List<String>
)

/**
 * Extrae y sanea el JSON estructurado devuelto por Gemini, tolerando bloques Markdown ` ```json `.
 */
object GeminiResponseParser {

    /**
     * Parsea [rawResponse] de Gemini. Retorna `null` si no contiene pasos validos.
     */
    fun parse(rawResponse: String): ParsedMicroGoal? = runCatching {
        val root = JSONObject(rawResponse)
        val candidates = root.optJSONArray("candidates") ?: return@runCatching null
        if (candidates.length() == 0) return@runCatching null

        val rawText = candidates.getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val payload = JSONObject(rawText)
        val conceptBrief = payload.optString("conceptBrief").trim()
        val arr = payload.optJSONArray("steps") ?: return@runCatching null
        val steps = buildList {
            for (i in 0 until arr.length()) {
                val item = arr.optString(i).trim()
                if (item.isNotEmpty()) add(item)
            }
        }.take(3)

        if (steps.isEmpty()) null else ParsedMicroGoal(conceptBrief = conceptBrief, steps = steps)
    }.getOrNull()
}
