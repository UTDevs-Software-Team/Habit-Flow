/**
 * ============================================================================
 * MODULO BLACK BOX: DOMINIO DEL PROGRAMA DE 21 DIAS Y MAGNITUDES
 * ============================================================================
 * Contrato:
 * - Lee `window.HABITFLOW_BASE_PARAMS`.
 * - Expone `HABIT_MAGNITUDES`, `PROGRAM_STAGES`, `getDayConfig(dayNum, mag)`
 *   y `getPlantPhase(completedDaysCount)` como funciones puras de dominio.
 */
const BASE_PARAMS = HABITFLOW_BASE_PARAMS;
const HABIT_MAGNITUDES = BASE_PARAMS.MAGNITUDES || {
  chico: {
    id: 'chico',
    name: 'Chico (Baja complejidad / Físico rápido)',
    shortName: 'Chico',
    tag: 'Baja Complejidad',
    color: 'emerald',
    badgeBg: 'bg-emerald-100 text-emerald-800 border-emerald-300',
    icon: '🧹',
    examples: 'Ordenar el cuarto, tender la cama, hidratación, recoger el escritorio, 5 estiramientos.',
    description: 'Micro-tareas físicas o de baja carga cognitiva. No requieren 10 o 25 minutos. Lo difícil es la inercia de levantarse. Los lapsos van de 2 a 5 min en la Etapa 1 y se consolidan en rutinas ágiles de 5 a 10 min.',
    timeRangeLabel: '2 a 10 minutos por día',
    totalPlannedMinutes: 87
  },
  mediano: {
    id: 'mediano',
    name: 'Mediano (Complejidad moderada / Rutina diaria)',
    shortName: 'Mediano',
    tag: 'Moderado',
    color: 'indigo',
    badgeBg: 'bg-indigo-100 text-indigo-800 border-indigo-300',
    icon: '📖',
    examples: 'Lectura de libros, repasar apuntes del día, practicar idioma (Duolingo), diario reflexivo, ejercicio ligero.',
    description: 'Hábitos diarios balanceados entre concentración mental y constancia de rutina. Lapsos estructurados de 5 a 10 min al inicio, alcanzando de 10 a 20 min en la etapa de autonomía.',
    timeRangeLabel: '5 a 23 minutos por día',
    totalPlannedMinutes: 182
  },
  grande: {
    id: 'grande',
    name: 'Grande (Alta demanda cognitiva / Estudio profundo)',
    shortName: 'Grande',
    tag: 'Alta Complejidad',
    color: 'purple',
    badgeBg: 'bg-purple-100 text-purple-800 border-purple-300',
    icon: '🧠',
    examples: 'Estudiar materias complejas (cálculo, física, medicina), redactar tesis/ensayos, programar software, resolver problemas analíticos.',
    description: 'Proyectos académicos y profesionales de alta exigencia mental. Arranca con 5-8 min de fricción cero para vencer la intimidación, escalando a bloques profundos de 15 a 30 min (técnica Pomodoro guiada).',
    timeRangeLabel: '5 a 30 minutos por día',
    totalPlannedMinutes: 257
  }
};

// Stage Names
const PROGRAM_STAGES = BASE_PARAMS.STAGES || [
  { id: 1, name: 'Etapa 1: Fundamentos y Micro-pasos', days: 'Días 1 a 7', color: 'indigo' },
  { id: 2, name: 'Etapa 2: Consistencia y Ritmo', days: 'Días 8 a 14', color: 'blue' },
  { id: 3, name: 'Etapa 3: Consolidación y Autonomía', days: 'Días 15 a 21', color: 'emerald' },
];

// [SECCION 5] Catalogo CSV embebido (auto-contenido para funcionar tambien en file://)


