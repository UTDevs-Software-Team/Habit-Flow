/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo de Asset Visual Vectorial: Arbol Evolutivo en 9 etapas con sistema de raices,
 * monticulo de tierra organica, floracion y frutos (basado en los vectores de la raiz)
 * y soporte de salud recuperable "Cero Culpa" (THRIVING vs NEEDS_WATER).
 */
package com.utdevs.habitflow.ui.components.plant

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.PlantStage
import com.utdevs.habitflow.ui.theme.ForestLimeSoft
import com.utdevs.habitflow.ui.theme.LocalForestColors
import com.utdevs.habitflow.ui.theme.TerracottaAction

@Composable
fun VirtualPlantTerrariumCanvas(
    stage: PlantStage,
    completedCount: Int,
    healthState: PlantHealthState = PlantHealthState.THRIVING,
    isMaintenanceMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    val visualStep = when {
        isMaintenanceMode -> 10
        completedCount <= 1 -> 1
        completedCount <= 3 -> 2
        completedCount <= 5 -> 3
        completedCount <= 8 -> 4
        completedCount <= 11 -> 5
        completedCount <= 14 -> 6
        completedCount <= 17 -> 7
        completedCount <= 20 -> 8
        else -> 9
    }

    val growthScale by animateFloatAsState(
        targetValue = visualStep / 10f,
        label = "tree_growth_10"
    )
    val foliageColor = if (healthState == PlantHealthState.NEEDS_WATER) {
        colors.clayNeedsWater
    } else {
        colors.vibrantSuccess
    }
    val blossomColor = if (healthState == PlantHealthState.NEEDS_WATER) {
        colors.clayNeedsWater.copy(alpha = 0.7f)
    } else {
        Color(0xFFF6ECC9)
    }
    val fruitColor = TerracottaAction

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(112.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val soilY = h * 0.68f

            // Aro exterior sutil
            drawCircle(
                color = colors.cardBorder,
                radius = w * 0.48f,
                center = Offset(cx, h * 0.50f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Monticulo de tierra organica (como el vector de referencia)
            val soilWidth = w * (0.34f + 0.28f * growthScale)
            val soilHeight = h * (0.14f + 0.14f * growthScale)
            drawArc(
                color = colors.barkTrunk,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - soilWidth / 2f, soilY - soilHeight / 2f),
                size = Size(soilWidth, soilHeight * 1.6f)
            )

            // Cesped sobre la tierra (etapas 3..9)
            if (visualStep >= 3) {
                for (i in -3..3) {
                    val gx = cx + i * (soilWidth * 0.12f)
                    drawLine(
                        color = foliageColor,
                        start = Offset(gx, soilY),
                        end = Offset(gx + (i * 1.2f), soilY - 6f),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Raices ramificadas dentro de la tierra (etapas 2..9)
            if (visualStep >= 2) {
                val rootDepth = soilHeight * 0.65f
                drawLine(
                    color = colors.clayNeedsWater,
                    start = Offset(cx, soilY + 2f),
                    end = Offset(cx, soilY + rootDepth),
                    strokeWidth = 2.2f,
                    cap = StrokeCap.Round
                )
                val branchCount = (visualStep - 1).coerceAtMost(6)
                for (r in 1..branchCount) {
                    val side = if (r % 2 == 0) 1f else -1f
                    val ry = soilY + (rootDepth * (r / (branchCount + 1f)))
                    drawLine(
                        color = colors.clayNeedsWater,
                        start = Offset(cx, ry),
                        end = Offset(cx + side * (soilWidth * 0.28f), ry + 6f),
                        strokeWidth = 1.4f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Parte aerea segun etapa 1..9
            when {
                visualStep == 1 -> {
                    drawCircle(color = fruitColor, radius = 5f, center = Offset(cx, soilY - 2f))
                }
                visualStep == 2 -> {
                    drawCircle(color = fruitColor, radius = 5f, center = Offset(cx, soilY - 2f))
                    drawLine(
                        color = foliageColor,
                        start = Offset(cx, soilY - 4f),
                        end = Offset(cx + 4f, soilY - 12f),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }
                visualStep in 3..5 -> {
                    val stemH = h * (0.16f + 0.08f * (visualStep - 2))
                    val topY = soilY - stemH
                    drawLine(
                        color = if (visualStep == 5) colors.barkTrunk else foliageColor,
                        start = Offset(cx, soilY),
                        end = Offset(cx, topY),
                        strokeWidth = if (visualStep == 5) 4f else 2.8f,
                        cap = StrokeCap.Round
                    )
                    val leaves = visualStep * 2
                    for (i in 0 until leaves) {
                        val isLeft = i % 2 == 0
                        val ly = soilY - stemH * (0.35f + 0.12f * i).coerceAtMost(0.95f)
                        val dx = if (isLeft) -12f else 12f
                        val leaf = Path().apply {
                            moveTo(cx, ly)
                            quadraticTo(cx + dx, ly - 8f, cx + dx * 1.1f, ly - 2f)
                            quadraticTo(cx + dx * 0.4f, ly + 4f, cx, ly)
                        }
                        drawPath(leaf, color = foliageColor)
                    }
                }
                else -> {
                    // Etapas 6..9: Arbol joven -> maduro -> floracion -> manzano con frutos
                    val trunkH = h * (0.28f + 0.04f * (visualStep - 6))
                    val crownCenterY = soilY - trunkH
                    drawLine(
                        color = colors.barkTrunk,
                        start = Offset(cx, soilY),
                        end = Offset(cx, crownCenterY + 6f),
                        strokeWidth = if (visualStep >= 8) 7f else 5.5f,
                        cap = StrokeCap.Round
                    )
                    // Ramas principales
                    drawLine(colors.barkTrunk, Offset(cx, crownCenterY + 12f), Offset(cx - 12f, crownCenterY), strokeWidth = 3f)
                    drawLine(colors.barkTrunk, Offset(cx, crownCenterY + 12f), Offset(cx + 12f, crownCenterY), strokeWidth = 3f)

                    val canopyColor = if (visualStep == 8) blossomColor else foliageColor
                    val rMain = 18f + (visualStep - 6) * 3f
                    drawCircle(canopyColor, radius = rMain, center = Offset(cx, crownCenterY - 4f))
                    drawCircle(canopyColor, radius = rMain * 0.78f, center = Offset(cx - 15f, crownCenterY + 3f))
                    drawCircle(canopyColor, radius = rMain * 0.78f, center = Offset(cx + 15f, crownCenterY + 3f))

                    // Flores blancas/crema en etapa 8
                    if (visualStep == 8) {
                        listOf(
                            Offset(cx - 10f, crownCenterY - 6f),
                            Offset(cx + 10f, crownCenterY - 4f),
                            Offset(cx, crownCenterY + 4f),
                            Offset(cx - 16f, crownCenterY + 5f),
                            Offset(cx + 16f, crownCenterY + 4f)
                        ).forEach { pt ->
                            drawCircle(Color.White, radius = 3.2f, center = pt)
                            drawCircle(ForestLimeSoft, radius = 1.4f, center = pt)
                        }
                    }

                    // Frutos terracota/manzanas en etapa 9 y 10
                    if (visualStep >= 9) {
                        listOf(
                            Offset(cx - 11f, crownCenterY - 6f),
                            Offset(cx + 9f, crownCenterY - 8f),
                            Offset(cx, crownCenterY + 3f),
                            Offset(cx - 15f, crownCenterY + 5f),
                            Offset(cx + 14f, crownCenterY + 4f)
                        ).forEach { pt ->
                            drawCircle(fruitColor, radius = 3.5f, center = pt)
                        }
                    }

                    // Cajas de cosecha en etapa 10 (Modo Mantenimiento)
                    if (visualStep == 10) {
                        drawRoundRect(
                            color = colors.clayNeedsWater,
                            topLeft = Offset(cx - 28f, soilY - 9f),
                            size = Size(16f, 9f)
                        )
                        drawRoundRect(
                            color = colors.clayNeedsWater,
                            topLeft = Offset(cx + 12f, soilY - 9f),
                            size = Size(16f, 9f)
                        )
                        drawCircle(fruitColor, radius = 2.6f, center = Offset(cx - 22f, soilY - 10f))
                        drawCircle(fruitColor, radius = 2.6f, center = Offset(cx - 17f, soilY - 10f))
                        drawCircle(fruitColor, radius = 2.6f, center = Offset(cx + 17f, soilY - 10f))
                        drawCircle(fruitColor, radius = 2.6f, center = Offset(cx + 22f, soilY - 10f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = if (visualStep == 10) {
                    "Arbol Etapa 10/10 (Cosecha Activa)"
                } else {
                    "Arbol Etapa $visualStep/10 (${stage.title})"
                },
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "$completedCount / 21 dias completados",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            if (healthState == PlantHealthState.NEEDS_WATER) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Necesita riego hoy (completa tu bloque para hidratarla)",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.terracottaCta
                )
            }
        }
    }
}
