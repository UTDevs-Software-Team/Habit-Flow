/**
 * Codigo anadido por Felipe Denis
 *
 * Pruebas unitarias JVM para EvaluatePunctualityUseCase.
 */
package com.utdevs.habitflow.domain

import com.utdevs.habitflow.domain.usecase.EvaluatePunctualityUseCase
import java.time.LocalTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluatePunctualityUseCaseTest {

    private val useCase = EvaluatePunctualityUseCase()

    @Test
    fun `standard 10 minute window allows up to +10 minutes`() {
        assertTrue(useCase("17:00", LocalTime.of(17, 10), 10))
        assertFalse(useCase("17:00", LocalTime.of(17, 11), 10))
    }

    @Test
    fun `day 9 strict 5 minute window rejects +6 minutes`() {
        assertTrue(useCase("17:00", LocalTime.of(17, 5), 5))
        assertFalse(useCase("17:00", LocalTime.of(17, 6), 5))
    }
}
