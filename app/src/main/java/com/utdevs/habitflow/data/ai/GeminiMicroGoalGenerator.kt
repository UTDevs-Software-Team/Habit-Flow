/**
 * Codigo anadido por Felipe Denis
 *
 * Implementacion Black Box de MicroGoalGenerator usando cascada de modelos Gemini Flash
 * (gemini-3.8-flash -> gemini-3-flash-preview -> gemini-3.5-flash) con contexto completo
 * de la meta y respaldo offline contextualizado en assets/plans/micro_step_fallbacks.json.
 */
package com.utdevs.habitflow.data.ai

import android.content.Context
import com.utdevs.habitflow.BuildConfig
import com.utdevs.habitflow.domain.repository.MicroGoalGenerator
import com.utdevs.habitflow.domain.repository.MicroStepContext
import com.utdevs.habitflow.domain.repository.MicroStepSuggestion
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeminiMicroGoalGenerator(
    private val context: Context
) : MicroGoalGenerator {

    private val modelCascade = listOf(
        "gemini-3.8-flash",
        "gemini-3-flash-preview",
        "gemini-3.5-flash"
    )

    override suspend fun suggestMicroSteps(
        context: MicroStepContext
    ): Result<MicroStepSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty()) {
            return@withContext Result.success(
                MicroStepSuggestion(
                    steps = loadContextualFallback(context),
                    fromAi = false,
                    modelUsed = "offline"
                )
            )
        }

        val prompt = buildPrompt(context)

        for (modelId in modelCascade) {
            val attempt = runCatching { callGeminiModel(modelId, apiKey, prompt) }
            val steps = attempt.getOrNull().orEmpty()
            if (steps.isNotEmpty()) {
                return@withContext Result.success(
                    MicroStepSuggestion(
                        steps = steps,
                        fromAi = true,
                        modelUsed = modelId
                    )
                )
            }
        }

        Result.success(
            MicroStepSuggestion(
                steps = loadContextualFallback(context),
                fromAi = false,
                modelUsed = "offline"
            )
        )
    }

    private fun buildPrompt(ctx: MicroStepContext): String {
        val goalLabel = ctx.goalTitle.ifBlank { ctx.anchorTask }.ifBlank { "Meta personal" }
        val anchorLabel = ctx.anchorTask.ifBlank { goalLabel }
        val prevText = ctx.previousClosure?.takeIf { it.isNotBlank() } ?: "Ninguno aun (primer arranque)"
        val locationLabel = ctx.triggerLocation.ifBlank { "espacio de estudio/trabajo" }
        val horizon = if (ctx.forTomorrow) {
            "para dejar listos y ejecutar MANANA sin friccion"
        } else {
            "secuenciales y concretos para ejecutar HOY en este bloque de ${ctx.focusMinutes} minutos"
        }

        return """
            Eres un mentor tecnico y coach conductual anti-procrastinacion.
            Meta principal que el usuario quiere lograr o aprender: "$goalLabel".
            Tarea ancla diaria: "$anchorLabel".
            Contexto de la sesion: Dia ${ctx.dayNumber} de 21, duracion ${ctx.focusMinutes} minutos, lugar "$locationLabel".
            Regla del dia: "${ctx.dayRule}".
            Criterio de exito: "${ctx.successCriteria}".
            Ultimo avance o cierre previo: "$prevText".

            Genera exactamente 3 micro-pasos $horizon sobre "$goalLabel" ($anchorLabel).
            Reglas estrictas:
            1. Usa vocabulario y acciones especificas del tema "$goalLabel" (por ejemplo conceptos, ejercicios, archivos, herramientas o practicas reales de ese tema).
            2. PROHIBIDO dar consejos genericos como "abre tu material", "elimina distracciones" o "manten el foco".
            3. Cada micro-paso debe ser una accion verificable de maximo 14 palabras, en espanol directo y sin emojis.
            Responde UNICAMENTE JSON valido con la forma: {"steps":["paso 1","paso 2","paso 3"]}.
        """.trimIndent()
    }

    private fun callGeminiModel(modelId: String, apiKey: String, prompt: String): List<String> {
        val endpoint =
            "https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey"
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5_000
            readTimeout = 8_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }

        val body = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", prompt))
                    )
                )
            )
            put(
                "generationConfig",
                JSONObject()
                    .put("temperature", 0.4)
                    .put("responseMimeType", "application/json")
            )
        }

        return try {
            conn.outputStream.use { os ->
                os.write(body.toString().toByteArray(Charsets.UTF_8))
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val rawResponse = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                error("Gemini $modelId HTTP $code: $rawResponse")
            }
            extractSteps(rawResponse)
        } finally {
            conn.disconnect()
        }
    }

    private fun extractSteps(rawResponse: String): List<String> {
        val root = JSONObject(rawResponse)
        val candidates = root.optJSONArray("candidates") ?: return emptyList()
        if (candidates.length() == 0) return emptyList()
        val text = candidates.getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val parsed = JSONObject(text)
        val arr = parsed.optJSONArray("steps") ?: return emptyList()
        return buildList {
            for (i in 0 until arr.length()) {
                val item = arr.optString(i).trim()
                if (item.isNotEmpty()) add(item)
            }
        }.take(3)
    }

    private fun loadContextualFallback(ctx: MicroStepContext): List<String> {
        val topic = ctx.goalTitle.ifBlank { ctx.anchorTask }.ifBlank { "tu meta" }
        val anchor = ctx.anchorTask.ifBlank { topic }
        val base = runCatching {
            val raw = context.assets.open("plans/micro_step_fallbacks.json")
                .bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val bucket = when {
                ctx.focusMinutes <= 15 -> "short"
                ctx.focusMinutes <= 35 -> "medium"
                else -> "deep"
            }
            val arr = root.getJSONArray(bucket)
            buildList {
                for (i in 0 until arr.length()) add(arr.getString(i))
            }
        }.getOrDefault(emptyList())

        val contextual = listOf(
            "Divide '$topic' en 1 concepto clave para los primeros 5 min",
            "Ejecuta '$anchor' registrando 1 resultado verificable hoy",
            "Anota el siguiente paso exacto de '$topic' antes de cerrar"
        )
        return if (base.isEmpty()) contextual else contextual
    }
}
