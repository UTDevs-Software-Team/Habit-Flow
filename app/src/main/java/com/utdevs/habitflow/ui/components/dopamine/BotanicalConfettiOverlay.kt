/**
 * Codigo anadido por Felipe Denis
 *
 * Overlay animado de Confeti de Hojas en Compose Canvas.
 * Se activa cuando el usuario sube de nivel en su meta (etapa de planta o Rango Botanico).
 */
package com.utdevs.habitflow.ui.components.dopamine

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import com.utdevs.habitflow.ui.theme.ForestEmerald
import com.utdevs.habitflow.ui.theme.ForestLimeSoft
import com.utdevs.habitflow.ui.theme.ForestMintSoft
import com.utdevs.habitflow.ui.theme.ForestVibrant
import com.utdevs.habitflow.ui.theme.TerracottaAction
import kotlin.math.sin
import kotlin.random.Random

private data class LeafParticle(
    val startXFraction: Float,
    val speedMultiplier: Float,
    val swayAmplitude: Float,
    val initialRotation: Float,
    val sizePx: Float,
    val color: Color
)

@Composable
fun BotanicalConfettiOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val palette = listOf(ForestEmerald, ForestVibrant, ForestLimeSoft, ForestMintSoft, TerracottaAction)
    val particles = remember {
        List(36) { idx ->
            val rnd = Random(idx * 37 + 11)
            LeafParticle(
                startXFraction = rnd.nextFloat(),
                speedMultiplier = 0.7f + rnd.nextFloat() * 0.6f,
                swayAmplitude = 18f + rnd.nextFloat() * 34f,
                initialRotation = rnd.nextFloat() * 360f,
                sizePx = 14f + rnd.nextFloat() * 14f,
                color = palette[idx % palette.size]
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2600, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        if (t >= 1f) return@Canvas

        particles.forEach { p ->
            val y = (-40f) + (size.height + 120f) * (t * p.speedMultiplier)
            val x = p.startXFraction * size.width + sin(t * 10f + p.initialRotation) * p.swayAmplitude
            val rot = p.initialRotation + t * 320f

            rotate(degrees = rot, pivot = Offset(x, y)) {
                val path = Path().apply {
                    moveTo(x, y - p.sizePx / 2f)
                    quadraticTo(x + p.sizePx / 2f, y, x, y + p.sizePx / 2f)
                    quadraticTo(x - p.sizePx / 2f, y, x, y - p.sizePx / 2f)
                    close()
                }
                drawPath(path = path, color = p.color.copy(alpha = (1f - t).coerceIn(0f, 1f)))
            }
        }
    }
}
