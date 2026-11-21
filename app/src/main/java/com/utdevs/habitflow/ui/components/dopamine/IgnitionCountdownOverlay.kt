/**
 * Codigo anadido por Felipe Denis
 *
 * Overlay de ignicion 3-2-1 de baja friccion para romper la paralisis inicial antes de arrancar.
 */
package com.utdevs.habitflow.ui.components.dopamine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utdevs.habitflow.ui.theme.LocalForestColors
import kotlinx.coroutines.delay

@Composable
fun IgnitionCountdownOverlay(
    taskTitle: String,
    onFinished: () -> Unit
) {
    val colors = LocalForestColors.current
    var count by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(700L)
            count -= 1
        }
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.heroStart.copy(alpha = 0.94f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Ignicion de Enfoque",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textOnAccent.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (count > 0) count.toString() else "AHORA",
                fontSize = 68.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textOnAccent
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = taskTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textOnAccent.copy(alpha = 0.9f)
            )
        }
    }
}