const getDayConfig = (dayNum, magnitude = 'mediano') => {
  // 1. DURATION MAPPINGS FOR THE 3 CALENDARS
  const durations = BASE_PARAMS.DURATIONS || {
    chico:   [2, 3, 3, 3, 3, 4, 2,  5, 3,  5,  5,  5,  8, 3,  5,  4, 3, 10,  5, 3, 3],
    mediano: [5, 5, 5, 5, 5, 7, 3,  8, 5, 10,  8, 10, 18, 5, 10,  5, 5, 23, 10, 5, 5],
    grande:  [5, 7, 8, 6, 8, 10, 5, 12, 8, 15, 12, 15, 25, 7, 15, 10, 8, 30, 15, 8, 8]
  };

  // 2. MULTI-PHASE GUIDED SEQUENCES FOR DAYS 13 AND 18
  const sequences = BASE_PARAMS.SEQUENCES || {
    chico: {
      13: [
        { name: 'Acción Rápida', minutes: 4, type: 'work' },
        { name: 'Pausa Breve', minutes: 1, type: 'break' },
        { name: 'Cierre y Orden', minutes: 3, type: 'work' }
      ],
      18: [
        { name: 'Micro-Bloque 1', minutes: 4, type: 'work' },
        { name: 'Pausa Activa', minutes: 2, type: 'break' },
        { name: 'Micro-Bloque 2', minutes: 4, type: 'work' }
      ]
    },
    mediano: {
      13: [
        { name: 'Trabajo Enfocado', minutes: 10, type: 'work' },
        { name: 'Descanso Activo', minutes: 3, type: 'break' },
        { name: 'Trabajo de Cierre', minutes: 5, type: 'work' }
      ],
      18: [
        { name: 'Bloque 1 de Trabajo', minutes: 10, type: 'work' },
        { name: 'Pausa de Descanso', minutes: 3, type: 'break' },
        { name: 'Bloque 2 de Trabajo', minutes: 10, type: 'work' }
      ]
    },
    grande: {
      13: [
        { name: 'Estudio Profundo', minutes: 15, type: 'work' },
        { name: 'Descanso Activo', minutes: 3, type: 'break' },
        { name: 'Consolidación Analítica', minutes: 7, type: 'work' }
      ],
      18: [
        { name: 'Bloque 1 Pomodoro', minutes: 12, type: 'work' },
        { name: 'Descanso Mental', minutes: 4, type: 'break' },
        { name: 'Bloque 2 Pomodoro', minutes: 14, type: 'work' }
      ]
    }
  };

  // 3. BASE DAY METADATA
  const baseDays = [
    { day: 1, stage: 1, title: 'Registrar Tarea y Primera Acción Mínima', purpose: 'Reducir la fricción inicial al nivel más bajo posible definiendo una micro-acción inmediata.', actionsCount: 4 },
    { day: 2, stage: 1, title: 'Descomposición en 3 Pasos Ordenados', purpose: 'Dividir la tarea en una secuencia lineal manejable y clara.', actionsCount: 4 },
    { day: 3, stage: 1, title: 'Ejecución Enfocada en Paso 1', purpose: 'Iniciar directamente la primera acción con el temporizador sin dudar.', actionsCount: 3 },
    { day: 4, stage: 1, title: 'Acondicionamiento del Espacio de Estudio', purpose: 'Eliminar obstáculos físicos y preparar herramientas antes de comenzar.', actionsCount: 4 },
    { day: 5, stage: 1, title: 'Identificación de Distracciones y Regla Antidistracción', purpose: 'Anticipar tentaciones comunes y diseñar una barrera preventiva.', actionsCount: 3 },
    { day: 6, stage: 1, title: 'Priorización Rápida y Foco Único', purpose: 'Elegir una sola prioridad entre 3 pendientes y dedicarle trabajo concentrado.', actionsCount: 4 },
    { day: 7, stage: 1, title: 'Evaluación y Resumen de la Semana 1', purpose: 'Medir el impacto de los micro-pasos y consolidar el aprendizaje inicial.', actionsCount: 3 },
    { day: 8, stage: 2, title: 'Hora Programada y Puntualidad de Inicio', purpose: 'Establecer un compromiso horario y verificar el margen de inicio de 15 minutos.', actionsCount: 3 },
    { day: 9, stage: 2, title: 'Definir "Mi Próximo Paso" y Materiales', purpose: 'Dejar lista la acción futura para desplegarla como tarjeta destacada en el inicio.', actionsCount: 3 },
    { day: 10, stage: 2, title: 'Bloque de Enfoque Sostenido', purpose: 'Ampliar la capacidad de concentración continua y registrar avance real.', actionsCount: 3 },
    { day: 11, stage: 2, title: 'Tracción Continua en Paso Pendiente', purpose: 'Dar continuidad al trabajo en curso y actualizar su estado con honestidad.', actionsCount: 3 },
    { day: 12, stage: 2, title: 'Principio de Premack (Enfoque previo al Ocio)', purpose: 'Vincular una gratificación placentera como recompensa de una sesión previa.', actionsCount: 4 },
    { day: 13, stage: 2, title: 'Temporizador Guiado Secuencial (3 Fases)', purpose: 'Entrenar el ritmo de trabajo y descanso intercalado mediante bloques guiados.', actionsCount: 2 },
    { day: 14, stage: 2, title: 'Evaluación Semana 2 y Comparativa', purpose: 'Identificar las dos mejores estrategias y contrastar la facilidad de inicio.', actionsCount: 3 },
    { day: 15, stage: 3, title: 'Priorización Matricial 1 a 3 y Foco en la #1', purpose: 'Ordenar 3 tareas por impacto real y trabajar concentrado en la principal.', actionsCount: 4 },
    { day: 16, stage: 3, title: 'Confrontar la Tarea Evitada', purpose: 'Vencer la mayor resistencia psicológica dedicando un lapso exacto a lo postergado.', actionsCount: 4 },
    { day: 17, stage: 3, title: 'Preparación Anticipada "Para Mañana"', purpose: 'Dejar el camino libre de fricciones la noche anterior para arrancar al instante.', actionsCount: 4 },
    { day: 18, stage: 3, title: 'Sesión Doble Guiada de Enfoque Profundo', purpose: 'Consolidar la resistencia mental en dos bloques continuos con pausa activa.', actionsCount: 2 },
    { day: 19, stage: 3, title: 'Checklist Integral de 5 Puntos', purpose: 'Verificar sistemáticamente los 5 elementos del estudio autónomo.', actionsCount: 2 },
    { day: 20, stage: 3, title: 'Diseño del Plan Personal de Estudio Sostenible', purpose: 'Seleccionar las 2 estrategias maestras y definir la frecuencia semanal.', actionsCount: 3 },
    { day: 21, stage: 3, title: 'Evaluación Final, Graduación y Planta Madura', purpose: 'Cierre del programa de 21 días, balance de métricas objetivas y nuevo ciclo.', actionsCount: 3 }
  ];

  const base = baseDays[dayNum - 1] || baseDays[0];
  const validMag = HABIT_MAGNITUDES[magnitude] ? magnitude : 'mediano';
  const duration = durations[validMag][dayNum - 1];
  const sequence = sequences[validMag][dayNum] || null;

  return {
    ...base,
    duration,
    sequence,
    magnitude: validMag
  };
};

