/**
 * Codigo anadido por Felipe Denis
 *
 * Contenedor manual de inyeccion de dependencias (ensambla cajas negras aisladas).
 */
package com.utdevs.habitflow.di

import android.content.Context
import com.utdevs.habitflow.data.ai.GeminiMicroGoalGenerator
import com.utdevs.habitflow.data.dopamine.AssetDopamineCatalogSource
import com.utdevs.habitflow.data.feedback.HapticAndToneController
import com.utdevs.habitflow.data.plan.AssetPlanDataSource
import com.utdevs.habitflow.data.reminder.GoalReminderScheduler
import com.utdevs.habitflow.data.repository.FirebaseAuthRepository
import com.utdevs.habitflow.data.repository.FirestoreGoalsRepository
import com.utdevs.habitflow.data.shield.FocusShieldController
import com.utdevs.habitflow.data.theme.ThemePreferences
import com.utdevs.habitflow.domain.dopamine.ComputeDayRewardUseCase
import com.utdevs.habitflow.domain.dopamine.EvaluateAchievementsUseCase
import com.utdevs.habitflow.domain.dopamine.EvaluatePlantHealthUseCase
import com.utdevs.habitflow.domain.dopamine.ValidateActiveGoalsLimitUseCase
import com.utdevs.habitflow.domain.repository.AuthRepository
import com.utdevs.habitflow.domain.repository.DopamineCatalogSource
import com.utdevs.habitflow.domain.repository.GoalsRepository
import com.utdevs.habitflow.domain.repository.MicroGoalGenerator
import com.utdevs.habitflow.domain.repository.PlanDataSource
import com.utdevs.habitflow.domain.usecase.ApplyVerdictResolutionUseCase
import com.utdevs.habitflow.domain.usecase.ComputeDayBlocksUseCase
import com.utdevs.habitflow.domain.usecase.ComputeMetricsAndVerdictUseCase
import com.utdevs.habitflow.domain.usecase.ComputePlantStageUseCase
import com.utdevs.habitflow.domain.usecase.EvaluatePunctualityUseCase
import com.utdevs.habitflow.domain.usecase.ResolveDayStatesUseCase
import com.utdevs.habitflow.service.TimerStateHolder

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val planDataSource: PlanDataSource by lazy {
        AssetPlanDataSource(appContext)
    }

    val dopamineCatalogSource: DopamineCatalogSource by lazy {
        AssetDopamineCatalogSource(appContext)
    }

    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepository()
    }

    val goalsRepository: GoalsRepository by lazy {
        FirestoreGoalsRepository()
    }

    val hapticController: HapticAndToneController by lazy {
        HapticAndToneController(appContext)
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(appContext)
    }

    val reminderScheduler: GoalReminderScheduler by lazy {
        GoalReminderScheduler(appContext)
    }

    val focusShieldController: FocusShieldController by lazy {
        FocusShieldController(appContext)
    }

    val microGoalGenerator: MicroGoalGenerator by lazy {
        GeminiMicroGoalGenerator(appContext)
    }

    val timerStateHolder: TimerStateHolder by lazy {
        TimerStateHolder()
    }

    val computeDayBlocksUseCase by lazy { ComputeDayBlocksUseCase() }
    val evaluatePunctualityUseCase by lazy { EvaluatePunctualityUseCase() }
    val computePlantStageUseCase by lazy { ComputePlantStageUseCase() }
    val resolveDayStatesUseCase by lazy { ResolveDayStatesUseCase() }
    val computeMetricsAndVerdictUseCase by lazy { ComputeMetricsAndVerdictUseCase(planDataSource) }
    val applyVerdictResolutionUseCase by lazy { ApplyVerdictResolutionUseCase() }

    val evaluateAchievementsUseCase by lazy { EvaluateAchievementsUseCase() }
    val evaluatePlantHealthUseCase by lazy { EvaluatePlantHealthUseCase() }
    val validateActiveGoalsLimitUseCase by lazy { ValidateActiveGoalsLimitUseCase() }
    val computeDayRewardUseCase by lazy {
        ComputeDayRewardUseCase(
            computePlantStageUseCase = computePlantStageUseCase,
            evaluateAchievementsUseCase = evaluateAchievementsUseCase
        )
    }
}
