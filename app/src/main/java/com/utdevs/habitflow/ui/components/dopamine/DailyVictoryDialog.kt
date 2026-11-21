/**
 * Codigo anadido por Felipe Denis
 *
 * Dialogo de victoria al cerrar el dia con desglose de dopamina:
 * XP base, Bono Fenix, Bono Sorpresa RPE Positivo, Subida de Nivel y Logros.
 */
package com.utdevs.habitflow.ui.components.dopamine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.domain.model.DayRewardOutcome
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun DailyVictoryDialog(
    dayNumber: Int,
    outcome: DayRewardOutcome,
    onContinue: () -> Unit
) {
    val colors = LocalForestColors.current

    AlertDialog(
        onDismissRequest = onContinue,
        title = {
            Text(
                text = if (outcome.leveledUp) "Subiste de Nivel en tu Meta" else "Dia $dayNumber Completado",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "+${outcome.totalXpEarned} XP ganados (Total: ${outcome.newTotalXp} XP)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.vibrantSuccess
                )
                Text(
                    text = outcome.rewardMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                if (outcome.leveledUp) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.softSuccessBg, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Nivel alcanzado: ${outcome.newPlantStage.title} | Rango: ${outcome.newRank}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.primaryAction
                        )
                    }
                }

                if (outcome.surpriseRpeBonusXp > 0) {
                    Text(
                        text = "Bono Sorpresa RPE Positivo: +${outcome.surpriseRpeBonusXp} XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.terracottaCta
                    )
                }

                if (outcome.phoenixBonusXp > 0) {
                    Text(
                        text = "Bono Efecto Fenix (retorno sin culpa): +${outcome.phoenixBonusXp} XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.tealSecondary
                    )
                }

                if (outcome.shieldEarned) {
                    Text(
                        text = "Nuevo Escudo de Racha obtenido (protege ante 1 dia omitido)",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primaryAction
                    )
                }

                if (outcome.newlyUnlockedAchievements.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Logros desbloqueados:", style = MaterialTheme.typography.titleMedium)
                    outcome.newlyUnlockedAchievements.forEach { ach ->
                        Text(
                            text = "- ${ach.title} (+${ach.bonusXp} XP): ${ach.description}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Continuar")
            }
        }
    )
}
