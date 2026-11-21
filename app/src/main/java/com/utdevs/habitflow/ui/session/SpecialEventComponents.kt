/**
 * Codigo anadido por Felipe Denis
 *
 * Componentes UI aislados para los Eventos Especiales condicionales (Dias 3, 7, 14 y 20)
 * usando LocalForestColors (Light / Dark Mode).
 */
package com.utdevs.habitflow.ui.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.domain.model.SpecialEvent
import com.utdevs.habitflow.ui.theme.LocalForestColors

@Composable
fun SpecialEventSection(
    event: SpecialEvent,
    specialInputValue: String,
    selectedFormatId: String?,
    onSpecialInputChange: (String) -> Unit,
    onSelectFormat: (String) -> Unit
) {
    val colors = LocalForestColors.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceCard),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Requisito Especial del Dia",
                style = MaterialTheme.typography.titleMedium,
                color = colors.primaryAction
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (event) {
                is SpecialEvent.TextInput -> {
                    OutlinedTextField(
                        value = specialInputValue,
                        onValueChange = onSpecialInputChange,
                        label = { Text(event.placeholder) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is SpecialEvent.AuditText -> {
                    OutlinedTextField(
                        value = specialInputValue,
                        onValueChange = onSpecialInputChange,
                        label = { Text(event.label) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is SpecialEvent.Rating1To5 -> {
                    Text(text = event.label, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (1..5).forEach { score ->
                            val selected = specialInputValue == score.toString()
                            OutlinedButton(
                                onClick = { onSpecialInputChange(score.toString()) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selected) colors.softSuccessBg else colors.surfaceCard
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(score.toString(), color = colors.textPrimary)
                            }
                        }
                    }
                }

                is SpecialEvent.FormatSelector -> {
                    Text(
                        text = "Elige el formato antes de iniciar el temporizador:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    event.options.forEach { option ->
                        val isSelected = selectedFormatId == option.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectFormat(option.id) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectFormat(option.id) }
                            )
                            Text(text = option.label, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        }
                    }
                }
            }
        }
    }
}
