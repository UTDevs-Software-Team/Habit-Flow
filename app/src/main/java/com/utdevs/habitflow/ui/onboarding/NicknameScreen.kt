/**
 * Codigo anadido por Felipe Denis
 *
 * Pantalla de bienvenida inicial solicitando el apodo ("Como quieres que te llamemos?").
 */
package com.utdevs.habitflow.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun NicknameScreen(
    viewModel: NicknameViewModel,
    onSignOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalForestColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
            border = BorderStroke(1.dp, colors.cardBorder)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Como quieres que te llamemos?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = "Personalizar tu espacio aumenta tu vinculo con cada sesion. Podras cambiarlo despues.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                OutlinedTextField(
                    value = state.nickname,
                    onValueChange = viewModel::onNicknameChange,
                    label = { Text("Tu nombre o apodo") },
                    placeholder = { Text("Ej. Denis") },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage.orEmpty(),
                        color = colors.terracottaCta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = viewModel::saveNickname,
                    enabled = !state.isSaving && state.nickname.trim().length >= 2,
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryAction,
                        contentColor = colors.textOnAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(if (state.isSaving) "Guardando..." else "Entrar a Mis Metas")
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onSignOut,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cambiar de cuenta", color = colors.textSecondary)
                }
            }
        }
    }
}
