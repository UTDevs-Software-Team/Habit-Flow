/**
 * Codigo anadido por Felipe Denis
 *
 * Grafo de navegacion Multi-Meta reactivo de HabitFlow.
 * Flujo: Auth -> Apodo inicial ("Como quieres que te llamemos?") -> GoalsHub ("Bienvenido / Tus Metas")
 * -> Dashboard Bento / Sesion Diaria / Estadisticas Gauge / Historial Donut / Mantenimiento.
 */
package com.utdevs.habitflow.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.utdevs.habitflow.di.AppContainer
import com.utdevs.habitflow.domain.model.GoalSprint
import com.utdevs.habitflow.domain.model.GoalStatus
import com.utdevs.habitflow.domain.model.VerdictTier
import com.utdevs.habitflow.ui.auth.AuthScreen
import com.utdevs.habitflow.ui.auth.AuthViewModel
import com.utdevs.habitflow.ui.components.dopamine.BotanicalConfettiOverlay
import com.utdevs.habitflow.ui.dashboard.DashboardScreen
import com.utdevs.habitflow.ui.dashboard.DashboardViewModel
import com.utdevs.habitflow.ui.demo.DemoControlsSheet
import com.utdevs.habitflow.ui.history.SessionHistoryScreen
import com.utdevs.habitflow.ui.history.SessionHistoryViewModel
import com.utdevs.habitflow.ui.home.GoalsHubScreen
import com.utdevs.habitflow.ui.home.GoalsHubViewModel
import com.utdevs.habitflow.ui.maintenance.MaintenanceModeScreen
import com.utdevs.habitflow.ui.maintenance.MaintenanceViewModel
import com.utdevs.habitflow.ui.metrics.MetricsVerdictScreen
import com.utdevs.habitflow.ui.metrics.MetricsViewModel
import com.utdevs.habitflow.ui.onboarding.NicknameScreen
import com.utdevs.habitflow.ui.onboarding.NicknameViewModel
import com.utdevs.habitflow.ui.session.DailySessionScreen
import com.utdevs.habitflow.ui.session.DailySessionViewModel
import com.utdevs.habitflow.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun HabitFlowNavGraph(container: AppContainer) {
    val authUser by container.authRepository.currentUserFlow.collectAsStateWithLifecycle(initialValue = null)
    val appearance by container.themePreferences.settings.collectAsStateWithLifecycle()

    if (authUser == null) {
        val authVm: AuthViewModel = viewModel(factory = AuthViewModel.Factory(container.authRepository))
        AuthScreen(viewModel = authVm)
        return
    }

    val user = authUser!!
    var migrationDone by remember(user.uid) { mutableStateOf(false) }
    LaunchedEffect(user.uid) {
        container.goalsRepository.ensureMigrated(user.uid, user.email)
        migrationDone = true
    }

    val profileFlow = remember(user.uid) { container.goalsRepository.observeUserProfile(user.uid) }
    val goalsFlow = remember(user.uid) { container.goalsRepository.observeGoals(user.uid) }
    val profile by profileFlow.collectAsStateWithLifecycle(initialValue = null)
    val goals by goalsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    if (!migrationDone || profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentProfile = profile!!

    if (!currentProfile.nicknameConfigured) {
        val suggested = user.displayName.ifBlank {
            user.email.substringBefore("@").ifBlank { "" }
        }
        val nickVm: NicknameViewModel = viewModel(
            key = "nick_${user.uid}",
            factory = NicknameViewModel.Factory(user.uid, user.email, suggested, container.goalsRepository)
        )
        NicknameScreen(
            viewModel = nickVm,
            onSignOut = { container.authRepository.signOut() }
        )
        return
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var demoTargetGoal by remember { mutableStateOf<GoalSprint?>(null) }
    var showHubConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(showHubConfetti) {
        if (showHubConfetti) {
            kotlinx.coroutines.delay(2700L)
            showHubConfetti = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.GOALS_HUB
        ) {
            composable(Routes.GOALS_HUB) {
                val hubVm: GoalsHubViewModel = viewModel(
                    key = "hub_${user.uid}",
                    factory = GoalsHubViewModel.Factory(
                        uid = user.uid,
                        email = user.email,
                        goalsRepository = container.goalsRepository,
                        dopamineCatalogSource = container.dopamineCatalogSource,
                        computePlantStageUseCase = container.computePlantStageUseCase,
                        evaluatePlantHealthUseCase = container.evaluatePlantHealthUseCase,
                        validateActiveGoalsLimitUseCase = container.validateActiveGoalsLimitUseCase,
                        reminderScheduler = container.reminderScheduler
                    )
                )
                GoalsHubScreen(
                    profile = currentProfile,
                    goals = goals,
                    soundEnabled = appearance.soundEnabled,
                    viewModel = hubVm,
                    onToggleTheme = { currentlyDark ->
                        container.themePreferences.toggleDarkLight(currentlyDark)
                    },
                    onToggleSound = { enabled ->
                        container.themePreferences.setSoundEnabled(enabled)
                    },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenGoal = { goalId ->
                        val g = goals.firstOrNull { it.goalId == goalId }
                        if (g != null && g.sprint.inMaintenanceMode) {
                            navController.navigate(Routes.goalMaintenance(goalId))
                        } else {
                            navController.navigate(Routes.goalDashboard(goalId))
                        }
                    },
                    onSignOut = { container.authRepository.signOut() }
                )
            }

            composable(Routes.SETTINGS) {
                val activeCount = goals.count {
                    it.status == GoalStatus.ACTIVE || it.status == GoalStatus.MAINTENANCE
                }
                SettingsScreen(
                    profile = currentProfile,
                    appearance = appearance,
                    activeGoalsCount = activeCount,
                    onBack = { navController.popBackStack() },
                    onSaveNickname = { newNick ->
                        scope.launch {
                            container.goalsRepository.saveNickname(user.uid, user.email, newNick)
                        }
                    },
                    onSelectThemeMode = { mode ->
                        container.themePreferences.setThemeMode(mode)
                    },
                    onToggleSound = { enabled ->
                        container.themePreferences.setSoundEnabled(enabled)
                    },
                    onToggleHaptics = { enabled ->
                        container.themePreferences.setHapticsEnabled(enabled)
                    },
                    onTestCelebration = {
                        container.hapticController.triggerLevelUpCelebration(
                            soundEnabled = appearance.soundEnabled,
                            hapticsEnabled = appearance.hapticsEnabled
                        )
                        showHubConfetti = true
                    },
                    onRescheduleReminders = {
                        goals.forEach { container.reminderScheduler.scheduleGoalReminders(it) }
                    },
                    onSignOut = { container.authRepository.signOut() }
                )
            }

            composable(
                route = Routes.GOAL_DASHBOARD,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: return@composable
                val goalFlow = remember(user.uid, goalId) {
                    container.goalsRepository.observeGoal(user.uid, goalId)
                }
                val goal by goalFlow.collectAsStateWithLifecycle(initialValue = null)
                val activeGoal = goal ?: return@composable

                val dashVm: DashboardViewModel = viewModel(
                    key = "dash_${user.uid}_$goalId",
                    factory = DashboardViewModel.Factory(
                        uid = user.uid,
                        goalId = goalId,
                        goalsRepository = container.goalsRepository,
                        planDataSource = container.planDataSource,
                        resolveDayStatesUseCase = container.resolveDayStatesUseCase,
                        computePlantStageUseCase = container.computePlantStageUseCase,
                        computeDayBlocksUseCase = container.computeDayBlocksUseCase,
                        computeMetricsAndVerdictUseCase = container.computeMetricsAndVerdictUseCase,
                        evaluatePlantHealthUseCase = container.evaluatePlantHealthUseCase,
                        reminderScheduler = container.reminderScheduler
                    )
                )
                DashboardScreen(
                    goal = activeGoal,
                    profile = currentProfile,
                    viewModel = dashVm,
                    onBackToHub = { navController.popBackStack(Routes.GOALS_HUB, false) },
                    onStartDay = { day -> navController.navigate(Routes.goalSession(goalId, day)) },
                    onOpenMetrics = { navController.navigate(Routes.goalMetrics(goalId)) },
                    onOpenHistory = { navController.navigate(Routes.goalHistory(goalId)) },
                    onOpenDemoSheet = { demoTargetGoal = activeGoal }
                )
            }

            composable(
                route = Routes.GOAL_SESSION,
                arguments = listOf(
                    navArgument("goalId") { type = NavType.StringType },
                    navArgument("day") { type = NavType.IntType }
                )
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: return@composable
                val day = entry.arguments?.getInt("day") ?: 1
                val goalFlow = remember(user.uid, goalId) {
                    container.goalsRepository.observeGoal(user.uid, goalId)
                }
                val goal by goalFlow.collectAsStateWithLifecycle(initialValue = null)
                val activeGoal = goal ?: return@composable

                val sessionVm: DailySessionViewModel = viewModel(
                    key = "session_${user.uid}_${goalId}_$day",
                    factory = DailySessionViewModel.Factory(
                        uid = user.uid,
                        goalId = goalId,
                        dayNumber = day,
                        goalsRepository = container.goalsRepository,
                        planDataSource = container.planDataSource,
                        dopamineCatalogSource = container.dopamineCatalogSource,
                        microGoalGenerator = container.microGoalGenerator,
                        focusShieldController = container.focusShieldController,
                        computeDayBlocksUseCase = container.computeDayBlocksUseCase,
                        evaluatePunctualityUseCase = container.evaluatePunctualityUseCase,
                        computeDayRewardUseCase = container.computeDayRewardUseCase,
                        timerStateHolder = container.timerStateHolder,
                        hapticController = container.hapticController,
                        themePreferences = container.themePreferences
                    )
                )
                DailySessionScreen(
                    goal = activeGoal,
                    profile = currentProfile,
                    viewModel = sessionVm,
                    onBack = { navController.popBackStack() },
                    onDayCompleted = { isDay21 ->
                        if (isDay21) {
                            navController.navigate(Routes.goalMetrics(goalId)) {
                                popUpTo(Routes.goalDashboard(goalId))
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(
                route = Routes.GOAL_METRICS,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: return@composable
                val goalFlow = remember(user.uid, goalId) {
                    container.goalsRepository.observeGoal(user.uid, goalId)
                }
                val goal by goalFlow.collectAsStateWithLifecycle(initialValue = null)
                val activeGoal = goal ?: return@composable

                val metricsVm: MetricsViewModel = viewModel(
                    key = "metrics_${user.uid}_$goalId",
                    factory = MetricsViewModel.Factory(
                        uid = user.uid,
                        goalId = goalId,
                        goalsRepository = container.goalsRepository,
                        planDataSource = container.planDataSource,
                        computeMetricsAndVerdictUseCase = container.computeMetricsAndVerdictUseCase,
                        applyVerdictResolutionUseCase = container.applyVerdictResolutionUseCase
                    )
                )
                MetricsVerdictScreen(
                    goal = activeGoal,
                    viewModel = metricsVm,
                    onBack = { navController.popBackStack() },
                    onOpenDemoSheet = { demoTargetGoal = activeGoal },
                    onResolutionApplied = { tier ->
                        val dest = if (tier == VerdictTier.HIGH_MASTERY) {
                            Routes.goalMaintenance(goalId)
                        } else {
                            Routes.goalDashboard(goalId)
                        }
                        navController.navigate(dest) {
                            popUpTo(Routes.GOALS_HUB)
                        }
                    }
                )
            }

            composable(
                route = Routes.GOAL_HISTORY,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: return@composable
                val goalFlow = remember(user.uid, goalId) {
                    container.goalsRepository.observeGoal(user.uid, goalId)
                }
                val goal by goalFlow.collectAsStateWithLifecycle(initialValue = null)
                val activeGoal = goal ?: return@composable

                val histVm = remember(goalId) {
                    SessionHistoryViewModel(container.computeMetricsAndVerdictUseCase)
                }
                SessionHistoryScreen(
                    goal = activeGoal,
                    viewModel = histVm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.GOAL_MAINTENANCE,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: return@composable
                val goalFlow = remember(user.uid, goalId) {
                    container.goalsRepository.observeGoal(user.uid, goalId)
                }
                val goal by goalFlow.collectAsStateWithLifecycle(initialValue = null)
                val activeGoal = goal ?: return@composable

                val maintVm: MaintenanceViewModel = viewModel(
                    key = "maint_${user.uid}_$goalId",
                    factory = MaintenanceViewModel.Factory(
                        uid = user.uid,
                        goalId = goalId,
                        goalsRepository = container.goalsRepository,
                        timerStateHolder = container.timerStateHolder,
                        hapticController = container.hapticController,
                        themePreferences = container.themePreferences
                    )
                )
                MaintenanceModeScreen(
                    goal = activeGoal,
                    viewModel = maintVm,
                    onBackToHub = { navController.popBackStack(Routes.GOALS_HUB, false) },
                    onOpenMetrics = { navController.navigate(Routes.goalMetrics(goalId)) }
                )
            }
        }

        BotanicalConfettiOverlay(visible = showHubConfetti)

        demoTargetGoal?.let { target ->
            DemoControlsSheet(
                uid = user.uid,
                profile = currentProfile,
                goal = target,
                goalsRepository = container.goalsRepository,
                planDataSource = container.planDataSource,
                dopamineCatalogSource = container.dopamineCatalogSource,
                computeDayRewardUseCase = container.computeDayRewardUseCase,
                hapticController = container.hapticController,
                soundEnabled = appearance.soundEnabled,
                onDismiss = { demoTargetGoal = null },
                onDemoLevelUpTriggered = { showHubConfetti = true },
                onSimulatedDay21 = { navController.navigate(Routes.goalMetrics(target.goalId)) },
                onGoalDeleted = { navController.popBackStack(Routes.GOALS_HUB, false) }
            )
        }
    }
}