// Botanical Plant Stages based on total completed days (0 to 21)
const getPlantPhase = (completedDaysCount) => {
  if (completedDaysCount === 0) {
    return {
      phase: 1,
      name: 'Semilla Latente',
      range: 'Días 1-3',
      description: 'Tu intención está sembrada en tierra fértil. La constancia diaria despertará tu disciplina.',
      quote: '“El comienzo es la parte más importante de cualquier labor.”'
    };
  }
  if (completedDaysCount <= 3) {
    return {
      phase: 1,
      name: 'Semilla Germinando',
      range: 'Días 1-3',
      description: 'La semilla absorbe tus primeras acciones mínimas y prepara sus raíces.',
      quote: '“Haz que el primer paso sea tan pequeño que no puedas decir que no.”'
    };
  }
  if (completedDaysCount <= 7) {
    return {
      phase: 2,
      name: 'Brote Emergente',
      range: 'Días 4-7',
      description: '¡Ha roto la superficie! Dos hojas cotiledóneas emergen buscando la luz.',
      quote: '“La fricción inicial ha sido derrotada. Ya estás en movimiento.”'
    };
  }
  if (completedDaysCount <= 14) {
    return {
      phase: 3,
      name: 'Tallo Firme con Hojas',
      range: 'Días 8-14',
      description: 'El tallo se engrosa, ramifica hojas firmes y soporta periodos de trabajo continuos.',
      quote: '“La disciplina no es fuerza bruta, es la repetición tranquila de lo acordado.”'
    };
  }
  if (completedDaysCount <= 20) {
    return {
      phase: 4,
      name: 'Floración Radiante',
      range: 'Días 15-20',
      description: 'Follaje frondoso con capullos abriéndose en flores vivaces y mariposas sutiles.',
      quote: '“Tu concentración florece con naturalidad. Tienes control de tu tiempo.”'
    };
  }
  return {
    phase: 5,
    name: 'Árbol Maduro de Estudio',
    range: 'Día 21',
    description: 'Árbol majestuoso y frondoso cargado de frutos dorados de logro personal.',
    quote: '“¡Felicidades! Has completado los 21 días. El hábito ahora es parte de ti.”'
  };
};

// --- VIRTUAL PLANT SVG COMPONENT ---

window.HABIT_MAGNITUDES = HABIT_MAGNITUDES;
window.PROGRAM_STAGES = PROGRAM_STAGES;
window.getDayConfig = getDayConfig;
window.getPlantPhase = getPlantPhase;
