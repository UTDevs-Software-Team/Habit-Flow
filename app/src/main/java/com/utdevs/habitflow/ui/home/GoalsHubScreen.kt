/**
 * Codigo anadido por Felipe Denis
 *
 * Pantalla Principal Hub: "Bienvenido, [Apodo]", boton superior "+ Nueva Meta"
 * y tarjetas autogenerables dinamicamente para "Tus Metas" (max 3 activas).
 */
package com.utdevs.habitflow.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.PlantHealthState
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsHubScreen(
    profile: UserProfile,
    goals: List<GoalSprint>,
    soundEnabled: Boolean,
    viewModel: GoalsHubViewModel,
    onToggleTheme: (currentlyDark: Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenGoal: (String) -> Unit,
    onSignOut: () -> Unit
) {
    LaunchedEffect(profile, goals) {
        viewModel.bind(profile, goals)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalForestColors.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = { Text("HabitFlow", color = colors.textPrimary) },
                actions = {
                    IconButton(onClick = { onToggleSound(!soundEnabled) }) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Alternar sonido",
                            tint = colors.textSecondary
                        )
                    }
                    IconButton(onClick = { onToggleTheme(colors.isDark) }) {
                        Icon(
                            imageVector = if (colors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Modo Claro o Oscuro",
                            tint = colors.textPrimary
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuracion",
                            tint = colors.textPrimary
                        )
                    }
                    TextButton(onClick = onSignOut) {
                        Text("Salir", color = colors.textSecondary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Boton prominente arriba del todo: + Nueva Meta
            Button(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.terracottaCta,
                    contentColor = colors.textOnAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva Meta (${state.activeGoalsCount}/${state.maxActiveGoals} activas)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Tarjeta de Bienvenida + Economia de Dopamina
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(listOf(colors.heroStart, colors.heroEnd)),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bienvenido, ${state.profile.nickname.ifBlank { "Viajero" }}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = colors.textOnAccent
                            )
                            Text(
                                text = "Rango: ${state.profile.currentRank} | ${state.profile.totalXp} XP",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textOnAccent.copy(alpha = 0.88f)
                            )
                        }
                        IconButton(onClick = { showNicknameDialog = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar apodo",
                                tint = colors.textOnAccent
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(colors.textOnAccent.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Racha: ${state.profile.globalStreak} dias",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textOnAccent
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(colors.textOnAccent.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = colors.textOnAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Escudos: ${state.profile.streakShields}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.textOnAccent
                            )
                        }
                    }

                    val xpProgress = (state.profile.totalXp.toFloat() / state.nextRankMinXp.coerceAtLeast(1).toFloat())
                        .coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { xpProgress },
                        color = colors.textOnAccent,
                        trackColor = colors.textOnAccent.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )
                    Text(
                        text = "Siguiente rango: ${state.nextRankTitle} (${state.profile.totalXp}/${state.nextRankMinXp} XP)",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textOnAccent.copy(alpha = 0.85f)
                    )
                }
            }

            if (state.feedbackMessage != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.softTerracottaBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.feedbackMessage.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.terracottaCta,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::clearFeedback) { Text("OK") }
                    }
                }
            }

            // Seccion autogenerable "Tus Metas"
            Text(
                text = "Tus Metas (${state.items.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            if (state.items.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Aun no tienes metas activas.",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Pulsa el boton superior '+ Nueva Meta' para comenzar tu primer sprint de 21 dias con 5 minutos hoy.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            } else {
                state.items.forEach { item ->
                    GoalAutoCard(
                        item = item,
                        onOpen = { onOpenGoal(item.goal.goalId) },
                        onTogglePause = { viewModel.toggleGoalPause(item.goal) }
                    )
                }
            }

            // Panel de Logros Conductuales
            if (state.allAchievements.isNotEmpty()) {
                Text(
                    text = "Logros Conductuales (${state.profile.unlockedAchievements.size}/${state.allAchievements.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                    border = BorderStroke(1.dp, colors.cardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.allAchievements.forEach { ach ->
                            val unlocked = ach.id in state.profile.unlockedAchievements
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (unlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (unlocked) colors.vibrantSuccess else colors.mutedLock,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${ach.title} (+${ach.bonusXp} XP)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (unlocked) colors.textPrimary else colors.textSecondary
                                    )
                                    Text(
                                        text = ach.description,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCreateDialog) {
        CreateGoalDialog(
            canActivateMore = state.canActivateMore,
            activeCount = state.activeGoalsCount,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, params ->
                showCreateDialog = false
                viewModel.createGoal(title, params, onCreated = onOpenGoal)
            }
        )
    }

    if (showNicknameDialog) {
        EditNicknameDialog(
            current = state.profile.nickname,
            onDismiss = { showNicknameDialog = false },
            onSave = { updated ->
                viewModel.updateNickname(updated)
                showNicknameDialog = false
            }
        )
    }
}

@Composable
private fun GoalAutoCard(
    item: GoalHubItemUi,
    onOpen: () -> Unit,
    onTogglePause: () -> Unit
) {
    val colors = LocalForestColors.current
    val goal = item.goal
    val isPaused = goal.status == GoalStatus.PAUSED

    val statusText = when {
        isPaused -> "En Pausa"
        goal.status == GoalStatus.MAINTENANCE -> "Modo Mantenimiento"
        item.healthState == PlantHealthState.NEEDS_WATER -> "Necesita riego hoy"
        else -> "Activa | ${item.plantStage.title}"
    }
    val statusColor = when {
        isPaused -> colors.mutedLock
        item.healthState == PlantHealthState.NEEDS_WATER -> colors.terracottaCta
        else -> colors.vibrantSuccess
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(58.dp)) {
                CircularProgressIndicator(
                    progress = { item.progressFraction },
                    color = statusColor,
                    trackColor = colors.surfaceSubtle,
                    strokeWidth = 6.dp,
                    modifier = Modifier.size(58.dp)
                )
                Text(
                    text = "${goal.sprint.completedDays.size}/21",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
                Text(
                    text = "Hora: ${goal.sprint.triggerTime} | Racha: ${goal.goalStreak}d",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onTogglePause) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Reactivar meta" else "Pausar meta",
                        tint = colors.textSecondary
                    )
                }
                OutlinedButton(
                    onClick = onOpen,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Abrir")
                }
            }
        }
    }
}

@Composable
private fun EditNicknameDialog(
    current: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var draft by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Como quieres que te llamemos?") },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Apodo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draft) },
                enabled = draft.trim().length >= 2
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
