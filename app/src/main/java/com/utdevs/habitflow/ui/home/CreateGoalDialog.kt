/**
 * Codigo anadido por Felipe Denis
 *
 * Dialogo modal para crear una Nueva Meta (Titulo + 4 parametros del Dia 0 + TimePicker).
 */
package com.utdevs.habitflow.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.domain.model.DayZeroParams
import com.utdevs.habitflow.domain.model.MAX_ACTIVE_GOALS
import com.utdevs.habitflow.ui.theme.LocalForestColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateGoalDialog(
    canActivateMore: Boolean,
    activeCount: Int,
    onDismiss: () -> Unit,
    onCreate: (title: String, params: DayZeroParams) -> Unit
) {
    val colors = LocalForestColors.current
    var title by remember { mutableStateOf("") }
    var anchorTask by remember { mutableStateOf("") }
    var triggerTime by remember { mutableStateOf("17:00") }
    var triggerLocation by remember { mutableStateOf("") }
    var blockingRule by remember { mutableStateOf("DND automatico + Alerta de salida") }
    var showTimePicker by remember { mutableStateOf(false) }

    val blockingPresets = listOf(
        "DND automatico + Alerta de salida",
        "Solo DND automatico",
        "Modo avion manual"
    )

    val params = DayZeroParams(anchorTask, triggerTime, triggerLocation, blockingRule)
    val isValid = title.isNotBlank() && params.isValid()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Meta (Sprint 21 Dias)") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!canActivateMore) {
                    Text(
                        text = "Aviso TMT: ya tienes $activeCount/$MAX_ACTIVE_GOALS metas activas. Esta meta se creara en Pausa para evitar paralisis de indecision.",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.terracottaCta
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre corto de la meta") },
                    placeholder = { Text("Ej. Dominar Kotlin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = anchorTask,
                    onValueChange = { anchorTask = it },
                    label = { Text("Tarea Ancla (unidad minima)") },
                    placeholder = { Text("Ej. Programar modulos 10 min") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hora disparadora: $triggerTime")
                }
                OutlinedTextField(
                    value = triggerLocation,
                    onValueChange = { triggerLocation = it },
                    label = { Text("Espacio fisico") },
                    placeholder = { Text("Ej. Escritorio principal") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Condicion de bloqueo:",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    blockingPresets.forEach { preset ->
                        FilterChip(
                            selected = blockingRule == preset,
                            onClick = { blockingRule = preset },
                            label = { Text(preset) }
                        )
                    }
                }
                OutlinedTextField(
                    value = blockingRule,
                    onValueChange = { blockingRule = it },
                    label = { Text("Regla de bloqueo de distractor") },
                    placeholder = { Text("Ej. DND automatico + Alerta de salida") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title, params) },
                enabled = isValid
            ) {
                Text(if (canActivateMore) "Crear y Activar" else "Crear en Pausa")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )

    if (showTimePicker) {
        val parts = triggerTime.split(":")
        val pickerState = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 17,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Selecciona la hora diaria") },
            text = { TimePicker(state = pickerState) },
            confirmButton = {
                TextButton(onClick = {
                    triggerTime = String.format("%02d:%02d", pickerState.hour, pickerState.minute)
                    showTimePicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }
}
