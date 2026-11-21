/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI de Estadisticas y Veredicto Final (inspirado en Pantalla 4 de referencia UI):
 * Gauge semicircular de veredicto, Grafica de Area 5m->80m, KPIs y comparativa semanal.
 */
package com.utdevs.habitflow.ui.metrics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.VerdictTier
import com.utdevs.habitflow.ui.components.charts.FocusAreaChartCanvas
import com.utdevs.habitflow.ui.components.charts.VerdictGaugeCanvas
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsVerdictScreen(
    goal: GoalSprint,
    viewModel: MetricsViewModel,
    onBack: () -> Unit,
    onOpenDemoSheet: () -> Unit,
    onResolutionApplied: (VerdictTier) -> Unit
) {
    LaunchedEffect(goal) {
        viewModel.bindState(goal)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = state.summary
    val colors = LocalForestColors.current

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = { Text("Estadisticas: ${state.goal.title}", color = colors.textPrimary, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = colors.textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenDemoSheet) {
                        Icon(Icons.Default.Build, contentDescription = "Modo Demo", tint = colors.textSecondary)
                    }
                }
            )
        }
    ) { padding ->
        if (summary == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Gauge semicircular
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    VerdictGaugeCanvas(compliancePercentage = summary.compliancePercentage)
                }
            }

            // Grafica de Area 5m -> 80m
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Curva de Tolerancia al Enfoque", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FocusAreaChartCanvas(
                        dailyMinutes = state.dailySeriesMinutes,
                        completedFlags = state.dailyCompletedFlags
                    )
                }
            }

            // Tarjetas KPI 2x2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "Dias Completados",
                    value = "${summary.completedDaysCount}/21",
                    subtitle = "${summary.compliancePercentage.toInt()}% tasa",
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Minutos Enfocados",
                    value = "${summary.accumulatedFocusMinutes} m",
                    subtitle = "Meta: ${summary.targetTotalMinutes} m",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "Puntualidad",
                    value = "${summary.punctualityPercentage.toInt()}%",
                    subtitle = "Inicio en ventana",
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Ciclo / Racha",
                    value = "#${state.goal.sprint.cycleNumber}",
                    subtitle = "${state.goal.goalStreak} dias seguidos",
                    modifier = Modifier.weight(1f)
                )
            }

            // Tabla comparativa semanal
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Comparativa por Fase", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    summary.phaseComparison.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(row.phaseName, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary, modifier = Modifier.weight(1f))
                            Text(
                                "${row.completedDays}/${row.totalDays}d | ${row.accumulatedMinutes}/${row.targetMinutes}m",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.primaryAction
                            )
                        }
                        HorizontalDivider(color = colors.cardBorder)
                    }
                }
            }

            // Tarjeta de Veredicto Objetivo
            val (verdictBg, verdictAccent) = when (summary.verdictTier) {
                VerdictTier.HIGH_MASTERY -> colors.softSuccessBg to colors.vibrantSuccess
                VerdictTier.MODERATE_STABILITY -> colors.softTealBg to colors.tealSecondary
                VerdictTier.RECALIBRATION_NEEDED -> colors.softTerracottaBg to colors.terracottaCta
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = verdictBg),
                border = BorderStroke(1.dp, verdictAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(summary.recommendedActionTitle, style = MaterialTheme.typography.titleMedium, color = verdictAccent)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(summary.recommendedActionDescription, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                    Spacer(modifier = Modifier.height(14.dp))

                    val actionLabel = when (summary.verdictTier) {
                        VerdictTier.HIGH_MASTERY -> "Activar Modo Mantenimiento"
                        VerdictTier.MODERATE_STABILITY -> "Repetir Fase 3 (Dias 15-21)"
                        VerdictTier.RECALIBRATION_NEEDED -> "Reiniciar con Factor 0.5x"
                    }
                    Button(
                        onClick = { viewModel.executeRecommendedAction(onResolutionApplied) },
                        enabled = !state.isApplying,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verdictAccent,
                            contentColor = colors.textOnAccent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isApplying) "Aplicando..." else actionLabel)
                    }
                }
            }

            if (state.errorMessage != null) {
                Text(state.errorMessage.orEmpty(), color = colors.terracottaCta)
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, color = colors.primaryAction)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
        }
    }
}
