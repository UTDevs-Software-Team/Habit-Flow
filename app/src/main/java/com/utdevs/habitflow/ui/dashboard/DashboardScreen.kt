/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI del Dashboard de Meta en Bento Grid "Botanical FinTech" (inspirado en Pantalla 3):
 * KPI Minutos/XP, Hero Zeigarnik, Terrario Vivo, Barras Semanales, Donuts y Cuadricula 21 Dias.
 */
package com.utdevs.habitflow.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.DayStatus
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.ui.components.charts.WeeklyBarChartCanvas
import com.utdevs.habitflow.ui.components.plant.VirtualPlantTerrariumCanvas
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    goal: GoalSprint,
    profile: UserProfile,
    viewModel: DashboardViewModel,
    onBackToHub: () -> Unit,
    onStartDay: (Int) -> Unit,
    onOpenMetrics: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDemoSheet: () -> Unit
) {
    LaunchedEffect(goal, profile) {
        viewModel.bindState(goal, profile)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalForestColors.current
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = {
                    Text(
                        text = state.goal.title.ifBlank { "Meta" },
                        color = colors.textPrimary,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHub) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a Tus Metas",
                            tint = colors.textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenMetrics) {
                        Icon(Icons.Default.BarChart, contentDescription = "Estadisticas", tint = colors.textPrimary)
                    }
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial", tint = colors.textPrimary)
                    }
                    IconButton(onClick = onOpenDemoSheet) {
                        Icon(Icons.Default.Build, contentDescription = "Modo Demo", tint = colors.textSecondary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fila Bento Superior 2 Columnas (Referencia Screen 3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Enfoque Total", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${state.accumulatedMinutes} m",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Meta: ${state.targetTotalMinutes}m",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+${state.goal.goalXp} XP | ${state.goal.goalStreak}d racha",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.vibrantSuccess
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(listOf(colors.heroStart, colors.heroEnd)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                        Column {
                            Text(
                                text = "Micro-tarea para hoy",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textOnAccent.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.goal.sprint.nextMicroTask.ifBlank {
                                    "Paso minimo: ${state.goal.sprint.anchorTask}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textOnAccent,
                                maxLines = 3
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onStartDay(state.activeDay) },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.terracottaCta,
                                contentColor = colors.textOnAccent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dia ${state.activeDay}")
                        }
                    }
                }
            }

            // Terrario Vivo + Barra de Progreso
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    VirtualPlantTerrariumCanvas(
                        stage = state.plantStage,
                        completedCount = state.goal.sprint.completedDays.size,
                        healthState = state.plantHealth,
                        isMaintenanceMode = state.goal.sprint.inMaintenanceMode
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { state.progressPercent / 100f },
                        color = colors.vibrantSuccess,
                        trackColor = colors.surfaceSubtle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }
            }

            // Grafica de Barras Semanal
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Impulso de la Semana Activa (min objetivo)",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    WeeklyBarChartCanvas(items = state.weeklyBars)
                }
            }

            // Fila inferior 2 columnas con mini-donuts: Puntualidad % y Cumplimiento %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniDonutCard(
                    title = "Puntualidad",
                    percent = state.punctualityPercent,
                    accentColor = colors.tealSecondary,
                    modifier = Modifier.weight(1f)
                )
                MiniDonutCard(
                    title = "Cumplimiento",
                    percent = state.progressPercent,
                    accentColor = colors.vibrantSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            // Parametros Dia 0 editables
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Contrato Dia 0", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = colors.textSecondary)
                        }
                    }
                    Text("Tarea Ancla: ${state.goal.sprint.anchorTask}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    Text(
                        "Hora: ${state.goal.sprint.triggerTime} | Lugar: ${state.goal.sprint.triggerLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Text("Bloqueo: ${state.goal.sprint.blockingRule}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                }
            }

            // Cuadricula de 21 Dias por 3 Fases
            state.phases.forEach { section ->
                val phaseAccent = when (section.phaseSpec.phaseId) {
                    1 -> colors.vibrantSuccess
                    2 -> colors.tealSecondary
                    else -> colors.terracottaCta
                }
                val phaseBg = when (section.phaseSpec.phaseId) {
                    1 -> colors.softSuccessBg
                    2 -> colors.softTealBg
                    else -> colors.softTerracottaBg
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${section.phaseSpec.name} (${section.phaseSpec.daysRangeLabel})",
                            style = MaterialTheme.typography.titleMedium,
                            color = phaseAccent
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            section.dayCards.forEach { card ->
                                DayGridTile(
                                    card = card,
                                    phaseAccent = phaseAccent,
                                    phaseBg = phaseBg,
                                    onClick = {
                                        if (card.status == DayStatus.ACTIVE) onStartDay(card.day)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showEditDialog) {
        EditGoalDialog(
            initialTitle = state.goal.title,
            initialParams = DayZeroParams(
                anchorTask = state.goal.sprint.anchorTask,
                triggerTime = state.goal.sprint.triggerTime,
                triggerLocation = state.goal.sprint.triggerLocation,
                blockingRule = state.goal.sprint.blockingRule
            ),
            onDismiss = { showEditDialog = false },
            onSave = { title, params ->
                viewModel.updateGoalParams(title, params)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun MiniDonutCard(
    title: String,
    percent: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalForestColors.current
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(48.dp)) {
                CircularProgressIndicator(
                    progress = { (percent / 100f).coerceIn(0f, 1f) },
                    color = accentColor,
                    trackColor = colors.surfaceSubtle,
                    strokeWidth = 5.dp,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
        }
    }
}

@Composable
private fun DayGridTile(
    card: DashboardDayCardModel,
    phaseAccent: Color,
    phaseBg: Color,
    onClick: () -> Unit
) {
    val colors = LocalForestColors.current
    val bgColor = when (card.status) {
        DayStatus.COMPLETED -> colors.softSuccessBg
        DayStatus.ACTIVE -> phaseBg
        DayStatus.MISSED -> colors.softTerracottaBg
        DayStatus.LOCKED -> colors.surfaceSubtle
    }
    val fgColor = when (card.status) {
        DayStatus.COMPLETED -> colors.vibrantSuccess
        DayStatus.ACTIVE -> phaseAccent
        DayStatus.MISSED -> colors.terracottaCta
        DayStatus.LOCKED -> colors.mutedLock
    }
    val alpha = if (card.status == DayStatus.LOCKED) 0.58f else 1f

    Box(
        modifier = Modifier
            .width(92.dp)
            .alpha(alpha)
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(enabled = card.status == DayStatus.ACTIVE, onClick = onClick)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dia ${card.day}", style = MaterialTheme.typography.titleMedium, color = fgColor)
                when (card.status) {
                    DayStatus.COMPLETED -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = fgColor, modifier = Modifier.size(16.dp))
                    DayStatus.LOCKED -> Icon(Icons.Default.Lock, contentDescription = null, tint = fgColor, modifier = Modifier.size(16.dp))
                    else -> {}
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${card.effectiveFocusMinutes} min", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
            Text(
                text = when (card.status) {
                    DayStatus.COMPLETED -> "Hecho"
                    DayStatus.ACTIVE -> "Disponible"
                    DayStatus.MISSED -> "Omitido"
                    DayStatus.LOCKED -> "Bloqueado"
                },
                style = MaterialTheme.typography.labelMedium,
                color = fgColor
            )
        }
    }
}

@Composable
private fun EditGoalDialog(
    initialTitle: String,
    initialParams: DayZeroParams,
    onDismiss: () -> Unit,
    onSave: (String, DayZeroParams) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var anchor by remember { mutableStateOf(initialParams.anchorTask) }
    var time by remember { mutableStateOf(initialParams.triggerTime) }
    var location by remember { mutableStateOf(initialParams.triggerLocation) }
    var blocking by remember { mutableStateOf(initialParams.blockingRule) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Meta y Dia 0") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titulo de la meta") })
                OutlinedTextField(value = anchor, onValueChange = { anchor = it }, label = { Text("Tarea Ancla") })
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Hora (HH:mm)") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lugar fisico") })
                OutlinedTextField(value = blocking, onValueChange = { blocking = it }, label = { Text("Regla de bloqueo") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, DayZeroParams(anchor, time, location, blocking)) },
                enabled = title.isNotBlank() && DayZeroParams(anchor, time, location, blocking).isValid()
            ) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
