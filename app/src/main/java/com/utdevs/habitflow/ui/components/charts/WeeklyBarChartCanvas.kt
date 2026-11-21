/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo Canvas aislado: Grafica de barras verticales de impulso semanal/por fase
 * inspirada en la Pantalla 3 de referencia UI.
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.ui.theme.LocalForestColors

data class BarDatum(
    val label: String,
    val value: Int,
    val completed: Boolean
)

@Composable
fun WeeklyBarChartCanvas(
    items: List<BarDatum>,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    val maxVal = (items.maxOfOrNull { it.value } ?: 1).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
        ) {
            if (items.isEmpty()) return@Canvas
            val slotWidth = size.width / items.size
            val barWidth = (slotWidth * 0.52f).coerceAtMost(28.dp.toPx())
            val baselineY = size.height - 4.dp.toPx()

            drawLine(
                color = colors.cardBorder,
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 1.dp.toPx()
            )

            items.forEachIndexed { index, datum ->
                val x = index * slotWidth + (slotWidth - barWidth) / 2f
                val ratio = (datum.value.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f)
                val barHeight = (baselineY - 8.dp.toPx()) * ratio
                val barColor = when {
                    datum.completed -> colors.vibrantSuccess
                    else -> colors.tealSecondary.copy(alpha = 0.28f)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, baselineY - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { datum ->
                Text(
                    text = datum.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary
                )
            }
        }
    }
}
