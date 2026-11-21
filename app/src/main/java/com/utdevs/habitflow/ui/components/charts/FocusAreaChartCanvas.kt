/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo Canvas aislado: Curva suave con relleno degradado de progresion de enfoque (5m -> 80m)
 * inspirada en la Pantalla 4 de referencia UI.
 */
package com.utdevs.habitflow.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun FocusAreaChartCanvas(
    dailyMinutes: List<Int>,
    completedFlags: List<Boolean>,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    val maxVal = (dailyMinutes.maxOrNull() ?: 80).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
        ) {
            if (dailyMinutes.size < 2) return@Canvas
            val topPad = 12.dp.toPx()
            val bottomY = size.height - 10.dp.toPx()
            val usableH = bottomY - topPad
            val stepX = size.width / (dailyMinutes.size - 1)

            val points = dailyMinutes.mapIndexed { idx, value ->
                val x = idx * stepX
                val y = bottomY - (value.toFloat() / maxVal.toFloat()) * usableH
                Offset(x, y)
            }

            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val midX = (prev.x + curr.x) / 2f
                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(points.last().x, bottomY)
                lineTo(points.first().x, bottomY)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.vibrantSuccess.copy(alpha = 0.36f),
                        Color.Transparent
                    )
                )
            )
            drawPath(
                path = strokePath,
                color = colors.primaryAction,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEachIndexed { idx, pt ->
                val done = completedFlags.getOrNull(idx) == true
                if (done) {
                    drawCircle(color = colors.vibrantSuccess, radius = 3.5.dp.toPx(), center = pt)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Dia 1 (5m)", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
            Text("Dia 11 (50m)", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
            Text("Dia 21 (50m)", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        }
    }
}
