/**
 * Codigo anadido por Felipe Denis
 *
 * Sistema de Tokens de Color "Botanical FinTech" (Paleta Forest de 30 colores)
 * con soporte completo para Modo Claro y Modo Oscuro.
 */
package com.utdevs.habitflow.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Nucleo Forest (30 colores organizados)
val ForestDeepest = Color(0xFF172B19)
val ForestCanopy = Color(0xFF224621)
val ForestMoss = Color(0xFF265D28)
val ForestEmerald = Color(0xFF39853F)
val ForestVibrant = Color(0xFF4BA548)
val ForestOlive = Color(0xFF688D42)
val ForestLimeSoft = Color(0xFF7FB14E)
val ForestSage = Color(0xFF78A972)
val ForestMintSoft = Color(0xFF92C58A)

val SlatePine = Color(0xFF2E403B)
val TealPine = Color(0xFF406260)
val TealMist = Color(0xFF53817E)

val BarkDark = Color(0xFF2C2119)
val BarkMedium = Color(0xFF473528)
val BarkWarm = Color(0xFF6C503C)
val ClaySoft = Color(0xFFA88167)
val TerracottaAction = Color(0xFFB86931)
val TerracottaLight = Color(0xFFD48046)
val OchreWarm = Color(0xFF9A572A)

@Immutable
data class ForestColors(
    val isDark: Boolean,
    val background: Color,
    val surfaceCard: Color,
    val surfaceSubtle: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnAccent: Color,
    val heroStart: Color,
    val heroEnd: Color,
    val primaryAction: Color,
    val vibrantSuccess: Color,
    val softSuccessBg: Color,
    val terracottaCta: Color,
    val softTerracottaBg: Color,
    val tealSecondary: Color,
    val softTealBg: Color,
    val clayNeedsWater: Color,
    val barkTrunk: Color,
    val mutedLock: Color
)

val LightForestColors = ForestColors(
    isDark = false,
    background = Color(0xFFF2F7F4),
    surfaceCard = Color(0xFFFFFFFF),
    surfaceSubtle = Color(0xFFE7F0EA),
    cardBorder = Color(0xFFDCE8E0),
    textPrimary = ForestDeepest,
    textSecondary = SlatePine,
    textOnAccent = Color(0xFFFFFFFF),
    heroStart = ForestCanopy,
    heroEnd = ForestEmerald,
    primaryAction = ForestEmerald,
    vibrantSuccess = ForestVibrant,
    softSuccessBg = Color(0xFFDFF2E1),
    terracottaCta = TerracottaAction,
    softTerracottaBg = Color(0xFFF9E6D8),
    tealSecondary = TealPine,
    softTealBg = Color(0xFFE0ECEB),
    clayNeedsWater = ClaySoft,
    barkTrunk = BarkMedium,
    mutedLock = Color(0xFF8FA396)
)

val DarkForestColors = ForestColors(
    isDark = true,
    background = Color(0xFF0E1811),
    surfaceCard = Color(0xFF16241A),
    surfaceSubtle = Color(0xFF1E3224),
    cardBorder = Color(0xFF2A4231),
    textPrimary = Color(0xFFEAF4EC),
    textSecondary = Color(0xFFA5C2AD),
    textOnAccent = Color(0xFFFFFFFF),
    heroStart = ForestDeepest,
    heroEnd = ForestMoss,
    primaryAction = ForestVibrant,
    vibrantSuccess = ForestLimeSoft,
    softSuccessBg = Color(0xFF1F3D24),
    terracottaCta = TerracottaLight,
    softTerracottaBg = Color(0xFF3A2416),
    tealSecondary = TealMist,
    softTealBg = Color(0xFF1D3332),
    clayNeedsWater = Color(0xFFC4987C),
    barkTrunk = Color(0xFF8F6D55),
    mutedLock = Color(0xFF5D7565)
)

val LocalForestColors = staticCompositionLocalOf { LightForestColors }
