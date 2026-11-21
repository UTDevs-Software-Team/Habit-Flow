/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para ComputeDayBlocksUseCase (factor 0.5x con redondeo hacia arriba y Dia 20).
 */
package com.utdevs.habitflow.domain

import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.plan.HabitFlow21DayPlan
import com.utdevs.habitflow.domain.usecase.ComputeDayBlocksUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ComputeDayBlocksUseCaseTest {

    private val useCase = ComputeDayBlocksUseCase()

    @Test
    fun `recalibration factor 0_5 rounds up odd durations`() {
        val day1 = HabitFlow21DayPlan.getDaySpec(1)
        val blocks = useCase(day1, durationFactor = 0.5)
        assertEquals(1, blocks.size)
        assertEquals(3, blocks[0].durationMinutes)
        assertEquals(BlockType.FOCUS, blocks[0].type)
    }

    @Test
    fun `day 20 format selector resolves chosen option`() {
        val day20 = HabitFlow21DayPlan.getDaySpec(20)
        val blocks1x50 = useCase(day20, durationFactor = 1.0, selectedFormatId = "format_1x50")
        assertEquals(2, blocks1x50.size)
        assertEquals(50, blocks1x50[0].durationMinutes)

        val blocks2x25 = useCase(day20, durationFactor = 1.0, selectedFormatId = "format_2x25")
        assertEquals(3, blocks2x25.size)
        assertEquals(25, blocks2x25[0].durationMinutes)
        assertEquals(5, blocks2x25[1].durationMinutes)
        assertEquals(25, blocks2x25[2].durationMinutes)
    }
}
