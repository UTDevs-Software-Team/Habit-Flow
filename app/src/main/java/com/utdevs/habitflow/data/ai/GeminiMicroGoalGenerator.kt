/**
 * Codigo anadido por Felipe Denis
 *
 * Implementacion Black Box de MicroGoalGenerator usando Gemini 3.8 Flash
 * (models/gemini-3.8-flash) con respaldo offline Data-Driven en assets/plans/micro_step_fallbacks.json.
 */
package com.utdevs.habitflow.data.ai

import android.content.Context
import com.utdevs.habitflow.BuildConfig
import com.utdevs.habitflow.domain.repository.MicroGoalGenerator
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class GeminiMicroGoalGenerator(
    private val context: Context
) : MicroGoalGenerator {

    private val modelId = "gemini-3.8-flash"

    override suspend fun suggestMicroSteps(
        anchorTask: String,
        focusMinutes: Int,
        previousClosure: String?,
        dayNumber: Int
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        if (apiKey.isEmpty()) {
            return@withContext Result.success(loadOfflineFallback(focusMinutes))
        }

        val remote = runCatching {
            val endpoint =
                "https://generativelanguage.googleapis.com/v1beta/models/$modelId:generateContent?key=$apiKey"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 15_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            val prevText = previousClosure?.takeIf { it.isNotBlank() } ?: "Ninguno aun (inicio)"
            val prompt = """
                Eres un coach conductual anti-procrastinacion.
                El usuario tiene esta meta/tarea ancla: "$anchorTask".
                Hoy es el Dia $dayNumber de 21 y su bloque es de $focusMinutes minutos.
                Paso previo cerrado ayer: "$prevText".
                Genera exactamente 3 micro-pasos ultra-concretos, fisicos y accionables para empezar sin friccion.
                Cada micro-paso debe tener maximo 12 palabras, sin emojis, en espanol directo.
                Responde UNICAMENTE JSON valido con la forma: {"steps":["paso 1","paso 2","paso 3"]}.
            """.trimIndent()

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
                        .put("temperature", 0.5)
                        .put("responseMimeType", "application/json")
                )
            }

            conn.outputStream.use { os ->
                os.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val rawResponse = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            if (code !in 200..299) {
                error("Gemini HTTP $code: $rawResponse")
            }

            extractSteps(rawResponse).ifEmpty { loadOfflineFallback(focusMinutes) }
        }

        if (remote.isSuccess) {
            remote
        } else {
            Result.success(loadOfflineFallback(focusMinutes))
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

    private fun loadOfflineFallback(focusMinutes: Int): List<String> = runCatching {
        val raw = context.assets.open("plans/micro_step_fallbacks.json")
            .bufferedReader().use { it.readText() }
        val root = JSONObject(raw)
        val bucket = when {
            focusMinutes <= 15 -> "short"
            focusMinutes <= 35 -> "medium"
            else -> "deep"
        }
        val arr = root.getJSONArray(bucket)
        buildList {
            for (i in 0 until arr.length()) add(arr.getString(i))
        }
    }.getOrElse {
        listOf(
            "Abre solo tu material principal y trabaja 5 minutos",
            "Define 1 sub-paso verificable antes de iniciar",
            "Completa el primer bloque sin abrir otra aplicacion"
        )
    }
}
