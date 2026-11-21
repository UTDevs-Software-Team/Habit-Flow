/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para verificar la integridad matematica y estructural del Plan de 21 Dias.
 */
package com.utdevs.habitflow.domain

import com.utdevs.habitflow.domain.plan.HabitFlow21DayPlan
import org.junit.Assert.assertEquals
import org.junit.Test

class HabitFlow21DayPlanTest {

    @Test
    fun `sum of all 21 days focus minutes equals exactly 785`() {
        val total = HabitFlow21DayPlan.getPhases()
            .flatMap { it.days }
            .sumOf { it.focusMinutes }
        assertEquals(785, total)
        assertEquals(785, HabitFlow21DayPlan.getTotalTargetMinutes())
    }

    @Test
    fun `each phase sums its exact target minutes`() {
        val phases = HabitFlow21DayPlan.getPhases()
        assertEquals(3, phases.size)
        assertEquals(115, phases[0].days.sumOf { it.focusMinutes })
        assertEquals(295, phases[1].days.sumOf { it.focusMinutes })
        assertEquals(375, phases[2].days.sumOf { it.focusMinutes })
    }

    @Test
    fun `day 1 is 5 minutes and day 9 has 5 min punctuality window`() {
        assertEquals(5, HabitFlow21DayPlan.getDaySpec(1).focusMinutes)
        assertEquals(5, HabitFlow21DayPlan.getDaySpec(9).punctualityWindowMinutes)
        assertEquals(10, HabitFlow21DayPlan.getDaySpec(2).punctualityWindowMinutes)
    }
}
