/**
 * Codigo anadido por Felipe Denis
 *
 * Activity unica de HabitFlow (Single Activity + Jetpack Compose con Modo Claro/Oscuro Forest).
 */
package com.utdevs.habitflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.ui.navigation.HabitFlowNavGraph
import com.utdevs.habitflow.ui.theme.HabitFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as HabitFlowApp).container
        setContent {
            val appearance by appContainer.themePreferences.settings.collectAsStateWithLifecycle()
            HabitFlowTheme(themeMode = appearance.themeMode) {
                HabitFlowNavGraph(container = appContainer)
            }
        }
    }
}
