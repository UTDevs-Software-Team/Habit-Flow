/**
 * HabitFlow: Recovery - recovery.js
 * Módulo central: Catálogo de las 5 adicciones, generador de planes y metas
 * a corto, mediano y largo plazo, hábitos de reemplazo específicos y sistema de crisis S.O.S.
 */

const ADDICTION_DATABASE = {
  social_media: {
    id: 'social_media',
    name: 'Redes Sociales y Celular Compulsivo',
    tagline: 'Supera el doomscrolling en TikTok, Reels, Instagram y el desvelo con pantallas',
    icon: '📱',
    accentColor: '#3b82f6',
    unitType: 'hours',
    defaultDailyValue: 4,
    unitLabel: 'horas al día',
    impactLabel: 'Horas de vida recuperadas',
    emergencyTips: [
      'Deja el teléfono en otra habitación durante al menos 15 minutos.',
      'Activa la pantalla en escala de grises (blanco y negro) para apagar la dopamina visual.',
      'Sal a dar un paseo de 5 minutos al aire libre sin llevar el teléfono contigo.',
      'Bebe un vaso de agua fría y haz 10 respiraciones conscientes.',
      'Abre un libro físico o cuaderno de notas y lee o escribe 2 páginas.'
    ],
    shortTermGoals: [
      { id: 'sm_s1', title: 'Regla de los primeros 30 minutos', desc: 'No tocar el celular durante los primeros 30 minutos al despertar.', period: 'Días 1 a 3', completed: false },
      { id: 'sm_s2', title: 'Habitación libre de pantallas para dormir', desc: 'Cargar el celular fuera del alcance de la cama 30 min antes de dormir.', period: 'Días 3 a 5', completed: false },
      { id: 'sm_s3', title: 'Eliminar accesos directos adictivos', desc: 'Mover Instagram/TikTok a una carpeta oculta o desinstalar por 48 horas.', period: 'Días 5 a 7', completed: false }
    ],
    mediumTermGoals: [
      { id: 'sm_m1', title: 'Desactivar notificaciones no esenciales', desc: 'Silenciar alertas de likes, sugerencias y aplicaciones sociales.', period: 'Semana 2', completed: false },
      { id: 'sm_m2', title: 'Límite diario de 90 minutos de pantalla', desc: 'Configurar temporizador en el sistema para bloquear apps tras 1.5 horas.', period: 'Semana 3', completed: false },
      { id: 'sm_m3', title: 'Medio día de Détox Digital', desc: 'Dedicar una mañana o tarde entera del fin de semana completamente sin redes.', period: 'Semana 4', completed: false }
    ],
    longTermGoals: [
      { id: 'sm_l1', title: 'Presencia plena en relaciones', desc: 'Cero celulares en mesas de comida, reuniones familiares o con amigos.', period: 'Mes 1', completed: false },
      { id: 'sm_l2', title: 'Inversión en proyectos y pasatiempos', desc: 'Dedicar el tiempo libre ganado a lectura, deporte o habilidades nuevas.', period: 'Mes 2', completed: false },
      { id: 'sm_l3', title: 'Identidad recuperada y control total', desc: 'Usar la tecnología como herramienta consciente, no como escape automático.', period: 'Mes 3+', completed: false }
    ],
    dailyReplacements: [
      { id: 'rep_sm_1', title: 'Despertar sin pantalla (primeros 30 min)', icon: '🌅' },
      { id: 'rep_sm_2', title: 'Lectura en libro físico o aprendizaje', icon: '📖' },
      { id: 'rep_sm_3', title: 'Dormir con celular fuera de la cama', icon: '🌙' }
    ]
  },

  vaping: {
    id: 'vaping',
    name: 'Vapeo, Cigarrillos y Nicotina',
    tagline: 'Vence la dependencia oral y química a vapes descartables, pods y tabaco',
    icon: '💨',
    accentColor: '#06b6d4',
    unitType: 'money',
    defaultDailyValue: 5, // $5 USD aprox. al día
    unitLabel: 'costo diario promedio ($)',
    impactLabel: 'Dinero ahorrado en nicotina',
    emergencyTips: [
      'Toma un vaso grande de agua con hielo usando un sorbete para calmar la fijación oral.',
      'Mastica un chicle de menta fuerte o come una fruta cítrica (mandarina/manzana).',
      'Haz la respiración guiada 4-7-8: la ansiedad física dura menos de 5 minutos.',
      'Sal de la habitación o balcón donde sueles vapear.',
      'Lávate la cara con agua fría y haz 15 flexiones de pecho.'
    ],
    shortTermGoals: [
      { id: 'vp_s1', title: 'Retirar vapes y cargadores de la vista', desc: 'Guardar cualquier dispositivo fuera del escritorio y de la mesa de noche.', period: 'Días 1 a 2', completed: false },
      { id: 'vp_s2', title: 'La regla de esperar 10 minutos', desc: 'Ante un impulso urgente, esperar 10 minutos antes de cualquier decisión.', period: 'Días 3 a 4', completed: false },
      { id: 'vp_s3', title: 'Superar el pico físico de 72 horas', desc: 'Los primeros 3 días son los más intensos; el cuerpo elimina la nicotina.', period: 'Días 5 a 7', completed: false }
    ],
    mediumTermGoals: [
      { id: 'vp_m1', title: 'Sustituto oral saludable siempre a mano', desc: 'Llevar agua helada, chicles sin azúcar o mentas en el bolsillo.', period: 'Semana 2', completed: false },
      { id: 'vp_m2', title: 'Manejo de disparadores sociales', desc: 'Aprender a decir con orgullo: "No gracias, ya no fumo/vapeo" en fiestas.', period: 'Semana 3', completed: false },
      { id: 'vp_m3', title: 'Primer mes de aire limpio y capacidad pulmonar', desc: 'Notar la mejora en resistencia respiratoria y ritmo cardíaco.', period: 'Semana 4', completed: false }
    ],
    longTermGoals: [
      { id: 'vp_l1', title: 'Entrenamiento cardiovascular sin fatiga', desc: 'Realizar ejercicio de alta intensidad sin ahogo ni pesadez.', period: 'Mes 1', completed: false },
      { id: 'vp_l2', title: 'Fondo de ahorro acumulado', desc: 'Comprobar el dinero ahorrado y destinarlo a un viaje o meta importante.', period: 'Mes 2', completed: false },
      { id: 'vp_l3', title: 'Libertad absoluta de la nicotina', desc: 'Enfrentar situaciones de alto estrés sin recurrir a ninguna sustancia.', period: 'Mes 3+', completed: false }
    ],
    dailyReplacements: [
      { id: 'rep_vp_1', title: 'Hidratación con agua fría ante antojos', icon: '💧' },
      { id: 'rep_vp_2', title: '30 minutos de ejercicio o cardio activo', icon: '🏃‍♂️' },
      { id: 'rep_vp_3', title: 'Pausa de respiración profunda consciente', icon: '🧘' }
    ]
  },

  gaming: {
    id: 'gaming',
    name: 'Videojuegos en Exceso y Gaming Compulsivo',
    tagline: 'Frena partidas interminables, juegos competitivos tóxicos y desvelos',
    icon: '🎮',
    accentColor: '#8b5cf6',
    unitType: 'hours',
    defaultDailyValue: 4,
    unitLabel: 'horas al día',
    impactLabel: 'Horas libres productivas recuperadas',
    emergencyTips: [
      'Apaga la consola o PC y ponte de pie inmediatamente.',
      'Sal a tomar aire fresco o camina una vuelta a la manzana.',
      'Ponte música relajante o escucha un podcast interesante.',
      'Estira los brazos, espalda y cuello durante 5 minutos.',
      'Anota en una hoja qué meta de tu vida real resolverás hoy.'
    ],
    shortTermGoals: [
      { id: 'gm_s1', title: 'Apagado nocturno a las 11:00 PM', desc: 'Prohibido jugar pasada esa hora para recuperar el sueño reparador.', period: 'Días 1 a 3', completed: false },
      { id: 'gm_s2', title: 'Prioridad primero: Estudio o Trabajo', desc: 'Cero minutos de juego hasta haber cumplido tus deberes principales.', period: 'Días 3 a 5', completed: false },
      { id: 'gm_s3', title: 'Desinstalar juegos con mecánicas de adicción', desc: 'Borrar juegos de pases de batalla o microtransacciones diseñados para enganchar.', period: 'Días 5 a 7', completed: false }
    ],
    mediumTermGoals: [
      { id: 'gm_m1', title: 'Días laborales sin partidas competitivas', desc: 'Solo jugar títulos casuales o dejar los videojuegos para el fin de semana.', period: 'Semana 2', completed: false },
      { id: 'gm_m2', title: 'Límite fijo de 60 minutos con alarma física', desc: 'Poner alarma y apagar la consola en cuanto suene sin aplazar.', period: 'Semana 3', completed: false },
      { id: 'gm_m3', title: 'Planes fuera de casa con personas reales', desc: 'Hacer deporte, salir con amigos o practicar actividades al aire libre.', period: 'Semana 4', completed: false }
    ],
    longTermGoals: [
      { id: 'gm_l1', title: 'Desarrollar habilidades del mundo real', desc: 'Invertir tu energía competitiva en aprender a programar, tocar música o idiomas.', period: 'Mes 1', completed: false },
      { id: 'gm_l2', title: 'El juego como recreación ocasional, no escape', desc: 'Jugar ocasionalmente sin descuidar tu vida real ni tu salud.', period: 'Mes 2', completed: false },
      { id: 'gm_l3', title: 'Control absoluto de tu tiempo y enfoque', desc: 'Canalizar tu ambición hacia logros tangibles en tu carrera y vida.', period: 'Mes 3+', completed: false }
    ],
    dailyReplacements: [
      { id: 'rep_gm_1', title: 'Cumplir deberes/estudio antes de ocio', icon: '📚' },
      { id: 'rep_gm_2', title: 'Sesión de ejercicio físico o pesas', icon: '💪' },
      { id: 'rep_gm_3', title: 'Desconexión total antes de las 11 PM', icon: '🌙' }
    ]
  },

  junk_food: {
    id: 'junk_food',
    name: 'Comida Chatarra, Azúcar y Atracones',
    tagline: 'Frena los ultraprocesados, gaseosas, atracones nocturnos y antojos por estrés',
    icon: '🍔',
    accentColor: '#f59e0b',
    unitType: 'money',
    defaultDailyValue: 6,
    unitLabel: 'costo diario promedio ($)',
    impactLabel: 'Dinero ahorrado y salud digestiva',
    emergencyTips: [
      'Bebe un vaso grande de agua fría con limón antes de abrir el refrigerador.',
      'Espera 15 minutos: el 90% de los antojos son aburrimiento o ansiedad, no hambre física.',
      'Come una fruta fresca o un puñado de frutos secos si el hambre persiste.',
      'Cepíllate los dientes inmediatamente: el sabor a menta corta el deseo de dulce.',
      'Sal de la cocina y ocupa tus manos en otra actividad.'
    ],
    shortTermGoals: [
      { id: 'jf_s1', title: 'Eliminar refrescos y bebidas azucaradas', desc: 'Sustituir por agua natural, agua con gas o infusiones sin azúcar.', period: 'Días 1 a 3', completed: false },
      { id: 'jf_s2', title: 'Limpiar la despensa de paquetes gigantes', desc: 'No tener galletas, frituras o golosinas en tu habitación.', period: 'Días 3 a 5', completed: false },
      { id: 'jf_s3', title: 'Cero pedidos de comida rápida por delivery', desc: 'Preparar alimentos sencillos en casa en lugar de pedir por apps.', period: 'Días 5 a 7', completed: false }
    ],
    mediumTermGoals: [
      { id: 'jf_m1', title: 'Comer sin pantallas enfrente', desc: 'Alimentación consciente para registrar la saciedad real de tu cuerpo.', period: 'Semana 2', completed: false },
      { id: 'jf_m2', title: 'Regla 80/20 y fruta fresca a diario', desc: '80% comida natural y real, permitiendo flexibilidad ocasional.', period: 'Semana 3', completed: false },
      { id: 'jf_m3', title: 'Estabilidad de energía diurna', desc: 'Comprobar la desaparición de los bajones de energía tras comer.', period: 'Semana 4', completed: false }
    ],
    longTermGoals: [
      { id: 'jf_l1', title: 'Regeneración del paladar al dulce', desc: 'La comida natural y la fruta saben dulces y satisfactorias de forma natural.', period: 'Mes 1', completed: false },
      { id: 'jf_l2', title: 'Composición corporal y ligereza digestiva', desc: 'Mejor digestión, ligereza y rendimiento físico sobresaliente.', period: 'Mes 2', completed: false },
      { id: 'jf_l3', title: 'Relación sana y libre con la comida', desc: 'Comer para nutrir y disfrutar sin culpa ni compulsión.', period: 'Mes 3+', completed: false }
    ],
    dailyReplacements: [
      { id: 'rep_jf_1', title: '8 vasos de agua natural al día', icon: '💧' },
      { id: 'rep_jf_2', title: 'Comer al menos 2 frutas frescas', icon: '🍎' },
      { id: 'rep_jf_3', title: 'Caminar 15 minutos después de comer', icon: '🚶' }
    ]
  },

  digital_habits: {
    id: 'digital_habits',
    name: 'Pornografía y Dopamina Digital',
    tagline: 'Reset mental (NoFap/Reboot), rompe la hiperestimulación y recupera tu autoestima',
    icon: '🧠',
    accentColor: '#ec4899',
    unitType: 'hours',
    defaultDailyValue: 2,
    unitLabel: 'horas al día',
    impactLabel: 'Claridad mental y autoestima recuperadas',
    emergencyTips: [
      '¡Aléjate de la pantalla ya! Deja el celular o laptop en otra habitación.',
      'Date una ducha con agua fría durante 1 o 2 minutos para cortar el impulso.',
      'Haz 20 flexiones de pecho o sentadillas para redirigir la energía física.',
      'Recuerda: la tentación es una ola que baja si no la alimentas durante 10 minutos.',
      'Llama a un amigo, familiar o ponte a hablar con alguien cercano.'
    ],
    shortTermGoals: [
      { id: 'dh_s1', title: 'Cero pantallas en la cama al despertar y dormir', desc: 'El móvil se queda lejos para evitar disparadores en soledad.', period: 'Días 1 a 3', completed: false },
      { id: 'dh_s2', title: 'Activar bloqueadores de contenido y DNS seguro', desc: 'Crear fricción positiva para detener la navegación impulsiva.', period: 'Días 3 a 5', completed: false },
      { id: 'dh_s3', title: 'Superar la primera semana de desintoxicación', desc: 'Reconocer el aburrimiento como una trampa mental para buscar dopamina.', period: 'Días 5 a 7', completed: false }
    ],
    mediumTermGoals: [
      { id: 'dh_m1', title: 'Canalizar energía en ejercicio físico de fuerza', desc: 'Transformar los impulsos en entrenamientos intensos.', period: 'Semana 2', completed: false },
      { id: 'dh_m2', title: 'Comprender la neurobiología del estímulo', desc: 'Entender cómo se recuperan los receptores cerebrales de dopamina.', period: 'Semana 3', completed: false },
      { id: 'dh_m3', title: 'Retorno de la motivación y contacto visual firme', desc: 'Mayor confianza y serenidad en interacciones sociales cotidianas.', period: 'Semana 4', completed: false }
    ],
    longTermGoals: [
      { id: 'dh_l1', title: 'Un mes de victoria mental (30 días)', desc: 'Claridad cognitiva superior, desaparición de la niebla mental.', period: 'Mes 1', completed: false },
      { id: 'dh_l2', title: 'Integridad y coherencia personal', desc: 'Sentirse alineado con los propios principios y metas de vida.', period: 'Mes 2', completed: false },
      { id: 'dh_l3', title: 'Reinicio neurológico completo (90 días)', desc: 'Cerebro reequilibrado para disfrutar de metas y conexiones reales.', period: 'Mes 3+', completed: false }
    ],
    dailyReplacements: [
      { id: 'rep_dh_1', title: 'Rutina de ejercicio físico intenso o pesas', icon: '🏋️' },
      { id: 'rep_dh_2', title: '10 minutos de respiración o meditación', icon: '🧘' },
      { id: 'rep_dh_3', title: 'Dormir con celular fuera de la habitación', icon: '🌙' }
    ]
  }
};

