/**
 * Codigo anadido por Felipe Denis
 *
 * Modelos de estado del usuario, parametros del Dia 0, checks diarios y sesiones de mantenimiento.
 * Caja negra pura sin dependencias externas.
 */
package com.utdevs.habitflow.domain.model

/** Estado operativo de cada tarjeta en la cuadricula de 21 dias. */
enum class DayStatus {
    COMPLETED,
    ACTIVE,
    MISSED,
    LOCKED
}

/** Bucle diario estandar de 3 verificaciones obligatorias. */
data class DailyCheckState(
    val checkPrep: Boolean = false,
    val checkExecution: Boolean = false,
    val checkClosure: Boolean = false
) {
    fun allChecked(): Boolean = checkPrep && checkExecution && checkClosure
}

/** Registro inmutable de ejecucion de un dia del sprint. */
data class DailyLogEntry(
    val day: Int,
    val completed: Boolean,
    val missed: Boolean = false,
    val startTimeIso: String? = null,
    val completedAtIso: String? = null,
    val punctual: Boolean = false,
    val effectiveFocusMinutes: Int = 0,
    val checks: DailyCheckState = DailyCheckState(),
    val closureTask: String = "",
    val specialInput: String? = null,
    val selectedFormatId: String? = null
)

/** Parametros fijos configurados en el Dia 0 y editables desde el Dashboard. */
data class DayZeroParams(
    val anchorTask: String,
    val triggerTime: String,
    val triggerLocation: String,
    val blockingRule: String
) {
    fun isValid(): Boolean =
        anchorTask.isNotBlank() &&
            triggerTime.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$")) &&
            triggerLocation.isNotBlank() &&
            blockingRule.isNotBlank()
}

/** Estado global del sprint del usuario sincronizado con Cloud Firestore. */
data class UserSprintState(
    val uid: String = "",
    val email: String = "",
    val setupCompleted: Boolean = false,
    val anchorTask: String = "",
    val secondaryAnchorTask: String? = null,
    val triggerTime: String = "",
    val triggerLocation: String = "",
    val blockingRule: String = "",
    val sprintStartDateIso: String = "",
    val currentDay: Int = 1,
    val completedDays: Set<Int> = emptySet(),
    val missedDays: Set<Int> = emptySet(),
    val nextMicroTask: String = "",
    val durationFactor: Double = 1.0,
    val inMaintenanceMode: Boolean = false,
    val maintenanceUnlocked: Boolean = false,
    val activeRepeatRangeStart: Int? = null,
    val cycleNumber: Int = 1,
    val dailyLogs: Map<Int, DailyLogEntry> = emptyMap()
)

/** Sesion libre ejecutada en Modo Mantenimiento (45/10 o 50/10). */
data class MaintenanceSessionRecord(
    val id: String = "",
    val anchorTaskUsed: String,
    val formatLabel: String,
    val focusMinutes: Int,
    val completedAtIso: String
)
