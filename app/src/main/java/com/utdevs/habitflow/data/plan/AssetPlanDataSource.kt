/**
 * Codigo anadido por Felipe Denis
 *
 * Cargador Data-Driven del plan de 21 dias desde assets/plans/anti_procrastination_21d.json.
 * Implementa PlanDataSource como caja negra aislada de la capa UI.
 */
package com.utdevs.habitflow.data.plan

import android.content.Context
import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.DayPlanSpec
import com.utdevs.habitflow.domain.model.FormatOption
import com.utdevs.habitflow.domain.model.PhaseSpec
import com.utdevs.habitflow.domain.model.SessionBlock
import com.utdevs.habitflow.domain.model.SpecialEvent
import com.utdevs.habitflow.domain.plan.HabitFlow21DayPlan
import com.utdevs.habitflow.domain.repository.PlanDataSource
import org.json.JSONArray
import org.json.JSONObject

class AssetPlanDataSource(
    context: Context
) : PlanDataSource {

    private val parsedPhases: List<PhaseSpec>
    private val totalTargetMinutes: Int

    init {
        val loaded = runCatching {
            val raw = context.assets.open("plans/anti_procrastination_21d.json")
                .bufferedReader()
                .use { it.readText() }
            parseJson(raw)
        }.getOrNull()

        if (loaded != null && loaded.first.isNotEmpty()) {
            parsedPhases = loaded.first
            totalTargetMinutes = loaded.second
        } else {
            parsedPhases = HabitFlow21DayPlan.getPhases()
            totalTargetMinutes = HabitFlow21DayPlan.getTotalTargetMinutes()
        }
    }

    override fun getPhases(): List<PhaseSpec> = parsedPhases

    override fun getDaySpec(day: Int): DayPlanSpec =
        parsedPhases.flatMap { it.days }.first { it.day == day }

    override fun getTotalTargetMinutes(): Int = totalTargetMinutes

    private fun parseJson(raw: String): Pair<List<PhaseSpec>, Int> {
        val root = JSONObject(raw)
        val total = root.optInt("totalTargetMinutes", HabitFlow21DayPlan.TOTAL_TARGET_MINUTES)
        val phasesArray = root.getJSONArray("phases")
        val phases = buildList {
            for (i in 0 until phasesArray.length()) {
                val pObj = phasesArray.getJSONObject(i)
                val phaseId = pObj.getInt("phaseId")
                val daysArr = pObj.getJSONArray("days")
                val days = buildList {
                    for (j in 0 until daysArr.length()) {
                        add(parseDay(daysArr.getJSONObject(j), phaseId))
                    }
                }
                add(
                    PhaseSpec(
                        phaseId = phaseId,
                        name = pObj.getString("name"),
                        daysRangeLabel = pObj.getString("daysRange"),
                        targetFocusMinutes = pObj.getInt("targetFocusMinutes"),
                        days = days
                    )
                )
            }
        }
        return phases to total
    }

    private fun parseDay(obj: JSONObject, phaseId: Int): DayPlanSpec {
        val constraintsArr = obj.optJSONArray("constraints") ?: JSONArray()
        val constraints = buildList {
            for (i in 0 until constraintsArr.length()) add(constraintsArr.getString(i))
        }
        val blocks = parseBlocks(obj.optJSONArray("blocks") ?: JSONArray())
        val specialObj = obj.optJSONObject("specialEvent")
        val specialEvent = specialObj?.let { parseSpecialEvent(it) }

        return DayPlanSpec(
            day = obj.getInt("day"),
            phaseId = phaseId,
            focusMinutes = obj.getInt("focusMinutes"),
            rule = obj.getString("rule"),
            successCriteria = obj.getString("successCriteria"),
            baseBlocks = blocks,
            constraints = constraints,
            punctualityWindowMinutes = obj.optInt("punctualityWindowMinutes", 10),
            specialEvent = specialEvent
        )
    }

    private fun parseBlocks(arr: JSONArray): List<SessionBlock> = buildList {
        for (i in 0 until arr.length()) {
            val b = arr.getJSONObject(i)
            val type = if (b.getString("type") == "break") BlockType.BREAK else BlockType.FOCUS
            add(SessionBlock(type = type, durationMinutes = b.getInt("duration")))
        }
    }

    private fun parseSpecialEvent(obj: JSONObject): SpecialEvent? = when (obj.optString("type")) {
        "text_input" -> SpecialEvent.TextInput(obj.getString("prompt"))
        "audit_text" -> SpecialEvent.AuditText(obj.getString("prompt"))
        "rating_1_5" -> SpecialEvent.Rating1To5(obj.getString("prompt"))
        "format_selector" -> {
            val optsArr = obj.getJSONArray("options")
            val options = buildList {
                for (i in 0 until optsArr.length()) {
                    val o = optsArr.getJSONObject(i)
                    add(
                        FormatOption(
                            id = o.getString("id"),
                            label = o.getString("label"),
                            blocks = parseBlocks(o.getJSONArray("blocks"))
                        )
                    )
                }
            }
            SpecialEvent.FormatSelector(options)
        }
        else -> null
    }
}
