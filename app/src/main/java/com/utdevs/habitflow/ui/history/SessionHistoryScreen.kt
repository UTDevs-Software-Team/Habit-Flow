/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI de Historial de Sesiones (inspirado en Pantalla 5 de referencia UI):
 * Grafica Donut segmentada de 785m por fase y timeline de sesiones con micro-tareas y auditorias.
 */
package com.utdevs.habitflow.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.ui.components.charts.PhaseDonutChartCanvas
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    goal: GoalSprint,
    viewModel: SessionHistoryViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(goal) {
        viewModel.bindState(goal)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalForestColors.current

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = { Text("Historial: ${state.goalTitle}", color = colors.textPrimary, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = colors.textPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PhaseDonutChartCanvas(
                        phase1Minutes = state.phase1Minutes,
                        phase2Minutes = state.phase2Minutes,
                        phase3Minutes = state.phase3Minutes,
                        targetTotalMinutes = state.targetTotalMinutes
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendDot("Fase 1 (${state.phase1Minutes}m)", colors.vibrantSuccess)
                        LegendDot("Fase 2 (${state.phase2Minutes}m)", colors.tealSecondary)
                        LegendDot("Fase 3 (${state.phase3Minutes}m)", colors.terracottaCta)
                    }
                }
            }

            Text(
                text = "Bitacora Cronologica (${state.logs.size} registros)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            if (state.logs.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Text(
                        text = "Completa tu primer bloque para registrar sesiones en la bitacora.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                state.logs.forEach { entry ->
                    val statusColor = if (entry.completed) colors.vibrantSuccess else colors.terracottaCta
                    val statusLabel = when {
                        entry.completed && entry.punctual -> "Completado (Puntual)"
                        entry.completed -> "Completado"
                        else -> "Omitido"
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                        border = BorderStroke(1.dp, colors.cardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Dia ${entry.day} | ${entry.effectiveFocusMinutes} min",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = statusLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = statusColor
                                )
                            }
                            if (entry.closureTask.isNotBlank()) {
                                Text(
                                    text = "Cierre: ${entry.closureTask}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                            if (!entry.specialInput.isNullOrBlank()) {
                                Text(
                                    text = "Nota especial: ${entry.specialInput}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.tealSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    val colors = LocalForestColors.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
    }
}
