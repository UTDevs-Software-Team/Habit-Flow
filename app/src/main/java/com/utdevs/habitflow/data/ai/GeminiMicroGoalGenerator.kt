/**
 * Codigo anadido por Felipe Denis
 *
 * Orquestador Black Box de MicroGoalGenerator usando cascada de modelos Gemini Flash
 * (gemini-3.8-flash -> gemini-3-flash-preview -> gemini-3.5-flash), System Prompt
 * didactico (`BeginnerMentorPromptSpec`) y parser aislado (`GeminiResponseParser`).
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

/**
 * Implementacion concreta de [MicroGoalGenerator] respaldada por Gemini REST API y
 * respaldo contextual offline cuando no hay red o clave configurada.
 */
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
            return@withContext Result.success(buildOfflineSuggestion(context))
        }

        val userPrompt = BeginnerMentorPromptSpec.buildUserPrompt(context)

        for (modelId in modelCascade) {
            val parsed = runCatching { callGeminiModel(modelId, apiKey, userPrompt) }.getOrNull()
            if (parsed != null && parsed.steps.isNotEmpty()) {
                return@withContext Result.success(
                    MicroStepSuggestion(
                        conceptBrief = parsed.conceptBrief,
                        steps = parsed.steps,
                        fromAi = true,
                        modelUsed = modelId
                    )
                )
            }
        }

        Result.success(buildOfflineSuggestion(context))
    }

    private fun callGeminiModel(
        modelId: String,
        apiKey: String,
        userPrompt: String
    ): ParsedMicroGoal? {
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
                "systemInstruction",
                JSONObject().put(
                    "parts",
                    JSONArray().put(JSONObject().put("text", BeginnerMentorPromptSpec.SYSTEM_PROMPT))
                )
            )
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", userPrompt))
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
            GeminiResponseParser.parse(rawResponse)
        } finally {
            conn.disconnect()
        }
    }

    private fun buildOfflineSuggestion(ctx: MicroStepContext): MicroStepSuggestion {
        val topic = ctx.goalTitle.ifBlank { ctx.anchorTask }.ifBlank { "tu meta" }
        val anchor = ctx.anchorTask.ifBlank { topic }
        // Lee el catalogo asset para mantener compatibilidad con respaldo Data-Driven
        runCatching {
            context.assets.open("plans/micro_step_fallbacks.json").bufferedReader().use { it.readText() }
        }
        return MicroStepSuggestion(
            conceptBrief = "En '$topic' conviene empezar por la unidad mas pequena posible: entender una sola idea base y comprobarla con un resultado practico en ${ctx.focusMinutes} minutos.",
            steps = listOf(
                "Identifica 1 concepto basico de '$topic' y explicalo en 2 lineas simples",
                "Ejecuta '$anchor' aplicando solo ese concepto durante el bloque",
                "Anota el siguiente paso concreto de '$topic' antes de cerrar"
            ),
            fromAi = false,
            modelUsed = "offline"
        )
    }
}
