/**
 * Codigo anadido por Felipe Denis
 *
 * Nodo UI de Sesion Diaria: Regla Operativa, Bloqueo Real DND + Detector Anti-Fuga,
 * Sugerencias de Micro-Pasos con Gemini 3.8 Flash, Temporizador Multi-Bloque,
 * Bucle de 3 Checks, Ignicion 3-2-1, Confeti de Hojas y Dialogo de Victoria.
 */
package com.utdevs.habitflow.ui.session

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.service.TimerStatus
import com.utdevs.habitflow.ui.components.dopamine.BotanicalConfettiOverlay
import com.utdevs.habitflow.ui.components.dopamine.DailyVictoryDialog
import com.utdevs.habitflow.ui.components.dopamine.IgnitionCountdownOverlay
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DailySessionScreen(
    goal: GoalSprint,
    profile: UserProfile,
    viewModel: DailySessionViewModel,
    onBack: () -> Unit,
    onDayCompleted: (isDay21: Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.onAppLeftDuringFocus()
                Lifecycle.Event.ON_RESUME -> viewModel.refreshDndPermission()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(goal, profile) {
        viewModel.bindState(goal, profile)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val isThisSessionTimer = timerState.sessionTag == viewModel.sessionTag
    val colors = LocalForestColors.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = colors.background,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                    title = { Text("${state.goal.title} | Dia ${state.daySpec.day}", color = colors.textPrimary) },
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
                if (state.liveXpMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.softSuccessBg, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = state.liveXpMessage.orEmpty(),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.primaryAction
                        )
                    }
                }

                // Escudo de Bloqueo Real (DND automatico en Android)
                if (!state.hasDndPermission) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.softTealBg),
                        border = BorderStroke(1.dp, colors.tealSecondary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.DoNotDisturbOn, contentDescription = null, tint = colors.tealSecondary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bloqueo Real No Molestar", style = MaterialTheme.typography.labelMedium, color = colors.textPrimary)
                                Text(
                                    "Autoriza DND para silenciar alertas al iniciar Enfoque y restaurarlas al terminar.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.textSecondary
                                )
                            }
                            OutlinedButton(onClick = { viewModel.openDndSettings(context) }) {
                                Text("Activar")
                            }
                        }
                    }
                }

                // Regla operativa y criterio de exito
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Regla Operativa", style = MaterialTheme.typography.titleMedium, color = colors.primaryAction)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(state.daySpec.rule, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Criterio de Exito: ${state.daySpec.successCriteria}", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)

                        if (state.goal.sprint.nextMicroTask.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Punto de partida (ayer): ${state.goal.sprint.nextMicroTask}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.vibrantSuccess
                            )
                        }
                    }
                }

                // Plan de Micro-Pasos Auto-Generado con Contexto de la Meta
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = colors.primaryAction,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Micro-pasos para tu meta",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = colors.textPrimary
                                )
                            }
                            TextButton(
                                onClick = { viewModel.requestAiMicroSteps(forTomorrow = false) },
                                enabled = !state.isGeneratingSteps
                            ) {
                                Text("Regenerar")
                            }
                        }

                        val sourceBadge = when {
                            state.isGeneratingSteps -> "Generando con Gemini para '${state.goal.title.ifBlank { state.goal.sprint.anchorTask }}'..."
                            state.stepsFromAi -> "IA activa (${state.stepsModelUsed}) - Contexto: ${state.goal.title.ifBlank { state.goal.sprint.anchorTask }}"
                            state.suggestedSteps.isNotEmpty() -> "Respaldo offline - Contexto: ${state.goal.title.ifBlank { state.goal.sprint.anchorTask }}"
                            else -> "Preparando ruta de arranque..."
                        }
                        Text(
                            text = sourceBadge,
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textSecondary
                        )

                        if (state.isGeneratingSteps && state.suggestedSteps.isEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Text(
                                    "Desglosando pasos accionables...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                            }
                        } else {
                            state.suggestedSteps.forEachIndexed { idx, step ->
                                Text(
                                    text = "${idx + 1}. $step",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }

                state.daySpec.specialEvent?.let { special ->
                    SpecialEventSection(
                        event = special,
                        specialInputValue = state.specialInputValue,
                        selectedFormatId = state.selectedFormatId,
                        onSpecialInputChange = viewModel::onSpecialInputChange,
                        onSelectFormat = viewModel::onSelectFormat
                    )
                }

                // Temporizador Multi-Bloque
                val activeBlock = if (isThisSessionTimer) timerState.currentBlock else state.resolvedBlocks.firstOrNull()
                val isBreak = activeBlock?.type == BlockType.BREAK
                val badgeBg = if (isBreak) colors.softTerracottaBg else colors.softSuccessBg
                val badgeFg = if (isBreak) colors.terracottaCta else colors.vibrantSuccess
                val badgeLabel = if (isBreak) "DESCANSO" else "ENFOQUE"

                val displayMillis = if (isThisSessionTimer && timerState.status != TimerStatus.IDLE) {
                    timerState.remainingMillis
                } else {
                    (state.resolvedBlocks.firstOrNull()?.durationMinutes ?: 5) * 60_000L
                }
                val totalSeconds = (displayMillis / 1000L).coerceAtLeast(0L)
                val timeFormatted = String.format("%02d:%02d", totalSeconds / 60L, totalSeconds % 60L)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .background(badgeBg, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                        ) {
                            Text(text = badgeLabel, color = badgeFg, style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.resolvedBlocks.joinToString(" -> ") { b ->
                                val tag = if (b.type == BlockType.FOCUS) "Enfoque" else "Descanso"
                                "$tag ${b.durationMinutes}m"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = timeFormatted,
                            style = TextStyle(
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum",
                                color = badgeFg
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val isRunning = isThisSessionTimer && timerState.status == TimerStatus.RUNNING
                            Button(
                                onClick = {
                                    if (isRunning) viewModel.pauseTimer(context)
                                    else viewModel.requestStartOrResume(context)
                                },
                                enabled = !state.checkExecution,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primaryAction,
                                    contentColor = colors.textOnAccent
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    when {
                                        state.checkExecution -> "Completado"
                                        isRunning -> "Pausar"
                                        isThisSessionTimer && timerState.status == TimerStatus.PAUSED -> "Reanudar"
                                        else -> "Iniciar"
                                    }
                                )
                            }

                            if (isThisSessionTimer && (timerState.status == TimerStatus.RUNNING || timerState.status == TimerStatus.PAUSED)) {
                                OutlinedButton(
                                    onClick = { viewModel.skipBlockTo5s(context) },
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Demo: Saltar a 5s")
                                }
                            }
                        }
                    }
                }

                // Bucle Diario de 3 Checks + Asistente Gemini 3.8 Flash
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Bucle Diario Obligatorio (3 Checks)", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = state.checkPrep, onCheckedChange = viewModel::onTogglePrep)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Check 1: Preparacion (+5 XP)", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = state.checkExecution, onCheckedChange = null, enabled = false)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Check 2: Bloque a 00:00", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = state.checkClosure, onCheckedChange = null, enabled = false)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Check 3: Micro-tarea para manana", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        }

                        OutlinedButton(
                            onClick = { viewModel.requestAiMicroSteps(forTomorrow = true) },
                            enabled = !state.isGeneratingSteps,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isGeneratingSteps) {
                                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generando micro-pasos...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sugerir cierre para manana (IA)")
                            }
                        }

                        if (state.suggestedSteps.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                state.suggestedSteps.forEach { suggestion ->
                                    AssistChip(
                                        onClick = { viewModel.applySuggestedStep(suggestion) },
                                        label = { Text(suggestion) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = state.closureTask,
                            onValueChange = viewModel::onClosureTaskChange,
                            label = { Text("Micro-tarea exacta con la que iniciaras manana") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (state.errorMessage != null) {
                    Text(state.errorMessage.orEmpty(), color = colors.terracottaCta)
                }

                Button(
                    onClick = { viewModel.finalizeDay(context) },
                    enabled = state.canFinalizeDay,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.terracottaCta,
                        contentColor = colors.textOnAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(if (state.isSaving) "Guardando..." else "Finalizar Dia")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (state.showIgnition) {
            IgnitionCountdownOverlay(
                taskTitle = state.goal.sprint.anchorTask,
                onFinished = { viewModel.onIgnitionFinished(context) }
            )
        }

        BotanicalConfettiOverlay(visible = state.showConfetti)

        state.victoryOutcome?.let { outcome ->
            DailyVictoryDialog(
                dayNumber = state.daySpec.day,
                outcome = outcome,
                onContinue = { viewModel.dismissVictoryDialog(onDayCompleted) }
            )
        }
    }
}