// Medallas de Hitos de Libertad
const RECOVERY_BADGES = [
  { id: 'b_24h', name: 'Primeras 24 Horas', icon: '🌱', thresholdHours: 24, desc: 'El paso más valiente: tu primer día completo de libertad.' },
  { id: 'b_3d', name: '72 Horas Fuertes', icon: '🛡️', thresholdHours: 72, desc: 'Has superado el pico físico más difícil de la abstinencia.' },
  { id: 'b_7d', name: '1 Semana Libre', icon: '⚔️', thresholdHours: 168, desc: '7 días de constancia. El hábito destructivo pierde poder.' },
  { id: 'b_14d', name: '2 Semanas Imparable', icon: '🔥', thresholdHours: 336, desc: 'Nuevos caminos neuronales saludables en formación.' },
  { id: 'b_21d', name: '21 Días de Rutina', icon: '⚡', thresholdHours: 504, desc: 'Hito clásico de reconfiguración de hábitos personales.' },
  { id: 'b_30d', name: '1 Mes de Oro', icon: '🏆', thresholdHours: 720, desc: 'Un mes entero de renacimiento y control de tu vida.' },
  { id: 'b_60d', name: '60 Días de Acero', icon: '💎', thresholdHours: 1440, desc: 'Fortaleza mental demostrada en múltiples pruebas cotidianas.' },
  { id: 'b_90d', name: 'Reinicio Total (90 Días)', icon: '👑', thresholdHours: 2160, desc: 'Maestro de tu propia mente y libre de la dependencia.' }
];

