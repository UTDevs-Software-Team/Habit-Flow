/**
 * HABITFLOW - storage.js
 * Capa de persistencia en LocalStorage para garantizar que todo el progreso,
 * entradas del usuario, selecciones y estado de la planta virtual se conserven
 * fielmente al recargar la página.
 */

const HabitStorage = (() => {
  const STORAGE_KEY = 'habitflow_app_state_v2';

  const defaultState = {
    version: '2.0',
    onboardingStep: 'welcome', // 'welcome' | 'age' | 'habit' | 'dashboard'
    user: {
      name: '',
      ageRange: null // '13-15' | '16-18' | '19-20'
    },
    habit: {
      id: null, // e.g. 'procrastinacion'
      startedAt: null,
      currentDay: 1, // Día actual del reto (1 a 7). Controla qué días son "futuros"
      viewingDay: 1, // Día seleccionado en el calendario para consultar
      completedDays: [], // Array de números [1, 2, ...] de días marcados como completados
      maxProgressPercentage: 0, // Regla oficial: la planta NO pierde su crecimiento alcanzado
      dayInputs: {}, // Respuestas interactivas por día { 1: { ... }, 2: { ... } }
      reflection: '', // Pregunta de reflexión del día 7
      completedAt: null
    },
    history: [] // Historial de métodos completados anteriormente
  };

  /**
   * Carga el estado guardado o inicializa uno nuevo
   */
  function load() {
    try {
      const serialized = localStorage.getItem(STORAGE_KEY);
      if (!serialized) {
        return JSON.parse(JSON.stringify(defaultState));
      }
      const parsed = JSON.parse(serialized);
      // Fusión con defaultState para garantizar campos nuevos
      return {
        ...defaultState,
        ...parsed,
        user: { ...defaultState.user, ...(parsed.user || {}) },
        habit: { ...defaultState.habit, ...(parsed.habit || {}) }
      };
    } catch (e) {
      console.error('Error cargando HabitFlow desde LocalStorage:', e);
      return JSON.parse(JSON.stringify(defaultState));
    }
  }

  /**
   * Guarda el estado actual en LocalStorage
   */
  function save(state) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
      return true;
    } catch (e) {
      console.error('Error guardando en LocalStorage:', e);
      return false;
    }
  }

  /**
   * Actualiza el rango de edad
   */
  function setAgeRange(ageId, name = '') {
    const state = load();
    state.user.ageRange = ageId;
    if (name) state.user.name = name.trim();
    state.onboardingStep = 'habit';
    save(state);
    return state;
  }

  /**
   * Selecciona el hábito y activa el método de 7 días
   */
  function selectHabit(habitId) {
    const state = load();
    state.habit.id = habitId;
    state.habit.startedAt = new Date().toISOString();
    state.habit.currentDay = 1;
    state.habit.viewingDay = 1;
    state.habit.completedDays = [];
    state.habit.maxProgressPercentage = 0;
    state.habit.dayInputs = {};
    state.habit.reflection = '';
    state.habit.completedAt = null;
    state.onboardingStep = 'dashboard';
    save(state);
    return state;
  }

  /**
   * Marca o desmarca un día como completado respetando las reglas:
   * - Solo se puede marcar si dayNum <= currentDay (no se pueden marcar días futuros)
   * - Actualiza el porcentaje y el crecimiento máximo acumulado de la planta
   */
  function toggleDayCompletion(dayNum) {
    const state = load();
    const day = parseInt(dayNum, 10);

    // Regla de funcionamiento oficial (Sección 6):
    // "El usuario podrá consultar los días anteriores, pero no deberá poder marcar actividades futuras como completadas antes de realizarlas."
    if (day > state.habit.currentDay) {
      return {
        success: false,
        reason: 'future_day_locked',
        message: 'No puedes marcar actividades de días futuros antes de alcanzarlos.',
        state
      };
    }

    const completed = new Set(state.habit.completedDays || []);
    let isNowCompleted = false;

    if (completed.has(day)) {
      completed.delete(day);
      isNowCompleted = false;
    } else {
      completed.add(day);
      isNowCompleted = true;
    }

    state.habit.completedDays = Array.from(completed).sort((a, b) => a - b);

    // Fórmula oficial: Porcentaje de progreso = (Actividades completadas / 7) * 100
    const currentPercent = (state.habit.completedDays.length / 7) * 100;
    
    // Regla de la planta: La planta no perderá su crecimiento si el usuario deja una actividad pendiente
    if (currentPercent > (state.habit.maxProgressPercentage || 0)) {
      state.habit.maxProgressPercentage = currentPercent;
    }

    save(state);
    return {
      success: true,
      isNowCompleted,
      state
    };
  }

  /**
   * Cambia el día activo que se está consultando en el checklist
   */
  function setViewingDay(dayNum) {
    const state = load();
    const day = Math.max(1, Math.min(7, parseInt(dayNum, 10)));
    state.habit.viewingDay = day;
    save(state);
    return state;
  }

  /**
   * Avanza o simula el día actual del método (1 a 7)
   * Útil para avanzar día a día y evaluar el cumplimiento
   */
  function setCurrentDay(dayNum) {
    const state = load();
    const day = Math.max(1, Math.min(7, parseInt(dayNum, 10)));
    state.habit.currentDay = day;
    state.habit.viewingDay = day;
    save(state);
    return state;
  }

  /**
   * Guarda una respuesta interactiva para un día específico
   */
  function saveDayInput(dayNum, key, value) {
    const state = load();
    const day = parseInt(dayNum, 10);
    if (!state.habit.dayInputs[day]) {
      state.habit.dayInputs[day] = {};
    }
    state.habit.dayInputs[day][key] = value;
    save(state);
    return state;
  }

  /**
   * Guarda la reflexión final
   */
  function saveReflection(reflectionText) {
    const state = load();
    state.habit.reflection = reflectionText;
    save(state);
    return state;
  }

  /**
   * Reinicia el método actual para repetirlo durante otra semana
   */
  function repeatCurrentHabit() {
    const state = load();
    // Guardar en historial
    if (state.habit.id) {
      state.history.push({
        habitId: state.habit.id,
        completedDays: state.habit.completedDays,
        reflection: state.habit.reflection,
        finishedAt: new Date().toISOString()
      });
    }
    state.habit.startedAt = new Date().toISOString();
    state.habit.currentDay = 1;
    state.habit.viewingDay = 1;
    state.habit.completedDays = [];
    state.habit.maxProgressPercentage = 0;
    state.habit.dayInputs = {};
    state.habit.reflection = '';
    state.habit.completedAt = null;
    state.onboardingStep = 'dashboard';
    save(state);
    return state;
  }

  /**
   * Permite elegir un nuevo hábito manteniendo el perfil de edad
   */
  function chooseNewHabit() {
    const state = load();
    if (state.habit.id) {
      state.history.push({
        habitId: state.habit.id,
        completedDays: state.habit.completedDays,
        reflection: state.habit.reflection,
        finishedAt: new Date().toISOString()
      });
    }
    state.habit = { ...defaultState.habit };
    state.onboardingStep = 'habit';
    save(state);
    return state;
  }

  /**
   * Reinicia completamente la aplicación (vuelve a la bienvenida)
   */
  function resetAll() {
    localStorage.removeItem(STORAGE_KEY);
    return JSON.parse(JSON.stringify(defaultState));
  }

  return {
    load,
    save,
    setAgeRange,
    selectHabit,
    toggleDayCompletion,
    setViewingDay,
    setCurrentDay,
    saveDayInput,
    saveReflection,
    repeatCurrentHabit,
    chooseNewHabit,
    resetAll
  };
})();

// Exportar en window
if (typeof window !== 'undefined') {
  window.HabitStorage = HabitStorage;
}
