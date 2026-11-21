/**
 * Codigo anadido por Felipe Denis
 *
 * Persistencia local de preferencias de tema (Modo Claro / Oscuro / Sistema), sonido y haptica.
 */
package com.utdevs.habitflow.data.theme

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ForestThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppearanceSettings(
    val themeMode: ForestThemeMode = ForestThemeMode.SYSTEM,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

class ThemePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("habitflow_appearance", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadInitial())
    val settings: StateFlow<AppearanceSettings> = _settings.asStateFlow()

    private fun loadInitial(): AppearanceSettings {
        val rawMode = prefs.getString("theme_mode", ForestThemeMode.SYSTEM.name)
        val mode = runCatching { ForestThemeMode.valueOf(rawMode ?: ForestThemeMode.SYSTEM.name) }
            .getOrDefault(ForestThemeMode.SYSTEM)
        val sound = prefs.getBoolean("sound_enabled", true)
        val haptics = prefs.getBoolean("haptics_enabled", true)
        return AppearanceSettings(themeMode = mode, soundEnabled = sound, hapticsEnabled = haptics)
    }

    fun setThemeMode(mode: ForestThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.update { it.copy(themeMode = mode) }
    }

    fun toggleDarkLight(currentlyDark: Boolean) {
        setThemeMode(if (currentlyDark) ForestThemeMode.LIGHT else ForestThemeMode.DARK)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _settings.update { it.copy(soundEnabled = enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _settings.update { it.copy(hapticsEnabled = enabled) }
    }
}
