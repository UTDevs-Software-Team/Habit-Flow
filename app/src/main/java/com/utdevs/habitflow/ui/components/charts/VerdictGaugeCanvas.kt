/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo Canvas aislado: Medidor semicircular Gauge de Veredicto (0-49%, 50-79%, 80-100%)
 * inspirado en la Pantalla 4 de referencia UI.
 */
package com.utdevs.habitflow.ui.components.charts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.ui.theme.LocalForestColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VerdictGaugeCanvas(
    compliancePercentage: Double,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    val clamped = compliancePercentage.toFloat().coerceIn(0f, 100f)
    val animatedPct by animateFloatAsState(targetValue = clamped, label = "gauge_pct")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(width = 210.dp, height = 116.dp)) {
            val strokeW = 16.dp.toPx()
            val arcSize = Size(size.width - strokeW, (size.width - strokeW))
            val topLeft = Offset(strokeW / 2f, strokeW / 2f)
            val stroke = Stroke(width = strokeW, cap = StrokeCap.Round)

            // 3 zonas: <50% (Terracota), 50-79% (TealPine), >=80% (ForestVibrant)
            drawArc(
                color = colors.terracottaCta.copy(alpha = 0.35f),
                startAngle = 180f,
                sweepAngle = 86f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = colors.tealSecondary.copy(alpha = 0.45f),
                startAngle = 270f,
                sweepAngle = 50f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = colors.vibrantSuccess,
                startAngle = 324f,
                sweepAngle = 36f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // Aguja apuntando al porcentaje actual
            val angleDeg = 180f + (animatedPct / 100f) * 180f
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val center = Offset(size.width / 2f, size.width / 2f)
            val radius = (size.width - strokeW * 2.2f) / 2f
            val tip = Offset(
                x = center.x + (radius * cos(angleRad)).toFloat(),
                y = center.y + (radius * sin(angleRad)).toFloat()
            )
            drawLine(
                color = colors.textPrimary,
                start = center,
                end = tip,
                strokeWidth = 3.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(color = colors.textPrimary, radius = 6.dp.toPx(), center = center)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${clamped.toInt()}% Cumplimiento",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary
        )
    }
}