const Recovery = {
  getCatalog() {
    return ADDICTION_DATABASE;
  },

  getBadges() {
    return RECOVERY_BADGES;
  },

  // Obtener plan de recuperación activo
  getActivePlan() {
    const data = Storage.get();
    return data.currentPlan || null;
  },

  // Crear o cambiar plan
  createPlan({ addictionId, whyReason, dailyCostOrHours }) {
    const data = Storage.get();
    const template = ADDICTION_DATABASE[addictionId] || ADDICTION_DATABASE.social_media;
    const nowIso = new Date().toISOString();

    const plan = {
      addictionId: template.id,
      title: `Plan para superar: ${template.name}`,
      icon: template.icon,
      accentColor: template.accentColor,
      startDate: nowIso,
      lastResetDate: nowIso,
      whyReason: whyReason.trim() || 'Deseo recuperar el control de mi vida, mi salud y mi tiempo.',
      dailyValue: Math.max(1, Number(dailyCostOrHours) || template.defaultDailyValue),
      unitType: template.unitType,
      unitLabel: template.unitLabel,
      impactLabel: template.impactLabel,
      shortTermGoals: JSON.parse(JSON.stringify(template.shortTermGoals)),
      mediumTermGoals: JSON.parse(JSON.stringify(template.mediumTermGoals)),
      longTermGoals: JSON.parse(JSON.stringify(template.longTermGoals)),
      dailyReplacements: JSON.parse(JSON.stringify(template.dailyReplacements))
    };

    data.currentPlan = plan;
    Storage.save(data);
    return plan;
  },

  // Calcular tiempo limpio
  getCleanTime() {
    const plan = this.getActivePlan();
    if (!plan) return null;

    const start = new Date(plan.lastResetDate || plan.startDate);
    const now = new Date();
    const diffMs = Math.max(0, now - start);

    const totalSeconds = Math.floor(diffMs / 1000);
    const totalMinutes = Math.floor(totalSeconds / 60);
    const totalHours = Math.floor(totalMinutes / 60);
    const days = Math.floor(totalHours / 24);
    const hours = totalHours % 24;
    const minutes = totalMinutes % 60;
    const seconds = totalSeconds % 60;

    const daysFraction = diffMs / (1000 * 60 * 60 * 24);
    const savedAmount = Math.round((daysFraction * plan.dailyValue) * 10) / 10;

    // Calcular insignias desbloqueadas
    const unlocked = [];
    RECOVERY_BADGES.forEach(b => {
      if (totalHours >= b.thresholdHours) {
        unlocked.push(b.id);
      }
    });

    const data = Storage.get();
    if (JSON.stringify(data.unlockedBadges || []) !== JSON.stringify(unlocked)) {
      data.unlockedBadges = unlocked;
      Storage.save(data);
    }

    return {
      days,
      hours,
      minutes,
      seconds,
      totalHours,
      totalDays: Math.floor(daysFraction),
      savedAmount,
      unitType: plan.unitType,
      impactLabel: plan.impactLabel,
      unlockedBadges: unlocked
    };
  },

  // Alternar estado de una meta del plan
  toggleGoal(phase, goalId) {
    const data = Storage.get();
    if (!data.currentPlan) return null;

    const listKey = phase === 'short' 
      ? 'shortTermGoals' 
      : phase === 'medium' 
      ? 'mediumTermGoals' 
      : 'longTermGoals';

    const list = data.currentPlan[listKey];
    if (!list) return null;

    const item = list.find(g => g.id === goalId);
    if (item) {
      item.completed = !item.completed;
      if (item.completed) {
        data.xp = (data.xp || 0) + 35; // +35 XP por meta completada
      }
      Storage.save(data);
    }
    return item;
  },

  // Registrar que se resistió una tentación
  recordCravingResisted(trigger = '', toolUsed = '', notes = '') {
    const data = Storage.get();
    const todayStr = formatDate(new Date());

    if (!data.dailyLogs) data.dailyLogs = {};
    if (!data.dailyLogs[todayStr]) {
      data.dailyLogs[todayStr] = {
        clean: true,
        cravingLevel: 'medium',
        cravingsResisted: 0,
        notes: '',
        replacements: {}
      };
    }

    data.dailyLogs[todayStr].cravingsResisted = (data.dailyLogs[todayStr].cravingsResisted || 0) + 1;
    data.xp = (data.xp || 0) + 25; // +25 XP por vencer un antojo

    // Guardar en historial de eventos
    if (!data.eventsLog) data.eventsLog = [];
    data.eventsLog.unshift({
      id: 'ev_' + Date.now().toString(36),
      type: 'craving_resisted',
      date: new Date().toISOString(),
      trigger: trigger.trim() || 'Tentación repentina',
      toolUsed: toolUsed.trim() || 'Respiración 4-7-8 y cambio de foco',
      notes: notes.trim()
    });

    Storage.save(data);
    return data.dailyLogs[todayStr].cravingsResisted;
  },

  // Registrar una recaída con actitud constructiva
  recordRelapse(reason = '', reflection = '') {
    const data = Storage.get();
    const now = new Date();
    const todayStr = formatDate(now);
    const cleanTime = this.getCleanTime();

    if (!data.dailyLogs) data.dailyLogs = {};
    if (!data.dailyLogs[todayStr]) {
      data.dailyLogs[todayStr] = {
        clean: false,
        cravingLevel: 'high',
        cravingsResisted: 0,
        notes: '',
        replacements: {}
      };
    }
    data.dailyLogs[todayStr].clean = false;

    // Reiniciar fecha de sobriedad
    if (data.currentPlan) {
      data.currentPlan.lastResetDate = now.toISOString();
    }

    // Registrar en eventos
    if (!data.eventsLog) data.eventsLog = [];
    data.eventsLog.unshift({
      id: 'ev_' + Date.now().toString(36),
      type: 'relapse',
      date: now.toISOString(),
      cleanDaysAchieved: cleanTime ? cleanTime.days : 0,
      reason: reason.trim() || 'Disparador no gestionado a tiempo',
      reflection: reflection.trim() || 'Identificar el detonante y volver con mayor determinación.'
    });

    Storage.save(data);
    return true;
  },

  // Alternar acción de reemplazo para el día de hoy
  toggleDailyReplacement(repId, dateStr = null) {
    const data = Storage.get();
    const dStr = dateStr || formatDate(new Date());

    if (!data.dailyLogs) data.dailyLogs = {};
    if (!data.dailyLogs[dStr]) {
      data.dailyLogs[dStr] = {
        clean: true,
        cravingLevel: 'none',
        cravingsResisted: 0,
        notes: '',
        replacements: {}
      };
    }

    if (!data.dailyLogs[dStr].replacements) {
      data.dailyLogs[dStr].replacements = {};
    }

    const currentStatus = Boolean(data.dailyLogs[dStr].replacements[repId]);
    data.dailyLogs[dStr].replacements[repId] = !currentStatus;

    if (!currentStatus) {
      data.xp = (data.xp || 0) + 15; // +15 XP por hábito de reemplazo
    }

    Storage.save(data);
    return !currentStatus;
  },

  // Obtener tips del modo de emergencia
  getEmergencyTips() {
    const plan = this.getActivePlan();
    if (!plan) return ADDICTION_DATABASE.social_media.emergencyTips;
    const cat = ADDICTION_DATABASE[plan.addictionId];
    return cat ? cat.emergencyTips : ADDICTION_DATABASE.social_media.emergencyTips;
  }
};
