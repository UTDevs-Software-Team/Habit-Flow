/**
 * Codigo anadido por Felipe Denis
 *
 * Modelos inmutables del plan de 21 dias.
 * Caja negra de dominio: sin dependencias de Android ni Firebase.
 */
package com.utdevs.habitflow.domain.model

/** Tipo de bloque dentro de una sesion de trabajo progresivo. */
enum class BlockType {
    FOCUS,
    BREAK
}

/** Unidad atomica de tiempo para el temporizador multi-bloque. */
data class SessionBlock(
    val type: BlockType,
    val durationMinutes: Int
)

/** Opcion seleccionable para dias con formato personalizable (ej. Dia 20). */
data class FormatOption(
    val id: String,
    val label: String,
    val blocks: List<SessionBlock>
)

/** Eventos condicionales requeridos en dias especificos del sprint. */
sealed interface SpecialEvent {
    data class TextInput(val placeholder: String) : SpecialEvent
    data class AuditText(val label: String) : SpecialEvent
    data class Rating1To5(val label: String) : SpecialEvent
    data class FormatSelector(val options: List<FormatOption>) : SpecialEvent
}

/** Especificacion canonica de un dia individual dentro del sprint de 21 dias. */
data class DayPlanSpec(
    val day: Int,
    val phaseId: Int,
    val focusMinutes: Int,
    val rule: String,
    val successCriteria: String,
    val baseBlocks: List<SessionBlock>,
    val constraints: List<String>,
    val punctualityWindowMinutes: Int = 10,
    val specialEvent: SpecialEvent? = null
)

/** Especificacion de una fase semanal del metodo HabitFlow. */
data class PhaseSpec(
    val phaseId: Int,
    val name: String,
    val daysRangeLabel: String,
    val targetFocusMinutes: Int,
    val days: List<DayPlanSpec>
)
