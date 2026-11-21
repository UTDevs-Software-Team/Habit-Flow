/**
 * Codigo anadido por Felipe Denis
 *
 * Caso de uso Black Box: Mapea el conteo de dias completados a la etapa visual de la Planta Virtual.
 */
package com.utdevs.habitflow.domain.usecase

import com.utdevs.habitflow.domain.model.PlantStage

class ComputePlantStageUseCase {

    operator fun invoke(completedDaysCount: Int): PlantStage = when {
        completedDaysCount <= 3 -> PlantStage.SEED
        completedDaysCount <= 7 -> PlantStage.SPROUT
        completedDaysCount <= 14 -> PlantStage.STEM_LEAVES
        completedDaysCount <= 20 -> PlantStage.BLOOMING
        else -> PlantStage.FULL_TREE
    }
}
