/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias para el modulo Black Box de IA:
 * `BeginnerMentorPromptSpec` y `GeminiResponseParser`.
 */
package com.utdevs.habitflow.data.ai

import com.utdevs.habitflow.domain.repository.MicroStepContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiResponseParserTest {

    @Test
    fun `parse extracts conceptBrief and 3 steps from fenced JSON`() {
        val raw = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "```json\n{\"conceptBrief\":\"Una variable guarda un dato con nombre.\",\"steps\":[\"Crea una variable val\",\"Imprime su valor\",\"Cambia el texto\"]}\n```"
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val parsed = GeminiResponseParser.parse(raw)
        assertNotNull(parsed)
        assertEquals("Una variable guarda un dato con nombre.", parsed?.conceptBrief)
        assertEquals(3, parsed?.steps?.size)
        assertEquals("Crea una variable val", parsed?.steps?.first())
    }

    @Test
    fun `parse returns null on invalid or empty response`() {
        assertNull(GeminiResponseParser.parse("{}"))
        assertNull(GeminiResponseParser.parse("invalid json"))
    }

    @Test
    fun `prompt spec enforces beginner explanation and embeds goal context`() {
        val ctx = MicroStepContext(
            goalTitle = "Aprender Rust desde cero",
            anchorTask = "Escribir 1 funcion",
            focusMinutes = 15,
            dayNumber = 1,
            dayRule = "Friccion cero",
            successCriteria = "Completar bloque",
            triggerLocation = "Escritorio",
            previousClosure = null,
            forTomorrow = false
        )
        val userPrompt = BeginnerMentorPromptSpec.buildUserPrompt(ctx)
        assertTrue(BeginnerMentorPromptSpec.SYSTEM_PROMPT.contains("DESDE CERO"))
        assertTrue(userPrompt.contains("Aprender Rust desde cero"))
        assertTrue(userPrompt.contains("15 minutos"))
    }
}
