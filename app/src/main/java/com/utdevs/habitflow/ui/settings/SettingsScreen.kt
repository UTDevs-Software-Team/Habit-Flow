/**
 * Codigo anadido por Felipe Denis
 *
 * Panel de Configuracion completo:
 * Perfil y Apodo, Tema (Claro/Oscuro/Sistema), Sonido, Haptica, Prueba de Celebracion,
 * Recordatorios y Cierre de Sesion.
 */
package com.utdevs.habitflow.ui.settings

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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.data.theme.AppearanceSettings
import com.utdevs.habitflow.data.theme.ForestThemeMode
import com.utdevs.habitflow.domain.model.MAX_ACTIVE_GOALS
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    profile: UserProfile,
    appearance: AppearanceSettings,
    activeGoalsCount: Int,
    onBack: () -> Unit,
    onSaveNickname: (String) -> Unit,
    onSelectThemeMode: (ForestThemeMode) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onTestCelebration: () -> Unit,
    onRescheduleReminders: () -> Unit,
    onSignOut: () -> Unit
) {
    val colors = LocalForestColors.current
    var nicknameDraft by remember(profile.nickname) { mutableStateOf(profile.nickname) }
    var savedMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
                title = { Text("Configuracion", color = colors.textPrimary) },
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
            // Perfil y Apodo
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Perfil del Usuario", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Text(
                        text = "Rango: ${profile.currentRank} | ${profile.totalXp} XP | Mejor racha: ${profile.bestStreak}d",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primaryAction
                    )
                    if (profile.email.isNotBlank()) {
                        Text("Cuenta: ${profile.email}", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    }
                    OutlinedTextField(
                        value = nicknameDraft,
                        onValueChange = { nicknameDraft = it; savedMsg = null },
                        label = { Text("Apodo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = {
                            onSaveNickname(nicknameDraft.trim())
                            savedMsg = "Apodo actualizado."
                        },
                        enabled = nicknameDraft.trim().length >= 2,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Guardar Apodo") }
                }
            }

            // Apariencia
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Apariencia (Paleta Forest)", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ForestThemeMode.entries.forEach { mode ->
                            val label = when (mode) {
                                ForestThemeMode.SYSTEM -> "Sistema"
                                ForestThemeMode.LIGHT -> "Claro"
                                ForestThemeMode.DARK -> "Oscuro"
                            }
                            FilterChip(
                                selected = appearance.themeMode == mode,
                                onClick = { onSelectThemeMode(mode) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            // Dopamina y Sensorial
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Dopamina y Sensorial", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sonido en celebraciones", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            Text("Tono suave al subir de nivel", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                        }
                        Switch(checked = appearance.soundEnabled, onCheckedChange = onToggleSound)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Vibracion haptica", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                            Text("Corta por +XP, larga + confeti al subir nivel", style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                        }
                        Switch(checked = appearance.hapticsEnabled, onCheckedChange = onToggleHaptics)
                    }

                    OutlinedButton(
                        onClick = onTestCelebration,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Probar Vibracion Larga y Confeti") }
                }
            }

            // Metas y Recordatorios
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
                border = BorderStroke(1.dp, colors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Metas y Disparadores", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                    Text(
                        text = "Metas activas: $activeGoalsCount / $MAX_ACTIVE_GOALS (tope anti-paralisis)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    OutlinedButton(
                        onClick = {
                            onRescheduleReminders()
                            savedMsg = "Recordatorios reprogramados para tus metas activas."
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Reprogramar Notificaciones (-2m y hora exacta)") }
                }
            }

            if (savedMsg != null) {
                Text(savedMsg.orEmpty(), color = colors.vibrantSuccess, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = onSignOut,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.terracottaCta,
                    contentColor = colors.textOnAccent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) { Text("Cerrar Sesion") }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
