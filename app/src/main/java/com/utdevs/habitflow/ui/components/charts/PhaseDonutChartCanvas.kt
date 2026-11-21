/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo Canvas aislado: Grafica Donut segmentada de 785 min totales por fase
 * inspirada en la Pantalla 5 (Historial) de la referencia UI.
 */
package com.utdevs.habitflow.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun PhaseDonutChartCanvas(
    phase1Minutes: Int,
    phase2Minutes: Int,
    phase3Minutes: Int,
    targetTotalMinutes: Int,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    val totalDone = phase1Minutes + phase2Minutes + phase3Minutes
    val denominator = targetTotalMinutes.coerceAtLeast(1).toFloat()

    Box(modifier = modifier.size(164.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(164.dp)) {
            val strokeW = 18.dp.toPx()
            val stroke = Stroke(width = strokeW, cap = StrokeCap.Butt)

            drawArc(
                color = colors.cardBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )

            val sweep1 = (phase1Minutes / denominator) * 360f
            val sweep2 = (phase2Minutes / denominator) * 360f
            val sweep3 = (phase3Minutes / denominator) * 360f

            var start = -90f
            if (sweep1 > 0f) {
                drawArc(colors.vibrantSuccess, start, sweep1, false, style = stroke)
                start += sweep1
            }
            if (sweep2 > 0f) {
                drawArc(colors.tealSecondary, start, sweep2, false, style = stroke)
                start += sweep2
            }
            if (sweep3 > 0f) {
                drawArc(colors.terracottaCta, start, sweep3, false, style = stroke)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$totalDone m",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )
            Text(
                text = "de $targetTotalMinutes m",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
        }
    }
}
