/**
 * Codigo anadido por Felipe Denis
 *
 * Tema Material 3 dual (Modo Claro y Modo Oscuro Forest) con acceso a LocalForestColors.
 */
package com.utdevs.habitflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.utdevs.habitflow.data.theme.ForestThemeMode

private val ForestLightScheme = lightColorScheme(
    primary = LightForestColors.primaryAction,
    onPrimary = LightForestColors.textOnAccent,
    secondary = LightForestColors.tealSecondary,
    onSecondary = LightForestColors.textOnAccent,
    tertiary = LightForestColors.terracottaCta,
    background = LightForestColors.background,
    onBackground = LightForestColors.textPrimary,
    surface = LightForestColors.surfaceCard,
    onSurface = LightForestColors.textPrimary,
    surfaceVariant = LightForestColors.surfaceSubtle,
    onSurfaceVariant = LightForestColors.textSecondary,
    error = TerracottaAction,
    onError = LightForestColors.textOnAccent
)

private val ForestDarkScheme = darkColorScheme(
    primary = DarkForestColors.primaryAction,
    onPrimary = DarkForestColors.textOnAccent,
    secondary = DarkForestColors.tealSecondary,
    onSecondary = DarkForestColors.textOnAccent,
    tertiary = DarkForestColors.terracottaCta,
    background = DarkForestColors.background,
    onBackground = DarkForestColors.textPrimary,
    surface = DarkForestColors.surfaceCard,
    onSurface = DarkForestColors.textPrimary,
    surfaceVariant = DarkForestColors.surfaceSubtle,
    onSurfaceVariant = DarkForestColors.textSecondary,
    error = TerracottaLight,
    onError = DarkForestColors.textOnAccent
)

@Composable
fun HabitFlowTheme(
    themeMode: ForestThemeMode = ForestThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ForestThemeMode.SYSTEM -> isSystemInDarkTheme()
        ForestThemeMode.LIGHT -> false
        ForestThemeMode.DARK -> true
    }
    val forestColors = if (useDark) DarkForestColors else LightForestColors
    val m3Scheme = if (useDark) ForestDarkScheme else ForestLightScheme

    CompositionLocalProvider(LocalForestColors provides forestColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography = HabitFlowTypography,
            content = content
        )
    }
}
