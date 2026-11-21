/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Calcula los bloques de enfoque y descanso efectivos de un dia
 * aplicando la seleccion de formato (Dia 20) y el factor de recalibracion (durationFactor).
 */
package com.utdevs.habitflow.domain.usecase

import com.utdevs.habitflow.domain.model.DayPlanSpec
import com.utdevs.habitflow.domain.model.SessionBlock
import com.utdevs.habitflow.domain.model.SpecialEvent
import kotlin.math.ceil

class ComputeDayBlocksUseCase {

    /**
     * @param daySpec Especificacion del dia desde PlanDataSource.
     * @param durationFactor 1.0 normal o 0.5 tras recalibracion.
     * @param selectedFormatId Identificador elegido cuando el dia posee FormatSelector.
     */
    operator fun invoke(
        daySpec: DayPlanSpec,
        durationFactor: Double,
        selectedFormatId: String? = null
    ): List<SessionBlock> {
        val rawBlocks = when (val event = daySpec.specialEvent) {
            is SpecialEvent.FormatSelector -> {
                val chosen = event.options.firstOrNull { it.id == selectedFormatId }
                    ?: event.options.first()
                chosen.blocks
            }
            else -> daySpec.baseBlocks
        }

        return rawBlocks.map { block ->
            val scaled = ceil(block.durationMinutes * durationFactor).toInt().coerceAtLeast(1)
            block.copy(durationMinutes = scaled)
        }
    }
}
