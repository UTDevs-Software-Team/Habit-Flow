/**
 * Codigo anadido por Felipe Denis
 *
 * Etapas evolutivas de la Planta Virtual segun dias completados.
 */
package com.utdevs.habitflow.domain.model

enum class PlantStage(
    val stageNumber: Int,
    val title: String,
    val rangeLabel: String
) {
    SEED(1, "Semilla germinando", "Dias 1-3"),
    SPROUT(2, "Brote verde con primeras hojas", "Dias 4-7"),
    STEM_LEAVES(3, "Tallo firme con varias hojas", "Dias 8-14"),
    BLOOMING(4, "Planta en plena floracion", "Dias 15-20"),
    FULL_TREE(5, "Arbol completo con fruto", "Dia 21")
}
