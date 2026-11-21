/**
 * Codigo anadido por Felipe Denis
 *
 * Cargador Data-Driven de feedback_catalog.json y achievements_catalog.json desde assets/dopamine/.
 */
package com.utdevs.habitflow.data.dopamine

import android.content.Context
import com.utdevs.habitflow.domain.model.AchievementSpec
import com.utdevs.habitflow.domain.model.DopamineCatalog
import com.utdevs.habitflow.domain.model.DopamineMessages
import com.utdevs.habitflow.domain.model.RankSpec
import com.utdevs.habitflow.domain.model.XpRules
import com.utdevs.habitflow.domain.repository.DopamineCatalogSource
import org.json.JSONArray
import org.json.JSONObject

class AssetDopamineCatalogSource(
    context: Context
) : DopamineCatalogSource {

    private val cachedCatalog: DopamineCatalog

    init {
        cachedCatalog = runCatching {
            val feedbackRaw = context.assets.open("dopamine/feedback_catalog.json")
                .bufferedReader().use { it.readText() }
            val achRaw = context.assets.open("dopamine/achievements_catalog.json")
                .bufferedReader().use { it.readText() }
            parseCatalog(feedbackRaw, achRaw)
        }.getOrElse { DopamineCatalog() }
    }

    override fun getCatalog(): DopamineCatalog = cachedCatalog

    private fun parseCatalog(feedbackRaw: String, achRaw: String): DopamineCatalog {
        val fRoot = JSONObject(feedbackRaw)
        val rulesObj = fRoot.getJSONObject("xpRules")
        val rules = XpRules(
            prepCheckXp = rulesObj.optInt("prepCheckXp", 5),
            punctualStartXp = rulesObj.optInt("punctualStartXp", 10),
            dayCompletionBaseXp = rulesObj.optInt("dayCompletionBaseXp", 25),
            xpPerFocusMinute = rulesObj.optInt("xpPerFocusMinute", 1),
            phoenixRecoveryBonusXp = rulesObj.optInt("phoenixRecoveryBonusXp", 20),
            surpriseRpeBonusXp = rulesObj.optInt("surpriseRpeBonusXp", 15),
            surpriseRpeChancePercent = rulesObj.optInt("surpriseRpeChancePercent", 30),
            streakDaysPerShield = rulesObj.optInt("streakDaysPerShield", 5),
            maxShields = rulesObj.optInt("maxShields", 2)
        )

        val ranksArr = fRoot.getJSONArray("ranks")
        val ranks = buildList {
            for (i in 0 until ranksArr.length()) {
                val r = ranksArr.getJSONObject(i)
                add(RankSpec(minXp = r.getInt("minXp"), title = r.getString("title")))
            }
        }

        val msgObj = fRoot.getJSONObject("messages")
        val messages = DopamineMessages(
            onPunctualStart = msgObj.optJSONArray("onPunctualStart").toStringList(),
            onMidBlock = msgObj.optJSONArray("onMidBlock").toStringList(),
            onRecoveryAfterMiss = msgObj.optJSONArray("onRecoveryAfterMiss").toStringList(),
            onSurpriseRpe = msgObj.optJSONArray("onSurpriseRpe").toStringList()
        )

        val aRoot = JSONObject(achRaw)
        val aArr = aRoot.getJSONArray("achievements")
        val achievements = buildList {
            for (i in 0 until aArr.length()) {
                val a = aArr.getJSONObject(i)
                add(
                    AchievementSpec(
                        id = a.getString("id"),
                        title = a.getString("title"),
                        description = a.getString("description"),
                        bonusXp = a.getInt("bonusXp")
                    )
                )
            }
        }

        return DopamineCatalog(
            xpRules = rules,
            ranks = ranks,
            achievements = achievements,
            messages = messages
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (i in 0 until length()) add(getString(i))
        }
    }
}
