/**
 * Codigo anadido por Felipe Denis
 *
 * ModalBottomSheet de Modo Demo y Pruebas de Dopamina para una Meta (goalId).
 */
package com.utdevs.habitflow.ui.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utdevs.habitflow.data.feedback.HapticAndToneController
import com.utdevs.habitflow.domain.dopamine.ComputeDayRewardUseCase
import com.utdevs.habitflow.domain.model.DailyCheckState
import com.utdevs.habitflow.domain.model.DailyLogEntry
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.UserProfile
import com.utdevs.habitflow.domain.repository.DopamineCatalogSource
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.domain.repository.PlanDataSource
import com.utdevs.habitflow.ui.theme.LocalForestColors
import java.time.Instant
import java.time.LocalDate
import kotlin.math.ceil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoControlsSheet(
    uid: String,
    profile: UserProfile,
    goal: GoalSprint,
    goalsRepository: GoalsRepository,
    planDataSource: PlanDataSource,
    dopamineCatalogSource: DopamineCatalogSource,
    computeDayRewardUseCase: ComputeDayRewardUseCase,
    hapticController: HapticAndToneController,
    soundEnabled: Boolean,
    onDismiss: () -> Unit,
    onDemoLevelUpTriggered: () -> Unit,
    onSimulatedDay21: () -> Unit,
    onGoalDeleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val colors = LocalForestColors.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Modo Demo Academico (${goal.title})",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )

            OutlinedButton(
                onClick = {
                    scope.launch {
                        val day = goal.sprint.currentDay.coerceIn(1, 21)
                        val spec = planDataSource.getDaySpec(day)
                        val mins = ceil(spec.focusMinutes * goal.sprint.durationFactor).toInt().coerceAtLeast(1)
                        val log = DailyLogEntry(
                            day = day,
                            completed = true,
                            startTimeIso = Instant.now().toString(),
                            completedAtIso = Instant.now().toString(),
                            punctual = true,
                            effectiveFocusMinutes = mins,
                            checks = DailyCheckState(true, true, true),
                            closureTask = "Micro-tarea demo del Dia $day"
                        )
                        val catalog = dopamineCatalogSource.getCatalog()
                        val outcome = computeDayRewardUseCase(
                            catalog = catalog,
                            profile = profile,
                            goalBeforeDay = goal,
                            completedLog = log,
                            newGoalStreak = goal.goalStreak + 1
                        )
                        val shields = if (outcome.shieldEarned) {
                            (profile.streakShields + 1).coerceAtMost(catalog.xpRules.maxShields)
                        } else profile.streakShields

                        goalsRepository.saveCompletedDayWithReward(
                            uid = uid,
                            goalId = goal.goalId,
                            log = log,
                            nextDay = (day + 1).coerceAtMost(21),
                            reward = outcome,
                            newGlobalStreak = profile.globalStreak + 1,
                            newBestStreak = maxOf(profile.bestStreak, profile.globalStreak + 1),
                            newGoalStreak = goal.goalStreak + 1,
                            newShields = shields
                        )
                        if (outcome.leveledUp) {
                            hapticController.triggerLevelUpCelebration(soundEnabled)
                            onDemoLevelUpTriggered()
                        } else {
                            hapticController.triggerPointsGainHaptic()
                        }
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Completar Dia Actual (${goal.sprint.currentDay}) + XP") }

            OutlinedButton(
                onClick = {
                    hapticController.triggerLevelUpCelebration(soundEnabled)
                    onDemoLevelUpTriggered()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Probar Celebracion: Vibracion Larga + Confeti") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        val day = goal.sprint.currentDay.coerceIn(1, 21)
                        goalsRepository.markDaysMissed(
                            uid = uid,
                            goalId = goal.goalId,
                            missedDays = setOf(day),
                            newCurrentDay = (day + 1).coerceAtMost(21),
                            shieldConsumed = false,
                            remainingShields = profile.streakShields
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Marcar Dia Actual como Omitido (Planta Seca)") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        goalsRepository.seedDemoScenario(uid, goal.goalId, buildDemoGoal(goal, planDataSource, 19))
                        onDismiss()
                        onSimulatedDay21()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simular Dia 21: Dominio Alto (>= 80%, 19/21)") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        goalsRepository.seedDemoScenario(uid, goal.goalId, buildDemoGoal(goal, planDataSource, 13))
                        onDismiss()
                        onSimulatedDay21()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simular Dia 21: Estabilidad Moderada (13/21)") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        goalsRepository.seedDemoScenario(uid, goal.goalId, buildDemoGoal(goal, planDataSource, 8))
                        onDismiss()
                        onSimulatedDay21()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simular Dia 21: Recalibracion (< 50%, 8/21)") }

            Button(
                onClick = {
                    scope.launch {
                        goalsRepository.resetGoalProgress(uid, goal.goalId)
                        onDismiss()
                        onGoalDeleted()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.terracottaCta),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Eliminar esta Meta") }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun buildDemoGoal(
    base: GoalSprint,
    planDataSource: PlanDataSource,
    completedCount: Int
): GoalSprint {
    val completed = (1..completedCount).toSet()
    val missed = ((completedCount + 1)..21).toSet()
    val logs = (1..21).associateWith { day ->
        if (day in completed) {
            val mins = ceil(planDataSource.getDaySpec(day).focusMinutes * base.sprint.durationFactor)
                .toInt().coerceAtLeast(1)
            DailyLogEntry(
                day = day,
                completed = true,
                punctual = day % 4 != 0,
                effectiveFocusMinutes = mins,
                checks = DailyCheckState(true, true, true),
                closureTask = "Continuar bloque del Dia $day"
            )
        } else {
            DailyLogEntry(day = day, completed = false, missed = true)
        }
    }

    return base.copy(
        status = GoalStatus.ACTIVE,
        sprint = base.sprint.copy(
            setupCompleted = true,
            sprintStartDateIso = LocalDate.now().minusDays(20).toString(),
            currentDay = 21,
            completedDays = completed,
            missedDays = missed,
            nextMicroTask = "Revision de cierre del Dia 21",
            inMaintenanceMode = false,
            dailyLogs = logs
        )
    )
}
