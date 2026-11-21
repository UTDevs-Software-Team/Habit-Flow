/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI de Modo Mantenimiento con paleta Forest Light/Dark y soporte de 2da Tarea Ancla.
 */
package com.utdevs.habitflow.ui.maintenance

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.service.TimerStatus
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceModeScreen(
    goal: GoalSprint,
    viewModel: MaintenanceViewModel,
    onBackToHub: () -> Unit,
    onOpenMetrics: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(goal) {
        viewModel.bindState(goal)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val isMaintTimer = timerState.sessionTag == viewModel.sessionTag
    val colors = LocalForestColors.current

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = { Text("Mantenimiento: ${state.goal.title}", color = colors.textPrimary, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackToHub) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tus Metas", tint = colors.textPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = onOpenMetrics) { Text("Estadisticas", color = colors.primaryAction) }
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tareas Ancla Activas", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Text("Principal: ${state.goal.sprint.anchorTask}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)

                    OutlinedTextField(
                        value = state.secondaryAnchorDraft,
                        onValueChange = viewModel::onSecondaryDraftChange,
                        label = { Text("2da Tarea Ancla (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = viewModel::saveSecondaryAnchor,
                        enabled = state.secondaryAnchorDraft.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar 2da Tarea Ancla")
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sesion Libre de Mantenimiento", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)

                    if (!state.goal.sprint.secondaryAnchorTask.isNullOrBlank()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !state.useSecondaryAnchor,
                                onClick = { viewModel.selectAnchor(false) },
                                label = { Text("Tarea Principal") }
                            )
                            FilterChip(
                                selected = state.useSecondaryAnchor,
                                onClick = { viewModel.selectAnchor(true) },
                                label = { Text("2da Tarea Ancla") }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MaintenanceFormat.entries.forEach { fmt ->
                            FilterChip(
                                selected = state.selectedFormat == fmt,
                                onClick = { viewModel.selectFormat(fmt) },
                                label = { Text("${fmt.focusMin}/${fmt.breakMin}") }
                            )
                        }
                    }

                    val millis = if (isMaintTimer && timerState.status != TimerStatus.IDLE) {
                        timerState.remainingMillis
                    } else {
                        state.selectedFormat.focusMin * 60_000L
                    }
                    val totalSec = (millis / 1000L).coerceAtLeast(0L)
                    Text(
                        text = String.format("%02d:%02d", totalSec / 60L, totalSec % 60L),
                        style = TextStyle(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum",
                            color = colors.vibrantSuccess
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        val running = isMaintTimer && timerState.status == TimerStatus.RUNNING
                        Button(
                            onClick = {
                                when {
                                    running -> viewModel.pauseTimer(context)
                                    isMaintTimer && timerState.status == TimerStatus.PAUSED -> viewModel.resumeTimer(context)
                                    else -> viewModel.startMaintenanceSession(context)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primaryAction),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                when {
                                    running -> "Pausar"
                                    isMaintTimer && timerState.status == TimerStatus.PAUSED -> "Reanudar"
                                    else -> "Iniciar ${state.selectedFormat.focusMin}/${state.selectedFormat.breakMin}"
                                }
                            )
                        }

                        if (isMaintTimer && (timerState.status == TimerStatus.RUNNING || timerState.status == TimerStatus.PAUSED)) {
                            OutlinedButton(
                                onClick = { viewModel.skipTo5s(context) },
                                modifier = Modifier.weight(1f)
                            ) { Text("Demo: 5s") }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Sesiones Recientes (${state.recentSessions.size})", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    state.recentSessions.take(5).forEach { rec ->
                        Text("${rec.anchorTaskUsed} | ${rec.formatLabel}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    }
                }
            }

            OutlinedButton(
                onClick = viewModel::exitMaintenanceToSprint,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver al Panel de 21 Dias") }

            if (state.feedbackMessage != null) {
                Text(state.feedbackMessage.orEmpty(), color = colors.vibrantSuccess)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
