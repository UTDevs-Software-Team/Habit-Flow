/**
 * PARAMETROS BASE DE HABITFLOW (CONFIGURACION CENTRALIZADA)
 *
 * Modifica cualquiera de estos valores para ajustar duraciones, tolerancias,
 * etapas o umbrales de la planta sin tocar la logica de React en index.html.
 */
window.HABITFLOW_BASE_PARAMS = {
  TOTAL_DAYS: 21,
  DEFAULT_HABIT: 'Vencer la procrastinacion e iniciar sesiones de estudio efectivas',
  DEFAULT_MAGNITUDE: 'mediano', // 'chico' | 'mediano' | 'grande'
  PUNCTUALITY_WINDOW_MINUTES: 15,

  MAGNITUDES: {
    chico: {
      id: 'chico',
      name: 'Chico (Baja complejidad / Fisico rapido)',
      shortName: 'Chico',
      tag: 'Baja Complejidad',
      color: 'emerald',
      badgeBg: 'bg-emerald-100 text-emerald-800 border-emerald-300',
      icon: '🧹',
      examples: 'Ordenar el cuarto, tender la cama, hidratacion, recoger el escritorio, 5 estiramientos.',
      description: 'Micro-tareas fisicas o de baja carga cognitiva. No requieren 10 o 25 minutos. Lo dificil es la inercia de levantarse. Los lapsos van de 2 a 5 min en la Etapa 1 y se consolidan en rutinas agiles de 5 a 10 min.',
      timeRangeLabel: '2 a 10 minutos por dia',
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
      examples: 'Lectura de libros, repasar apuntes del dia, practicar idioma (Duolingo), diario reflexivo, ejercicio ligero.',
      description: 'Habitos diarios balanceados entre concentracion mental y constancia de rutina. Lapsos estructurados de 5 a 10 min al inicio, alcanzando de 10 a 20 min en la etapa de autonomia.',
      timeRangeLabel: '5 a 23 minutos por dia',
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
      examples: 'Estudiar materias complejas (calculo, fisica, medicina), redactar tesis/ensayos, programar software, resolver problemas analiticos.',
      description: 'Proyectos academicos y profesionales de alta exigencia mental. Arranca con 5-8 min de friccion cero para vencer la intimidacion, escalando a bloques profundos de 15 a 30 min (tecnica Pomodoro guiada).',
      timeRangeLabel: '5 a 30 minutos por dia',
      totalPlannedMinutes: 257
    }
  },

  // Duracion en minutos por dia (Dia 1 al Dia 21) para cada magnitud
  DURATIONS: {
    chico:   [2, 3, 3, 3, 3, 4, 2,  5, 3,  5,  5,  5,  8, 3,  5,  4, 3, 10,  5, 3, 3],
    mediano: [5, 5, 5, 5, 5, 7, 3,  8, 5, 10,  8, 10, 18, 5, 10,  5, 5, 23, 10, 5, 5],
    grande:  [5, 7, 8, 6, 8, 10, 5, 12, 8, 15, 12, 15, 25, 7, 15, 10, 8, 30, 15, 8, 8]
  },

  // Bloques multi-fase guiados para los dias 13 y 18
  SEQUENCES: {
    chico: {
      13: [
        { name: 'Accion Rapida', minutes: 4, type: 'work' },
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
        { name: 'Consolidacion Analitica', minutes: 7, type: 'work' }
      ],
      18: [
        { name: 'Bloque 1 Pomodoro', minutes: 12, type: 'work' },
        { name: 'Descanso Mental', minutes: 4, type: 'break' },
        { name: 'Bloque 2 Pomodoro', minutes: 14, type: 'work' }
      ]
    }
  },

  STAGES: [
    { id: 1, name: 'Etapa 1: Fundamentos y Micro-pasos', days: 'Dias 1 a 7', color: 'indigo' },
    { id: 2, name: 'Etapa 2: Consistencia y Ritmo', days: 'Dias 8 a 14', color: 'blue' },
    { id: 3, name: 'Etapa 3: Consolidacion y Autonomia', days: 'Dias 15 a 21', color: 'emerald' }
  ]
};
