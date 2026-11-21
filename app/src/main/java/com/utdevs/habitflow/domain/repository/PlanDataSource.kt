/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box para cargar el plan de 21 dias desde una fuente externa (assets JSON o memoria).
 */
package com.utdevs.habitflow.domain.repository

import com.utdevs.habitflow.domain.model.DayPlanSpec
import com.utdevs.habitflow.domain.model.PhaseSpec

interface PlanDataSource {
    fun getPhases(): List<PhaseSpec>
    fun getDaySpec(day: Int): DayPlanSpec
    fun getTotalTargetMinutes(): Int
}
