/**
 * HABITFLOW - data.js
 * Base de datos con los 6 métodos de mejora de hábitos de 7 días,
 * adaptaciones por rango de edad (13-15, 16-18, 19-20) y etapas de la planta virtual.
 * 
 * Cada método contiene 7 objetos con:
 * - dia (1 al 7)
 * - titulo
 * - descripcion (texto fiel al documento oficial)
 * - indicador
 * - adaptacionesPorEdad: { '13-15': ..., '16-18': ..., '19-20': ... }
 * - tipoInteraccion: tipo de campo de registro sugerido para enriquecer el seguimiento
 */

const HABITFLOW_DATA = {
  // Rangos de edad oficiales del documento
  ageRanges: [
    {
      id: '13-15',
      label: 'De 13 a 15 años',
      subtitle: 'Secundaria / Adolescencia temprana',
      icon: '🎒',
      description: 'Adaptado a tareas del colegio, videojuegos, dinámicas familiares y primeros hábitos independientes.',
      tone: 'Cercano, motivador, con ejemplos de tareas escolares y tiempo con amigos.'
    },
    {
      id: '16-18',
      label: 'De 16 a 18 años',
      subtitle: 'Preparatoria / Bachillerato',
      icon: '🎓',
      description: 'Adaptado a proyectos escolares, preparación para el futuro, vida social y equilibrio de tiempo.',
      tone: 'Práctico, enfocado en autonomía, autogestión y preparación.'
    },
    {
      id: '19-20',
      label: 'De 19 a 20 años',
      subtitle: 'Universidad / Jóvenes Adultos',
      icon: '💼',
      description: 'Adaptado a estudios universitarios, primeros empleos, independencia y gestión del descanso.',
      tone: 'Maduro, enfocado en productividad sostenible y bienestar personal.'
    }
  ],

  // Etapas de la planta virtual (Sección 7 del documento)
  plantStages: [
    {
      stage: 1,
      name: 'Semilla',
      minPercent: 0,
      maxPercent: 19.99,
      description: 'Una pequeña semilla sembrada en tierra fértil. Está esperando tu primer paso.',
      message: 'Todo gran cambio comienza con un primer día. ¡Da el paso!',
      levelName: 'Etapa 1: Semilla'
    },
    {
      stage: 2,
      name: 'Brote',
      minPercent: 20,
      maxPercent: 39.99,
      description: 'Un brote tierno de color verde vivo que emerge con fuerza de la tierra.',
      message: '¡El hábito empieza a asomarse! Cada día suma.',
      levelName: 'Etapa 2: Brote'
    },
    {
      stage: 3,
      name: 'Planta pequeña',
      minPercent: 40,
      maxPercent: 59.99,
      description: 'Un tallo firme con hojas frescas que absorben la luz del sol.',
      message: '¡Vas por la mitad del camino! La constancia es tu mejor abono.',
      levelName: 'Etapa 3: Planta pequeña'
    },
    {
      stage: 4,
      name: 'Planta desarrollada',
      minPercent: 60,
      maxPercent: 79.99,
      description: 'Follaje frondoso y resistente, con brotes florales listos para abrir.',
      message: '¡Casi lo logras! Tu hábito se siente cada vez más natural.',
      levelName: 'Etapa 4: Planta desarrollada'
    },
    {
      stage: 5,
      name: 'Planta florecida',
      minPercent: 80,
      maxPercent: 100,
      description: 'Una planta completamente florecida, radiante y llena de vida.',
      message: '¡Felicitaciones! Has completado el ciclo de 7 días con éxito admirable.',
      levelName: 'Etapa 5: Planta florecida'
    }
  ],

  // Los 6 métodos de hábitos (Sección 5 del documento oficial)
  methods: [
    {
      id: 'procrastinacion',
      number: 1,
      title: 'Procrastinación',
      tagline: 'Vence la postergación y arranca tus responsabilidades',
      icon: '⏳',
      color: 'amber',
      accentBg: 'from-amber-500/10 to-orange-500/10',
      borderColor: 'border-amber-300 dark:border-amber-600',
      badgeColor: 'bg-amber-100 text-amber-800 dark:bg-amber-900/40 dark:text-amber-300',
      objective: 'Ayudar al usuario a comenzar sus responsabilidades sin aplazarlas constantemente.',
      indicator: 'Actividades marcadas como completadas durante los siete días.',
      days: [
        {
          day: 1,
          title: 'Identificar las tareas pendientes',
          description: 'El usuario deberá identificar y escribir una tarea que normalmente pospone. Después, deberá indicar qué suele distraerlo o impedirle comenzar.',
          interactiveType: 'inputs_procrastination_d1',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Piensa en esa tarea escolar o deber en casa que dejas para el final porque prefieres jugar o ver videos. ¿Qué aplicación o juego suele ser tu mayor distracción?',
            '16-18': 'Identifica ese proyecto o estudio que vienes posponiendo. ¿Es el scroll infinito en TikTok/Instagram o la pereza lo que te frena al empezar?',
            '19-20': 'Elige ese reporte académico, trámite o pendiente de trabajo que postergas con frecuencia. Reconoce honestamente el disparador de tu distracción.'
          }
        },
        {
          day: 2,
          title: 'Preparar el espacio',
          description: 'El usuario deberá organizar el espacio donde realizará su tarea, retirar objetos que puedan distraerlo y dejar preparados los materiales necesarios.',
          interactiveType: 'checklist_workspace',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Despeja tu mesa o escritorio: retira consolas, cómics o juguetes. Deja listos tus cuadernos, lápices y la botella de agua.',
            '16-18': 'Limpia tu área de estudio, pon el móvil boca abajo o en otra mesa y ten listas las libretas o ventanas de la computadora necesarias.',
            '19-20': 'Prepara un entorno ergonómico y minimalista. Cierra pestañas irrelevantes del navegador y ten tu agenda y notas a mano.'
          }
        },
        {
          day: 3,
          title: 'La regla de los 10 minutos',
          description: 'El usuario deberá comenzar una tarea pendiente y trabajar en ella durante al menos 10 minutos sin interrupciones voluntarias.',
          interactiveType: 'timer_focus',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': 'Dile a tu cerebro: "Solo voy a trabajar 10 minutos". Pon el temporizador y concéntrate. Verás que después de empezar, continuar es fácil.',
            '16-18': 'Aplica el temporizador de 10 minutos. No mires notificaciones ni te levantes durante ese bloque. Arrancar es el 80% de la batalla.',
            '19-20': 'Aplica la regla de fricción mínima: trabaja enfocado 10 minutos completos sin cambiar de ventana ni mirar el móvil.'
          }
        },
        {
          day: 4,
          title: 'Dividir una tarea',
          description: 'El usuario deberá elegir una actividad grande y dividirla en tres pasos pequeños. Posteriormente, deberá completar el primero.',
          interactiveType: 'task_split_3',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Si tienes que leer un libro o hacer una maqueta: Paso 1 (buscar materiales/abrir libro), Paso 2 (leer 5 páginas), Paso 3 (hacer el resumen). ¡Haz el primero hoy!',
            '16-18': 'Toma una tarea pesada (ensayo o problema matemático). Divídela en 3 micro-pasos y tacha el primero hoy mismo.',
            '19-20': 'Aplica descomposición analítica: divide el entregable complejo en 3 entregables menores de 20 minutos y ejecuta el paso 1.'
          }
        },
        {
          day: 5,
          title: 'Eliminar una distracción',
          description: 'El usuario deberá identificar una distracción frecuente y evitarla durante un bloque de trabajo de 15 minutos.',
          interactiveType: 'timer_distraction',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Pon el móvil en modo silencio y déjalo en otra habitación. Durante 15 minutos solo existe tu tarea.',
            '16-18': 'Activa el modo "No molestar" o cierra WhatsApp Web. Concéntrate en tu objetivo durante 15 minutos completos.',
            '19-20': 'Silencia canales de mensajería y notificaciones emergentes en tus dispositivos durante un bloque de trabajo enfocado de 15 minutos.'
          }
        },
        {
          day: 6,
          title: 'Planificar antes de comenzar',
          description: 'El usuario deberá escribir tres actividades pendientes, establecer cuál es la más importante y dedicar al menos 15 minutos a comenzar esa actividad.',
          interactiveType: 'priority_trio',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Anota 3 pendientes de la escuela. Marca con una estrella el más urgente y dedícale 15 minutos de reloj ya mismo.',
            '16-18': 'Escribe 3 compromisos de tu semana. Identifica el que tiene mayor impacto y dedícale un bloque de 15 minutos sin pausas.',
            '19-20': 'Aplica la matriz de prioridad simple con 3 compromisos. Ejecuta el de mayor valor estratégico durante 15 minutos iniciales.'
          }
        },
        {
          day: 7,
          title: 'Evaluar los avances',
          description: 'El usuario deberá revisar las actividades realizadas durante la semana e identificar qué estrategia le ayudó más a comenzar sus responsabilidades.',
          interactiveType: 'reflection_review',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Mira atrás: ¿te sirvió más la regla de los 10 minutos, ordenar tu mesa o quitar el celular? ¡Elige tu superpoder!',
            '16-18': 'Analiza qué técnica te dio mayor tracción y cómo puedes incorporarla a tu rutina semanal habitual.',
            '19-20': 'Evalúa tu rendimiento y fricción reducida: identifica la técnica con mayor retorno sobre tu tiempo y productividad.'
          }
        }
      ]
    },
    {
      id: 'organizacion_personal',
      number: 2,
      title: 'Organización Personal',
      tagline: 'Desarrolla una rutina sólida de planificación diaria',
      icon: '📋',
      color: 'blue',
      accentBg: 'from-blue-500/10 to-indigo-500/10',
      borderColor: 'border-blue-300 dark:border-blue-600',
      badgeColor: 'bg-blue-100 text-blue-800 dark:bg-blue-900/40 dark:text-blue-300',
      objective: 'Desarrollar una rutina básica de organización y planificación de actividades.',
      indicator: 'Actividades organizadas, registradas y realizadas.',
      days: [
        {
          day: 1,
          title: 'Identificar responsabilidades',
          description: 'El usuario deberá escribir cinco actividades o responsabilidades que tenga pendientes durante la semana.',
          interactiveType: 'list_5_items',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Escribe 5 cosas que tienes que hacer esta semana (ej: tarea de historia, pasear al perro, ordenar tu mochila, práctica de deportes, etc.).',
            '16-18': 'Lista 5 responsabilidades de tus estudios, compromisos familiares o extracurriculares pendientes para los próximos días.',
            '19-20': 'Registra tus 5 pendientes clave de la semana entre cursos, responsabilidades laborales, pagos o trámites personales.'
          }
        },
        {
          day: 2,
          title: 'Establecer prioridades',
          description: 'El usuario deberá seleccionar tres actividades importantes de su lista y ordenarlas según su prioridad.',
          interactiveType: 'prioritize_3_items',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'De tus pendientes, elige las 3 más importantes (lo que vence primero o tiene mayor nota) y ponlas en orden: 1, 2 y 3.',
            '16-18': 'Selecciona las 3 de mayor relevancia y ordénalas según urgencia e impacto para tu semana.',
            '19-20': 'Aplica el principio de Pareto: identifica las 3 actividades esenciales y jerarquízalas por orden de prioridad.'
          }
        },
        {
          day: 3,
          title: 'Crear una lista diaria',
          description: 'El usuario deberá elaborar una lista con tres actividades que quiera completar durante el día y marcar cada una cuando termine.',
          interactiveType: 'daily_3_checklist',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Escribe las 3 victorias que quieres lograr hoy. ¡Marca cada una conforme las vayas tachando!',
            '16-18': 'Define tu lista de 3 objetivos clave para el día de hoy y márcalos a medida que los termines.',
            '19-20': 'Establece tu "Trío del Día": 3 compromisos no negociables. Marca cada uno al finalizarlo.'
          }
        },
        {
          day: 4,
          title: 'Organizar el espacio',
          description: 'El usuario deberá dedicar 10 minutos a ordenar su espacio de estudio, trabajo o actividades personales.',
          interactiveType: 'timer_focus',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': 'Pon música y dedica 10 minutos a recoger tu cuarto, guardar ropa o doblar cosas sobre tu escritorio.',
            '16-18': 'Toma 10 minutos para ordenar tus notas, archivar papeles y dejar tu lugar de trabajo impecable.',
            '19-20': 'Dedica un bloque de 10 minutos al despeje de tu escritorio físico y la bandeja de entrada digital.'
          }
        },
        {
          day: 5,
          title: 'Establecer horarios',
          description: 'El usuario deberá asignar un horario aproximado a tres actividades de su rutina y procurar respetarlo.',
          interactiveType: 'schedule_3_slots',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Elige 3 momentos: ej. Tareas a las 4:00 PM, deportes a las 6:00 PM, tiempo libre a las 7:30 PM. ¡Intenta cumplirlos!',
            '16-18': 'Asigna bloques horarios específicos a tus 3 actividades principales del día y protégelos de distracciones.',
            '19-20': 'Implementa una técnica básica de time-blocking asignando franjas horarias a 3 tareas clave de tu jornada.'
          }
        },
        {
          day: 6,
          title: 'Preparar el día siguiente',
          description: 'El usuario deberá preparar los materiales, pendientes y actividades que necesitará para el día siguiente.',
          interactiveType: 'prep_next_day',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Prepara tu mochila escolar esta noche, la ropa que te vas a poner y tus cuadernos listos antes de acostarte.',
            '16-18': 'Deja lista tu mochila o maletín, tus pendientes anotados y los archivos descargados para el día de mañana.',
            '19-20': 'Prepara el kit del día siguiente: ropa, dispositivo cargado, agenda revisada y prioridades predefinidas.'
          }
        },
        {
          day: 7,
          title: 'Revisar la organización',
          description: 'El usuario deberá revisar qué actividades logró completar, cuáles quedaron pendientes y qué podría organizar mejor la próxima semana.',
          interactiveType: 'reflection_review',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': '¿Cómo te sentiste teniendo todo planeado? ¿Qué fue lo más fácil y qué te costó más recordar?',
            '16-18': 'Haz tu balance: evalúa tu cumplimiento y detecta qué ajustes en tus horarios te darán más tranquilidad.',
            '19-20': 'Realiza tu revisión semanal: analiza tu tasa de resolución de pendientes y planifica tu estrategia de mejora.'
          }
        }
      ]
    },
    {
      id: 'lectura',
      number: 3,
      title: 'Hábito de Lectura',
      tagline: 'Incorpora la lectura constante mediante periodos progresivos',
      icon: '📚',
      color: 'emerald',
      accentBg: 'from-emerald-500/10 to-teal-500/10',
      borderColor: 'border-emerald-300 dark:border-emerald-600',
      badgeColor: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300',
      objective: 'Incorporar la lectura como una actividad frecuente mediante periodos cortos y progresivos.',
      indicator: 'Tiempo de lectura realizado y actividades de reflexión completadas.',
      days: [
        {
          day: 1,
          title: 'Elegir una lectura',
          description: 'El usuario deberá seleccionar un libro, artículo o texto de su interés y leer durante 5 minutos.',
          interactiveType: 'reading_tracker_d1',
          timerMinutes: 5,
          ageAdaptations: {
            '13-15': 'Elige algo que de verdad te llame la atención: novela juvenil, cómic, manga o artículo sobre ciencia o deportes. Lee solo 5 minutos.',
            '16-18': 'Elige un libro de desarrollo, ficción interesante o un artículo largo de actualidad. Conéctate con la lectura 5 minutos.',
            '19-20': 'Selecciona un libro formativo, ensayo o lectura literaria de tu preferencia y realiza una sesión inicial de 5 minutos.'
          }
        },
        {
          day: 2,
          title: 'Crear un espacio de lectura',
          description: 'El usuario deberá encontrar un lugar tranquilo, reducir las distracciones y leer durante 5 minutos.',
          interactiveType: 'reading_tracker_timed',
          timerMinutes: 5,
          ageAdaptations: {
            '13-15': 'Busca un sillón cómodo o tu cama con buena luz. Apaga la tele o pon música suave sin letra por 5 minutos.',
            '16-18': 'Encuentra un rincón con buena iluminación y silencio. Deja el móvil lejos durante estos 5 minutos de lectura.',
            '19-20': 'Diseña un santuario de lectura sin notificaciones ni estímulos visuales distractores durante 5 minutos.'
          }
        },
        {
          day: 3,
          title: 'Aumentar el tiempo',
          description: 'El usuario deberá dedicar 10 minutos a la lectura seleccionada.',
          interactiveType: 'reading_tracker_timed',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': '¡Subimos a 10 minutos! Sumérgete en la historia. Si te engancha, verás que el tiempo vuela.',
            '16-18': 'Duplica el tiempo: 10 minutos continuos. Concéntrate en el fluir del texto y la narrativa.',
            '19-20': 'Extiende tu sesión a 10 minutos de lectura focalizada y comprensiva sin interrupciones.'
          }
        },
        {
          day: 4,
          title: 'Identificar ideas importantes',
          description: 'El usuario deberá leer durante 10 minutos y escribir una idea o información que le haya parecido interesante.',
          interactiveType: 'reading_note_1_idea',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': 'Lee 10 minutos y anota una frase curiosa, un dato que no sabías o lo que más te sorprendió del personaje.',
            '16-18': 'Lee 10 minutos y rescata una idea clave o concepto que te haga reflexionar o que puedas aplicar.',
            '19-20': 'Sesión de 10 minutos con toma de notas activa: sintetiza la tesis o aprendizaje más potente del fragmento leído.'
          }
        },
        {
          day: 5,
          title: 'Mantener la rutina',
          description: 'El usuario deberá dedicar 15 minutos a leer, procurando hacerlo en un horario similar al de los días anteriores.',
          interactiveType: 'reading_tracker_timed',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Lee 15 minutos a la misma hora (por ejemplo, antes de cenar o antes de dormir). ¡Tu cerebro se acostumbra a ese momento!',
            '16-18': '15 minutos a la misma hora del día. La consistencia horaria ancla el hábito en tu sistema nervioso.',
            '19-20': 'Consolida la ventana de lectura habitual con 15 minutos de inmersión en tu horario seleccionado.'
          }
        },
        {
          day: 6,
          title: 'Compartir lo aprendido',
          description: 'El usuario deberá leer durante 15 minutos y escribir tres ideas que recuerde del texto.',
          interactiveType: 'reading_notes_3_ideas',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Lee 15 minutos y anota 3 cosas divertidas o interesantes que le contarías a tu mejor amigo sobre lo leído.',
            '16-18': 'Sesión de 15 minutos y extracción de 3 aprendizajes memorables. Explicar lo leído consolida la memoria.',
            '19-20': 'Lectura profunda de 15 minutos y redacción de 3 puntos clave o aplicaciones prácticas derivadas del texto.'
          }
        },
        {
          day: 7,
          title: 'Evaluar la experiencia',
          description: 'El usuario deberá revisar cuánto tiempo dedicó a la lectura durante la semana e identificar qué tipo de textos le gustaría continuar leyendo.',
          interactiveType: 'reflection_review',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': '¡Llegaste al día 7! ¿Cuánto leíste en total? ¿Te gustaría seguir con este libro o probar con otro género?',
            '16-18': 'Evalúa tu semana lectora: calcula el tiempo total y proyecta qué lectura abordará tu próximo ciclo.',
            '19-20': 'Balance semanal: analiza tu velocidad de absorción y define tu lista de lecturas recomendadas para el mes.'
          }
        }
      ]
    },
    {
      id: 'actividad_fisica',
      number: 4,
      title: 'Actividad Física',
      tagline: 'Suma movimiento a tu día de forma sencilla y progresiva',
      icon: '🏃',
      color: 'rose',
      accentBg: 'from-rose-500/10 to-pink-500/10',
      borderColor: 'border-rose-300 dark:border-rose-600',
      badgeColor: 'bg-rose-100 text-rose-800 dark:bg-rose-900/40 dark:text-rose-300',
      objective: 'Incorporar movimiento a la rutina cotidiana mediante actividades físicas sencillas y progresivas.',
      indicator: 'Actividades físicas realizadas y tiempo registrado.',
      safetyNote: 'Las actividades deben adaptarse a las condiciones físicas de cada persona. Puedes descansar o detenerte si sientes dolor, mareo o malestar.',
      days: [
        {
          day: 1,
          title: 'Identificar el punto de partida',
          description: 'El usuario deberá realizar una caminata tranquila de 5 minutos y registrar cómo se sintió.',
          interactiveType: 'physical_activity_log',
          timerMinutes: 5,
          ageAdaptations: {
            '13-15': 'Sal a dar una vuelta de 5 minutos al parque, alrededor de tu patio o por tu cuadra. Respira aire fresco y fíjate cómo se activa tu cuerpo.',
            '16-18': 'Camina a ritmo moderado 5 minutos continuos. Observa tu energía y frecuencia cardíaca inicial.',
            '19-20': 'Realiza una caminata de 5 minutos como pausa activa de tu jornada y registra tu sensación de vitalidad.'
          }
        },
        {
          day: 2,
          title: 'Aumentar el movimiento',
          description: 'El usuario deberá realizar una caminata de 10 minutos a un ritmo cómodo.',
          interactiveType: 'physical_activity_log',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': 'Duplica a 10 minutos. Puedes invitar a tu mascota o escuchar tu música favorita mientras caminas.',
            '16-18': '10 minutos de caminata ágil. Mantén una postura erguida y siente cómo se oxigena tu mente.',
            '19-20': '10 minutos a ritmo sostenido. Ideal para despejar la mente entre tareas de estudio o laborales.'
          }
        },
        {
          day: 3,
          title: 'Incorporar estiramientos',
          description: 'El usuario deberá realizar 5 minutos de estiramientos suaves y una caminata de 5 minutos.',
          interactiveType: 'physical_activity_combo',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': '5 min de estiramientos (brazos arriba, tocarte las puntas de los pies con cuidado, girar el cuello) + 5 min de caminata.',
            '16-18': '5 minutos de movilidad articular de tren superior e inferior seguido de una caminata revitalizante de 5 minutos.',
            '19-20': 'Rutina combinada: 5 minutos de descompresión postural y estiramiento + 5 minutos de marcha aeróbica.'
          }
        },
        {
          day: 4,
          title: 'Elegir una actividad',
          description: 'El usuario deberá seleccionar una actividad física de su preferencia, como caminar, bailar o andar en bicicleta, y realizarla durante 10 minutos.',
          interactiveType: 'physical_custom_activity',
          timerMinutes: 10,
          ageAdaptations: {
            '13-15': '¡Elige lo que más te divierta! Bailar con Just Dance/TikTok, andar en patineta, saltar cuerda o básquet por 10 minutos.',
            '16-18': 'Elige tu actividad favorita (bici, trote suave, bailar o calistenia básica) y practicala 10 minutos.',
            '19-20': 'Sesión de 10 minutos de la disciplina aeróbica que más disfrutes (bicicleta, natación, trote o baile).'
          }
        },
        {
          day: 5,
          title: 'Mantener el movimiento',
          description: 'El usuario deberá realizar 15 minutos de actividad física moderada, de acuerdo con sus posibilidades.',
          interactiveType: 'physical_activity_log',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': '15 minutos de actividad continua. Recuerda tomar agua si tienes sed y detenerte si te cansas demasiado.',
            '16-18': 'Mantén el movimiento durante 15 minutos a intensidad moderada. Disfruta de la liberación de endorfinas.',
            '19-20': 'Bloque de 15 minutos continuos de actividad cardiovascular a ritmo constante y disfrutable.'
          }
        },
        {
          day: 6,
          title: 'Repetir una actividad favorita',
          description: 'El usuario deberá repetir la actividad física que más disfrutó durante la semana durante 15 minutos.',
          interactiveType: 'physical_custom_activity',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': '¿Cuál de los días anteriores te gustó más? Repítelo durante 15 minutos a tu ritmo y con buena actitud.',
            '16-18': 'Vuelve a la actividad que te generó mayor bienestar por 15 minutos completos.',
            '19-20': 'Sesión de 15 minutos reforzando la práctica que más disfrutaste durante esta semana.'
          }
        },
        {
          day: 7,
          title: 'Evaluar la experiencia',
          description: 'El usuario deberá registrar qué actividad disfrutó más y elegir una forma sencilla de continuar incorporando movimiento a su rutina.',
          interactiveType: 'reflection_review',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': '¡Semana completa! ¿Cómo se siente tu cuerpo con más movimiento? ¿Qué deporte o juego quieres seguir practicando?',
            '16-18': 'Evalúa tu nivel de energía y decide qué hábito de movimiento integrarás a tus días de clases.',
            '19-20': 'Balance físico: documenta las mejoras en tu estado de ánimo y establece tu plan semanal continuo de movimiento.'
          }
        }
      ]
    },
    {
      id: 'sueno',
      number: 5,
      title: 'Hábitos de Sueño',
      tagline: 'Construye un descanso reparador y reduce el insomnio nocturno',
      icon: '🌙',
      color: 'indigo',
      accentBg: 'from-indigo-500/10 to-purple-500/10',
      borderColor: 'border-indigo-300 dark:border-indigo-600',
      badgeColor: 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900/40 dark:text-indigo-300',
      objective: 'Fomentar una rutina de descanso más organizada y reducir las actividades que dificultan prepararse para dormir.',
      indicator: 'Registro de horarios y realización de las actividades nocturnas.',
      days: [
        {
          day: 1,
          title: 'Identificar los horarios',
          description: 'El usuario deberá registrar aproximadamente a qué hora se duerme y a qué hora se despierta.',
          interactiveType: 'sleep_schedule_log',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Anota con sinceridad a qué hora te metes a la cama y a qué hora te despiertas. ¿Duermes las 8 a 9 horas recomendadas?',
            '16-18': 'Registra tus horas reales de sueño. Identifica si hay desfase entre los días con clases y los fines de semana.',
            '19-20': 'Calcula tu ventana de descanso y detecta tu déficit acumulado de horas de sueño entre semana.'
          }
        },
        {
          day: 2,
          title: 'Preparar una rutina nocturna',
          description: 'El usuario deberá elegir una actividad relajante para realizar antes de dormir, como leer, escuchar música tranquila o preparar sus cosas para el día siguiente.',
          interactiveType: 'sleep_routine_select',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Elige algo tranquilo para los 20 minutos antes de dormir: música suave, leer un cómic o preparar tu ropa para mañana.',
            '16-18': 'Selecciona tu ritual relajante: estiramientos suaves, infusión caliente o lectura ligera con luz tenue.',
            '19-20': 'Implementa una rutina de desaceleración cognitiva: desconexión de pantallas, higiene postural y lectura calmante.'
          }
        },
        {
          day: 3,
          title: 'Reducir las distracciones',
          description: 'El usuario deberá evitar utilizar dispositivos electrónicos durante los últimos 15 minutos antes de acostarse.',
          interactiveType: 'timer_screenfree',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Apaga el celular, tablet o consola 15 minutos antes de dormir. La luz azul de las pantallas le dice a tu cerebro que es de día.',
            '16-18': 'Coloca el teléfono en modo silencio y déjalo lejos de la almohada durante los 15 minutos previos a dormir.',
            '19-20': 'Cero pantallas durante los 15 minutos finales antes de apagar la luz. Permite que la melatonina natural actúe.'
          }
        },
        {
          day: 4,
          title: 'Establecer un horario',
          description: 'El usuario deberá elegir un horario de descanso adecuado para sus responsabilidades y procurar mantenerlo.',
          interactiveType: 'sleep_target_time',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Elige una hora fija para acostarte hoy (ejemplo: 10:00 PM) para despertar con energía sin que te cueste trabajo.',
            '16-18': 'Fija tu hora objetivo de dormir y despertar para garantizar al menos 7.5 a 8 horas de sueño continuo.',
            '19-20': 'Establece tu horario de inicio de sueño consistente para estabilizar tu ritmo circadiano.'
          }
        },
        {
          day: 5,
          title: 'Preparar el ambiente',
          description: 'El usuario deberá organizar su espacio de descanso, procurando que sea cómodo, tranquilo y con poca iluminación.',
          interactiveType: 'sleep_room_check',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Baja las luces de tu cuarto, acomoda tu almohada y cobijas, y asegúrate de que no haya ruidos molestos.',
            '16-18': 'Optimiza la temperatura de la habitación, reduce la luz ambiental al mínimo y ventila el espacio.',
            '19-20': 'Crea un ambiente de sueño óptimo: habitación fresca, oscuridad total o luz cálida mínima y reducción de ruido.'
          }
        },
        {
          day: 6,
          title: 'Repetir la rutina',
          description: 'El usuario deberá repetir la rutina nocturna que haya elegido y registrar si le resultó fácil realizarla.',
          interactiveType: 'sleep_repeat_check',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Vuelve a hacer tu ritual relajante de hoy. ¿Te dio menos flojera? ¿Te dormiste más rápido?',
            '16-18': 'Aplica de nuevo tu rutina previa al sueño y observa si tu mente se calma con mayor rapidez.',
            '19-20': 'Consolida tu ritual nocturno y evalúa la latencia del sueño (el tiempo que tardas en conciliarlo).'
          }
        },
        {
          day: 7,
          title: 'Evaluar los cambios',
          description: 'El usuario deberá revisar sus horarios y hábitos nocturnos e identificar qué cambios le ayudaron a descansar mejor.',
          interactiveType: 'reflection_review',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': '¡Día 7! ¿Te sientes menos cansado al despertar por las mañanas? ¿Qué truco te ayudó más?',
            '16-18': 'Revisa la calidad de tu descanso esta semana y consolida las 2 pautas que mejor efecto tuvieron en tu energía.',
            '19-20': 'Evalúa tu rendimiento diurno en función de la mejora del sueño y establece tus estándares de descanso.'
          }
        }
      ]
    },
    {
      id: 'celular',
      number: 6,
      title: 'Reducir Uso Excesivo del Celular',
      tagline: 'Uso más consciente y recuperación del tiempo frente a pantallas',
      icon: '📵',
      color: 'teal',
      accentBg: 'from-teal-500/10 to-cyan-500/10',
      borderColor: 'border-teal-300 dark:border-teal-600',
      badgeColor: 'bg-teal-100 text-teal-800 dark:bg-teal-900/40 dark:text-teal-300',
      objective: 'Fomentar un uso más consciente del teléfono y reducir las distracciones relacionadas con su utilización recreativa.',
      indicator: 'Periodos sin celular, límites establecidos y comparación del tiempo de pantalla.',
      days: [
        {
          day: 1,
          title: 'Identificar el tiempo de uso',
          description: 'El usuario deberá consultar el registro de tiempo de pantalla de su dispositivo e identificar cuánto tiempo dedica a utilizarlo.',
          interactiveType: 'screentime_log_initial',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Abre los ajustes de tu celular (Bienestar Digital o Tiempo en Pantalla). Mira cuántas horas pasaste hoy y anótalo sin pena.',
            '16-18': 'Revisa tu reporte de tiempo de pantalla diario. Conoce la cifra exacta de horas que pasas frente al móvil.',
            '19-20': 'Audita tu consumo digital en la configuración del teléfono e identifica el promedio semanal de horas de uso.'
          }
        },
        {
          day: 2,
          title: 'Identificar aplicaciones distractoras',
          description: 'El usuario deberá identificar las dos aplicaciones que más tiempo le consumen y registrar cuánto tiempo utiliza cada una.',
          interactiveType: 'screentime_top_apps',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Fíjate cuáles son las dos apps que más tiempo te quitan (ej: TikTok, YouTube, Instagram o algún juego) y sus minutos.',
            '16-18': 'Identifica las dos aplicaciones que dominan tus estadísticas de uso recreativo y registra el tiempo consumido.',
            '19-20': 'Aísla los dos sumideros principales de tiempo y dopamina en tu dispositivo y documenta sus métricas.'
          }
        },
        {
          day: 3,
          title: 'Establecer un periodo sin celular',
          description: 'El usuario deberá pasar 15 minutos realizando una actividad sin utilizar el teléfono.',
          interactiveType: 'timer_screenfree',
          timerMinutes: 15,
          ageAdaptations: {
            '13-15': 'Guarda el celular en tu mochila o déjalo cargando en otra habitación por 15 minutos. Dibuja, platica o sal al patio.',
            '16-18': 'Dedica 15 minutos continuos a una actividad física o manual sin tocar el teléfono una sola vez.',
            '19-20': 'Implementa una franja de descompresión analógica de 15 minutos libre de dispositivos.'
          }
        },
        {
          day: 4,
          title: 'Desactivar distracciones',
          description: 'El usuario deberá desactivar las notificaciones no esenciales de una aplicación que utilice frecuentemente.',
          interactiveType: 'screentime_notif_off',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Entra a ajustes y apaga las notificaciones de esa app que no para de sonar y hacerte mirar la pantalla a cada rato.',
            '16-18': 'Desactiva notificaciones push innecesarias (likes, recordatorios automáticos de juegos o promociones).',
            '19-20': 'Configura el modo concentración y apaga notificaciones no críticas para evitar la fragmentación de tu atención.'
          }
        },
        {
          day: 5,
          title: 'Sustituir el tiempo de pantalla',
          description: 'El usuario deberá elegir una actividad alternativa, como dibujar, leer, caminar o escuchar música sin revisar constantemente el teléfono, durante 20 minutos.',
          interactiveType: 'screentime_substitute',
          timerMinutes: 20,
          ageAdaptations: {
            '13-15': '20 minutos haciendo algo divertido sin pantalla: armar legos, dibujar, practicar un instrumento o jugar con tu mascota.',
            '16-18': 'Sustituye 20 minutos de redes sociales por lectura, tocar música, salir a caminar o cocinar algo rico.',
            '19-20': 'Dedica un bloque de 20 minutos a un pasatiempo análogo o actividad física sin chequear el móvil.'
          }
        },
        {
          day: 6,
          title: 'Establecer límites',
          description: 'El usuario deberá establecer un límite diario de uso recreativo para una de las aplicaciones identificadas y procurar respetarlo.',
          interactiveType: 'screentime_limit_set',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': 'Pon un temporizador en el celular a esa app (ej: máximo 45 min en TikTok al día). ¡Intenta no saltarte el aviso!',
            '16-18': 'Configura un límite de tiempo en la app de mayor consumo y comprométete a no ignorar el bloqueo al vencer.',
            '19-20': 'Establece un cupo máximo de uso diario para redes sociales recreativas en los controles de tu sistema operativo.'
          }
        },
        {
          day: 7,
          title: 'Comparar resultados',
          description: 'El usuario deberá consultar nuevamente su tiempo de pantalla y compararlo con el registro del primer día. Después, identificará qué estrategia le resultó más útil.',
          interactiveType: 'screentime_compare_results',
          timerMinutes: null,
          ageAdaptations: {
            '13-15': '¡Día 7! Mira tu tiempo de pantalla de hoy y compáralo con el día 1. ¿Lograste bajar los minutos? ¿Qué truco te sirvió más?',
            '16-18': 'Compara tus métricas de pantalla con las iniciales. Analiza el impacto de apagar notificaciones y poner límites.',
            '19-20': 'Calcula la reducción neta en horas y minutos frente a pantallas y formaliza las reglas digitales que mantendrás activas.'
          }
        }
      ]
    }
  ]
};

// Hacer accesible en window
if (typeof window !== 'undefined') {
  window.HABITFLOW_DATA = HABITFLOW_DATA;
}
