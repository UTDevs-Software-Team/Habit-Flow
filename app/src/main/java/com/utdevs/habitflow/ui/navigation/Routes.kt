/**
 * Codigo anadido por Felipe Denis
 *
 * Definicion centralizada de rutas de navegacion Multi-Meta en Jetpack Compose.
 */
package com.utdevs.habitflow.ui.navigation

object Routes {
    const val GOALS_HUB = "goals_hub"
    const val SETTINGS = "settings"
    const val GOAL_DASHBOARD = "goal/{goalId}/dashboard"
    const val GOAL_SESSION = "goal/{goalId}/session/{day}"
    const val GOAL_METRICS = "goal/{goalId}/metrics"
    const val GOAL_HISTORY = "goal/{goalId}/history"
    const val GOAL_MAINTENANCE = "goal/{goalId}/maintenance"

    fun goalDashboard(goalId: String): String = "goal/$goalId/dashboard"
    fun goalSession(goalId: String, day: Int): String = "goal/$goalId/session/$day"
    fun goalMetrics(goalId: String): String = "goal/$goalId/metrics"
    fun goalHistory(goalId: String): String = "goal/$goalId/history"
    fun goalMaintenance(goalId: String): String = "goal/$goalId/maintenance"
}
