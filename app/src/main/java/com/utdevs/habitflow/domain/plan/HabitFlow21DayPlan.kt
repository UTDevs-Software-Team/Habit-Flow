/**
 * Codigo anadido por Felipe Denis
 *
 * Implementacion en memoria del Plan de 21 Dias (usada en tests unitarios JVM y como respaldo).
 * En runtime Android la fuente principal es AssetPlanDataSource leyendo assets/plans/anti_procrastination_21d.json.
 */
package com.utdevs.habitflow.domain.plan

import com.utdevs.habitflow.domain.model.BlockType
import com.utdevs.habitflow.domain.model.DayPlanSpec
import com.utdevs.habitflow.domain.model.FormatOption
import com.utdevs.habitflow.domain.model.PhaseSpec
import com.utdevs.habitflow.domain.model.SessionBlock
import com.utdevs.habitflow.domain.model.SpecialEvent
import com.utdevs.habitflow.domain.repository.PlanDataSource

object HabitFlow21DayPlan : PlanDataSource {

    const val TOTAL_TARGET_MINUTES = 785

    private val phasesList: List<PhaseSpec> = listOf(
        PhaseSpec(
            phaseId = 1,
            name = "Fase 1: Romper la Inercia",
            daysRangeLabel = "Dias 1 al 7",
            targetFocusMinutes = 115,
            days = listOf(
                DayPlanSpec(
                    day = 1, phaseId = 1, focusMinutes = 5,
                    rule = "Regla de los 5 minutos: Trabajar solo 5 min. Permiso explicito de parar al sonar la alarma.",
                    successCriteria = "Completar 5 min sin abrir otra app o pestana.",
                    baseBlocks = listOf(SessionBlock(BlockType.FOCUS, 5)),
                    constraints = listOf("No tab switching")
                ),
                DayPlanSpec(
                    day = 2, phaseId = 1, focusMinutes = 10,
                    rule = "Inicio puntual: Empezar dentro de los 10 min posteriores a la hora fijada.",
                    successCriteria = "Iniciar en ventana de 10 min y cumplir 10 min.",
                    baseBlocks = listOf(SessionBlock(BlockType.FOCUS, 10)),
                    constraints = listOf("Punctuality window <= 10m")
                ),
                DayPlanSpec(
                    day = 3, phaseId = 1, focusMinutes = 10,
                    rule = "Micro-objetivo escrito: Escribir en 1 linea que se hara antes de iniciar.",
                    successCriteria = "Guardar linea escrita y cumplir 10 min.",
                    baseBlocks = listOf(SessionBlock(BlockType.FOCUS, 10)),
                    constraints = emptyList(),
                    specialEvent = SpecialEvent.TextInput("Que haras en estos 10 minutos?")
                ),
                DayPlanSpec(
                    day = 4, phaseId = 1, focusMinutes = 15,
                    rule = "Cero pausas internas: Prohibido levantarse o revisar mensajes durante el bloque.",
                    successCriteria = "15 min continuos sin interrupciones.",
                    baseBlocks = listOf(SessionBlock(BlockType.FOCUS, 15)),
                    constraints = listOf("Zero pauses")
                ),
                DayPlanSpec(
                    day = 5, phaseId = 1, focusMinutes = 15,
                    rule = "Regla de los 2 minutos: Preparar entorno en menos de 2 min sin pensar.",
                    successCriteria = "Cumplir preparacion inmediata + 15 min de enfoque.",
                    baseBlocks = listOf(SessionBlock(BlockType.FOCUS, 15)),
                    constraints = emptyList()
                ),
                DayPlanSpec(
                    day = 6, phaseId = 1, focusMinutes = 30,
                    rule = "Primer descanso controlado: Prohibido usar redes o videos cortos en la pausa.",
                    successCriteria = "Completar ambos bloques de 15 min.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 15),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 15)
                    ),
                    constraints = listOf("No social media during break")
                ),
                DayPlanSpec(
                    day = 7, phaseId = 1, focusMinutes = 30,
                    rule = "Auditoria Semanal 1: Registrar el principal distractor de la semana al terminar.",
                    successCriteria = "Completar bloques + responder auditoria escrita.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 15),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 15)
                    ),
                    constraints = emptyList(),
                    specialEvent = SpecialEvent.AuditText("Cual fue tu principal distractor esta semana?")
                )
            )
        ),
        PhaseSpec(
            phaseId = 2,
            name = "Fase 2: Construir Resistencia",
            daysRangeLabel = "Dias 8 al 14",
            targetFocusMinutes = 295,
            days = listOf(
                DayPlanSpec(
                    day = 8, phaseId = 2, focusMinutes = 25,
                    rule = "Estacionamiento de impulsos: Anotar cualquier pendiente ajeno en papel y continuar.",
                    successCriteria = "25 min sin cambiar de actividad.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5)
                    ),
                    constraints = listOf("Externalize thoughts")
                ),
                DayPlanSpec(
                    day = 9, phaseId = 2, focusMinutes = 25,
                    rule = "Tolerancia cero al retraso: Margen maximo de 5 min para iniciar segun la hora fijada.",
                    successCriteria = "Iniciar en ventana de 5 min + cumplir 25 min.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5)
                    ),
                    constraints = listOf("Punctuality window <= 5m"),
                    punctualityWindowMinutes = 5
                ),
                DayPlanSpec(
                    day = 10, phaseId = 2, focusMinutes = 40,
                    rule = "Segundo impulso: Continuar 15 min extra tras el descanso para entrenar resistencia.",
                    successCriteria = "Completar los 40 min efectivos.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 15)
                    ),
                    constraints = emptyList()
                ),
                DayPlanSpec(
                    day = 11, phaseId = 2, focusMinutes = 50,
                    rule = "Bloqueo total de notificaciones: Silenciar todo dispositivo durante la sesion.",
                    successCriteria = "Completar 2 ciclos de 25 min sin alertas.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 25)
                    ),
                    constraints = listOf("Strict DND")
                ),
                DayPlanSpec(
                    day = 12, phaseId = 2, focusMinutes = 50,
                    rule = "Sin multitarea: Solo 1 ventana o herramienta activa en pantalla durante el bloque.",
                    successCriteria = "Completar 50 min en una sola ventana.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 25)
                    ),
                    constraints = listOf("Single window active")
                ),
                DayPlanSpec(
                    day = 13, phaseId = 2, focusMinutes = 55,
                    rule = "Extension de umbral: Primer bloque extendido a 35 min continuos antes del descanso.",
                    successCriteria = "Superar el bloque largo de 35 min.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 35),
                        SessionBlock(BlockType.BREAK, 7),
                        SessionBlock(BlockType.FOCUS, 20)
                    ),
                    constraints = emptyList()
                ),
                DayPlanSpec(
                    day = 14, phaseId = 2, focusMinutes = 50,
                    rule = "Auditoria Semanal 2: Calificar del 1 al 5 la dificultad de inicio vs el Dia 1.",
                    successCriteria = "Completar 50 min + registrar calificacion 1-5.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 25),
                        SessionBlock(BlockType.BREAK, 5),
                        SessionBlock(BlockType.FOCUS, 25)
                    ),
                    constraints = emptyList(),
                    specialEvent = SpecialEvent.Rating1To5("Dificultad de inicio vs Dia 1 (1: Muy facil, 5: Muy dificil)")
                )
            )
        ),
        PhaseSpec(
            phaseId = 3,
            name = "Fase 3: Consolidacion y Autonomia",
            daysRangeLabel = "Dias 15 al 21",
            targetFocusMinutes = 375,
            days = listOf(
                DayPlanSpec(
                    day = 15, phaseId = 3, focusMinutes = 40,
                    rule = "Inmersion profunda: Un solo bloque largo de 40 min sin pausas intermedias.",
                    successCriteria = "Completar 40 min ininterrumpidos.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 40),
                        SessionBlock(BlockType.BREAK, 10)
                    ),
                    constraints = listOf("Continuous deep focus")
                ),
                DayPlanSpec(
                    day = 16, phaseId = 3, focusMinutes = 40,
                    rule = "Lo mas dificil primero: Dedicar los primeros 15 min exclusivamente a la parte mas compleja.",
                    successCriteria = "Empezar por el sub-paso mas dificil.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 40),
                        SessionBlock(BlockType.BREAK, 10)
                    ),
                    constraints = listOf("Hardest task first 15m")
                ),
                DayPlanSpec(
                    day = 17, phaseId = 3, focusMinutes = 70,
                    rule = "Pico de volumen: Dia de mayor exigencia del plan (2 bloques de 35 min).",
                    successCriteria = "Completar los 70 min efectivos.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 35),
                        SessionBlock(BlockType.BREAK, 7),
                        SessionBlock(BlockType.FOCUS, 35)
                    ),
                    constraints = listOf("Peak volume")
                ),
                DayPlanSpec(
                    day = 18, phaseId = 3, focusMinutes = 45,
                    rule = "Recuperacion activa: Un bloque solido de 45 min aplicando lo mas dificil primero.",
                    successCriteria = "Completar 45 min sin interrupciones.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 45),
                        SessionBlock(BlockType.BREAK, 10)
                    ),
                    constraints = emptyList()
                ),
                DayPlanSpec(
                    day = 19, phaseId = 3, focusMinutes = 80,
                    rule = "Simulacion de jornada real: Dos sesiones profundas de 40 min con descanso estructurado.",
                    successCriteria = "Completar ambos bloques de 40 min.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 40),
                        SessionBlock(BlockType.BREAK, 10),
                        SessionBlock(BlockType.FOCUS, 40)
                    ),
                    constraints = emptyList()
                ),
                DayPlanSpec(
                    day = 20, phaseId = 3, focusMinutes = 50,
                    rule = "Autonomia guiada: Elegir formato antes de iniciar (1x50/10 o 2x25/5).",
                    successCriteria = "Seleccionar formato y completarlo.",
                    baseBlocks = emptyList(),
                    constraints = listOf("User custom choice"),
                    specialEvent = SpecialEvent.FormatSelector(
                        options = listOf(
                            FormatOption(
                                id = "format_1x50",
                                label = "1 bloque de 50m + 10m descanso",
                                blocks = listOf(
                                    SessionBlock(BlockType.FOCUS, 50),
                                    SessionBlock(BlockType.BREAK, 10)
                                )
                            ),
                            FormatOption(
                                id = "format_2x25",
                                label = "2 bloques de 25m + 5m descanso",
                                blocks = listOf(
                                    SessionBlock(BlockType.FOCUS, 25),
                                    SessionBlock(BlockType.BREAK, 5),
                                    SessionBlock(BlockType.FOCUS, 25)
                                )
                            )
                        )
                    )
                ),
                DayPlanSpec(
                    day = 21, phaseId = 3, focusMinutes = 50,
                    rule = "Prueba Final y Cierre: Ejecutar el bloque estandar (50/10) y desbloquear veredicto.",
                    successCriteria = "Completar 50 min + generar reporte final.",
                    baseBlocks = listOf(
                        SessionBlock(BlockType.FOCUS, 50),
                        SessionBlock(BlockType.BREAK, 10)
                    ),
                    constraints = listOf("Final evaluation trigger")
                )
            )
        )
    )

    override fun getPhases(): List<PhaseSpec> = phasesList

    override fun getDaySpec(day: Int): DayPlanSpec =
        phasesList.flatMap { it.days }.first { it.day == day }

    override fun getTotalTargetMinutes(): Int = TOTAL_TARGET_MINUTES
}
