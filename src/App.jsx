/**
 * ============================================================================
 * ORQUESTADOR PRINCIPAL BLACK BOX: `HabitFlowApp`
 * ============================================================================
 * Administra estado global (`localStorage: habitflow_21days_v2`), tema oscuro
 * (`habitflow_dark_mode`), catalogo CSV y enrutamiento entre las 3 vistas.
 */
const { useState, useEffect, useRef, useMemo } = React;

const HabitFlowApp = () => {
  const STORAGE_KEY = 'habitflow_21days_v2';

  const getInitialState = () => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY) || localStorage.getItem('habitflow_21days_v1');
      if (saved) {
        const parsed = JSON.parse(saved);
        return {
          activeHabit: parsed.activeHabit || 'Vencer la procrastinación e iniciar sesiones de estudio efectivas',
          habitMagnitude: parsed.habitMagnitude || 'mediano', // 'chico' | 'mediano' | 'grande'
          completedDays: parsed.completedDays || [],
          currentViewingDay: parsed.currentViewingDay || 1,
          currentView: parsed.currentView || 'dashboard',
          dayData: parsed.dayData || {},
          metrics: parsed.metrics || {
            focusMinutes: 0,
            sessionsStarted: 0,
            sessionsCompleted: 0,
            tasksRegisteredCount: 0,
            stepsCompletedCount: 0,
            punctualityLogs: []
          },
          featuredNextStep: parsed.featuredNextStep || null,
          personalPlan: parsed.personalPlan || null
        };
      }
    } catch (e) {
      console.error('Error loading localStorage state:', e);
    }

    return {
      activeHabit: 'Vencer la procrastinación e iniciar sesiones de estudio efectivas',
      habitMagnitude: 'mediano',
      completedDays: [],
      currentViewingDay: 1,
      currentView: 'dashboard',
      dayData: {},
      metrics: {
        focusMinutes: 0,
        sessionsStarted: 0,
        sessionsCompleted: 0,
        tasksRegisteredCount: 0,
        stepsCompletedCount: 0,
        punctualityLogs: []
      },
      featuredNextStep: null,
      personalPlan: null
    };
  };

  const [appState, setAppState] = useState(getInitialState);
  const [darkMode, setDarkMode] = useState(() => {
    try { return localStorage.getItem('habitflow_dark_mode') === 'true'; } catch (e) { return false; }
  });
  const [csvCatalog, setCsvCatalog] = useState(() => parseMicroPasosCsv(EMBEDDED_MICRO_PASOS_CSV));
  const [toastMessage, setToastMessage] = useState(null);
  const [celebrationModal, setCelebrationModal] = useState(null);
  const [habitConfigModalOpen, setHabitConfigModalOpen] = useState(false);

  // Temporary edit state for config modal
  const [editHabitName, setEditHabitName] = useState(appState.activeHabit);
  const [editMagnitude, setEditMagnitude] = useState(appState.habitMagnitude);

  useEffect(() => {
    document.documentElement.classList.toggle('dark', darkMode);
    try { localStorage.setItem('habitflow_dark_mode', String(darkMode)); } catch (e) {}
  }, [darkMode]);

  // Persist state to localStorage
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(appState));
    } catch (e) {
      console.error('Failed to save to localStorage:', e);
    }
  }, [appState]);

  const showToast = (msg, type = 'info') => {
    setToastMessage({ msg, type });
    setTimeout(() => setToastMessage(null), 3500);
  };

  const fireConfetti = () => {
    if (typeof confetti === 'function') {
      confetti({
        particleCount: 80,
        spread: 70,
        origin: { y: 0.6 }
      });
    }
  };

  const completedDaysCount = appState.completedDays.length;
  const globalPercentage = Math.round((completedDaysCount / 21) * 100);
  const plantPhase = getPlantPhase(completedDaysCount);
  const currentMagnitudeConfig = HABIT_MAGNITUDES[appState.habitMagnitude] || HABIT_MAGNITUDES.mediano;

  const isDayLocked = (dayNum) => {
    if (dayNum === 1) return false;
    return !appState.completedDays.includes(dayNum - 1);
  };

  const isDayCompleted = (dayNum) => {
    return appState.completedDays.includes(dayNum);
  };

  const navigateTo = (view, dayNumber = null) => {
    if (dayNumber !== null) {
      if (isDayLocked(dayNumber)) {
        showToast(`El Día ${dayNumber} está bloqueado. Completa el Día ${dayNumber - 1} al 100% primero.`, 'warning');
        return;
      }
      setAppState(prev => ({
        ...prev,
        currentViewingDay: dayNumber,
        currentView: 'activity'
      }));
    } else {
      setAppState(prev => ({
        ...prev,
        currentView: view
      }));
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleUpdateDayField = (dayNum, fieldKey, value) => {
    setAppState(prev => {
      const currentDayObj = prev.dayData[dayNum] || {};
      return {
        ...prev,
        dayData: {
          ...prev.dayData,
          [dayNum]: {
            ...currentDayObj,
            [fieldKey]: value
          }
        }
      };
    });
  };

  // Change habit magnitude & name
  const handleSaveHabitConfig = (newName, newMag) => {
    setAppState(prev => ({
      ...prev,
      activeHabit: newName.trim() || prev.activeHabit,
      habitMagnitude: newMag
    }));
    setHabitConfigModalOpen(false);
    showToast(`Calendario condicionado a magnitud: ${HABIT_MAGNITUDES[newMag].shortName}`, 'success');
  };

  // Calculate Day Completion Percentage
  const calculateDayCompletion = (dayNum) => {
    const config = getDayConfig(dayNum, appState.habitMagnitude);
    const data = appState.dayData[dayNum] || {};
    let doneActions = 0;

    switch (dayNum) {
      case 1:
        if (data.taskName && data.taskName.trim().length > 1) doneActions++;
        if (data.category) doneActions++;
        if (data.firstAction && data.firstAction.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 2:
        if (data.step1 && data.step1.trim().length > 1) doneActions++;
        if (data.step2 && data.step2.trim().length > 1) doneActions++;
        if (data.step3 && data.step3.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 3:
        if (data.timerCompleted) doneActions++;
        if (data.step1Executed === 'yes' || data.step1Executed === 'no') doneActions++;
        if (data.reflection && data.reflection.trim().length > 2) doneActions++;
        break;
      case 4:
        if (data.cleanSpace) doneActions++;
        if (data.readyMaterials) doneActions++;
        if (data.devicePrepared) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 5:
        if (data.mainDistraction && data.mainDistraction.trim().length > 1) doneActions++;
        if (data.mitigationRule && data.mitigationRule.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 6:
        if (data.pending1 && data.pending2 && data.pending3) doneActions++;
        if (data.chosenPriority) doneActions++;
        if (data.firstStepPriority && data.firstStepPriority.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 7:
        if (data.week1Rating) doneActions++;
        if (data.biggestBreakthrough && data.biggestBreakthrough.trim().length > 2) doneActions++;
        if (data.summaryGenerated) doneActions++;
        break;
      case 8:
        if (data.scheduledTime) doneActions++;
        if (data.punctualityRecorded) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 9:
        if (data.nextStep && data.nextStep.trim().length > 1) doneActions++;
        if (data.requiredMaterials && data.requiredMaterials.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 10:
        if (data.focusGoal && data.focusGoal.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        if (data.progressReport && data.progressReport.trim().length > 2) doneActions++;
        break;
      case 11:
        if (data.selectedPendingStep && data.selectedPendingStep.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        if (data.stepStatus) doneActions++;
        break;
      case 12:
        if (data.leisureActivity && data.leisureActivity.trim().length > 1) doneActions++;
        if (data.linkedTask && data.linkedTask.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        if (data.premackConfirmed) doneActions++;
        break;
      case 13:
        if (data.timerCompleted) doneActions++;
        if (data.sessionFeelings) doneActions++;
        break;
      case 14:
        if (Array.isArray(data.topStrategies) && data.topStrategies.length === 2) doneActions++;
        if (data.weekComparison) doneActions++;
        if (data.week2Reflection && data.week2Reflection.trim().length > 2) doneActions++;
        break;
      case 15:
        if (data.task1 && data.task2 && data.task3) doneActions++;
        if (data.actionForTask1 && data.actionForTask1.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        if (data.task1PrioritySet) doneActions++;
        break;
      case 16:
        if (data.avoidedTask && data.avoidedTask.trim().length > 1) doneActions++;
        if (data.microAction5m && data.microAction5m.trim().length > 1) doneActions++;
        if (data.timerCompleted) doneActions++;
        if (data.frictionBroken) doneActions++;
        break;
      case 17:
        if (data.tomorrowTask && data.tomorrowTask.trim().length > 1) doneActions++;
        if (data.tomorrowFirstStep && data.tomorrowFirstStep.trim().length > 1) doneActions++;
        if (data.tomorrowHour) doneActions++;
        if (data.checklistTomorrowReady) doneActions++;
        break;
      case 18:
        if (data.timerCompleted) doneActions++;
        if (data.dualBlockConfirmed) doneActions++;
        break;
      case 19:
        if (data.check1 && data.check2 && data.check3 && data.check4 && data.check5) doneActions++;
        if (data.timerCompleted) doneActions++;
        break;
      case 20:
        if (Array.isArray(data.finalStrategies) && data.finalStrategies.length === 2) doneActions++;
        if (data.weeklyFrequency) doneActions++;
        if (data.personalPlanGenerated) doneActions++;
        break;
      case 21:
        if (data.transformationRating) doneActions++;
        if (data.procrastinationBeforeAfter && data.procrastinationBeforeAfter.trim().length > 2) doneActions++;
        if (data.finalTakeaway && data.finalTakeaway.trim().length > 2) doneActions++;
        break;
      default:
        break;
    }

    const percentage = Math.round((doneActions / config.actionsCount) * 100);
    return Math.min(100, percentage);
  };

  const handleTimerFinished = (dayNum, minutesCount) => {
    handleUpdateDayField(dayNum, 'timerCompleted', true);
    playChime();

    setAppState(prev => ({
      ...prev,
      metrics: {
        ...prev.metrics,
        focusMinutes: (prev.metrics.focusMinutes || 0) + minutesCount,
        sessionsCompleted: (prev.metrics.sessionsCompleted || 0) + 1,
        sessionsStarted: Math.max((prev.metrics.sessionsStarted || 0), (prev.metrics.sessionsCompleted || 0) + 1)
      }
    }));

    showToast(`¡Temporizador de ${minutesCount} minutos completado con éxito!`, 'success');
  };

  const handleFinalizeDay = (dayNum) => {
    const pct = calculateDayCompletion(dayNum);
    if (pct < 100) {
      showToast('Debes completar todas las acciones del día antes de finalizarlo.', 'warning');
      return;
    }

    const isAlreadyCompleted = appState.completedDays.includes(dayNum);
    const dayData = appState.dayData[dayNum] || {};

    let updatedFeaturedStep = appState.featuredNextStep;
    if (dayNum === 9 && dayData.nextStep) {
      updatedFeaturedStep = {
        step: dayData.nextStep,
        materials: dayData.requiredMaterials || 'Materiales organizados',
        savedAt: new Date().toLocaleDateString('es-ES')
      };
    }

    let updatedPlan = appState.personalPlan;
    if (dayNum === 20) {
      updatedPlan = {
        strategies: dayData.finalStrategies || ['Micro-pasos de fricción cero', 'Acondicionamiento de espacio'],
        frequency: dayData.weeklyFrequency || '4 días a la semana',
        schedule: dayData.preferredSchedule || 'Horario fijo vespertino',
        generatedAt: new Date().toLocaleDateString('es-ES')
      };
    }

    let addedTasks = 0;
    let addedSteps = 0;
    if (dayNum === 1 && dayData.taskName) addedTasks = 1;
    if (dayNum === 2) addedSteps = 3;
    if (dayNum === 6) addedTasks = 3;
    if (dayNum === 15) addedTasks = 3;

    setAppState(prev => {
      const newCompleted = isAlreadyCompleted 
        ? prev.completedDays 
        : [...prev.completedDays, dayNum].sort((a, b) => a - b);

      return {
        ...prev,
        completedDays: newCompleted,
        featuredNextStep: updatedFeaturedStep,
        personalPlan: updatedPlan,
        metrics: {
          ...prev.metrics,
          tasksRegisteredCount: (prev.metrics.tasksRegisteredCount || 0) + addedTasks,
          stepsCompletedCount: (prev.metrics.stepsCompletedCount || 0) + addedSteps
        }
      };
    });

    fireConfetti();
    setCelebrationModal({
      dayNum,
      isFinal: dayNum === 21
    });
  };

  const handleResetAllProgress = () => {
    if (window.confirm('¿Estás seguro de que deseas reiniciar todo el programa de 21 días? Se restablecerán todos los días, métricas y registros guardados.')) {
      const fresh = {
        activeHabit: appState.activeHabit,
        habitMagnitude: appState.habitMagnitude,
        completedDays: [],
        currentViewingDay: 1,
        currentView: 'dashboard',
        dayData: {},
        metrics: {
          focusMinutes: 0,
          sessionsStarted: 0,
          sessionsCompleted: 0,
          tasksRegisteredCount: 0,
          stepsCompletedCount: 0,
          punctualityLogs: []
        },
        featuredNextStep: null,
        personalPlan: null
      };
      setAppState(fresh);
      localStorage.removeItem(STORAGE_KEY);
      showToast('Programa reiniciado exitosamente. ¡Comencemos el Día 1!', 'info');
    }
  };

  // Open Modal to configure habit
  const openConfigModal = () => {
    setEditHabitName(appState.activeHabit);
    setEditMagnitude(appState.habitMagnitude);
    setHabitConfigModalOpen(true);
  };

  return (
    <div className="flex-1 flex flex-col">
      {/* TOAST ALERT */}
      {toastMessage && (
        <div className="fixed bottom-5 right-5 z-50 flex items-center gap-2.5 px-4 py-3 rounded-xl shadow-lg border text-sm font-semibold animate-bounce bg-white border-slate-200 text-slate-800">
          <span className={
            toastMessage.type === 'success' ? 'text-growth-600' :
            toastMessage.type === 'warning' ? 'text-amber-500' : 'text-brand-600'
          }>
            {toastMessage.type === 'success' ? '✅' : toastMessage.type === 'warning' ? '⚠️' : 'ℹ️'}
          </span>
          <span>{toastMessage.msg}</span>
        </div>
      )}

      {/* CELEBRATION MODAL */}
      {celebrationModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 sm:p-8 shadow-2xl border border-slate-100 text-center animate-scale-up">
            <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-growth-100 text-growth-700 flex items-center justify-center text-3xl">
              {celebrationModal.isFinal ? '🏆' : '🌱'}
            </div>

            <h3 className="text-xl sm:text-2xl font-bold text-slate-900 mb-2">
              {celebrationModal.isFinal ? '¡Graduación del Programa Conquistada!' : `¡Día ${celebrationModal.dayNum} Completado!`}
            </h3>

            <p className="text-sm text-slate-600 mb-6 leading-relaxed">
              {celebrationModal.isFinal 
                ? 'Has completado los 21 días de formación y práctica sistemática. Tu planta es ahora un árbol maduro y tu método de estudio es sólido y autónomo.'
                : `Has cumplido al 100% con todas las acciones programadas para el Día ${celebrationModal.dayNum}. El Día ${celebrationModal.dayNum + 1} ha sido desbloqueado en tu calendario.`
              }
            </p>

            <div className="flex flex-col gap-2.5">
              {!celebrationModal.isFinal && (
                <button
                  type="button"
                  onClick={() => {
                    const next = celebrationModal.dayNum + 1;
                    setCelebrationModal(null);
                    navigateTo('activity', next);
                  }}
                  className="w-full py-3 px-4 rounded-xl text-sm font-semibold bg-growth-600 hover:bg-growth-700 text-white shadow-sm flex items-center justify-center gap-2"
                >
                  Continuar al Día {celebrationModal.dayNum + 1} <Icons.ArrowRight />
                </button>
              )}
              <button
                type="button"
                onClick={() => {
                  setCelebrationModal(null);
                  navigateTo('dashboard');
                }}
                className="w-full py-2.5 px-4 rounded-xl text-sm font-semibold bg-slate-100 hover:bg-slate-200 text-slate-700"
              >
                Volver al Dashboard
              </button>
            </div>
          </div>
        </div>
      )}

      {/* HABIT & MAGNITUDE CONFIGURATION MODAL */}
      {habitConfigModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-xs">
          <div className="bg-white rounded-3xl max-w-xl w-full p-6 sm:p-8 shadow-2xl border border-slate-100 animate-scale-up space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <div className="flex items-center gap-2.5">
                <span className="p-2 bg-brand-50 text-brand-600 rounded-xl">
                  <Icons.Settings />
                </span>
                <div>
                  <h3 className="text-lg font-bold text-slate-900">Personalizar Hábito y Calendario</h3>
                  <p className="text-xs text-slate-500">Condiciona los lapsos de tiempo según la complejidad real</p>
                </div>
              </div>
              <button 
                onClick={() => setHabitConfigModalOpen(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                ✕
              </button>
            </div>

            {/* Habit Name Input */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-1.5">
                Nombre del Hábito o Meta
              </label>
              <input
                type="text"
                value={editHabitName}
                onChange={(e) => setEditHabitName(e.target.value)}
                placeholder="Ej. Vencer la procrastinación al estudiar, Ordenar mi cuarto, Leer 1 capítulo diario"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500"
              />
              {/* Quick Suggestions */}
              <div className="flex flex-wrap gap-1.5 mt-2">
                <span className="text-[11px] text-slate-400 self-center">Ejemplos:</span>
                <button
                  type="button"
                  onClick={() => { setEditHabitName('Ordenar y limpiar mi cuarto'); setEditMagnitude('chico'); }}
                  className="text-xs px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700"
                >
                  🧹 Ordenar mi cuarto (Chico)
                </button>
                <button
                  type="button"
                  onClick={() => { setEditHabitName('Lectura diaria de libros'); setEditMagnitude('mediano'); }}
                  className="text-xs px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700"
                >
                  📖 Lectura diaria (Mediano)
                </button>
                <button
                  type="button"
                  onClick={() => { setEditHabitName('Estudiar cálculo y materias de carrera'); setEditMagnitude('grande'); }}
                  className="text-xs px-2.5 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-700"
                >
                  🧠 Estudiar materias (Grande)
                </button>
              </div>
            </div>

            {/* Magnitude Selector (Chico, Mediano, Grande) */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700 mb-2">
                Magnitud y Complejidad del Hábito (Condiciona los 21 días)
              </label>

              <div className="grid grid-cols-1 gap-2.5">
                {Object.values(HABIT_MAGNITUDES).map((mag) => {
                  const isSelected = editMagnitude === mag.id;
                  return (
                    <div
                      key={mag.id}
                      onClick={() => setEditMagnitude(mag.id)}
                      className={`p-3.5 rounded-2xl border-2 cursor-pointer transition-all flex items-start gap-3 ${
                        isSelected 
                          ? 'border-brand-500 bg-brand-50/40 shadow-xs' 
                          : 'border-slate-200 hover:border-slate-300 bg-white'
                      }`}
                    >
                      <div className="text-2xl mt-0.5">{mag.icon}</div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between">
                          <span className="font-bold text-sm text-slate-900">{mag.name}</span>
                          <span className={`text-[11px] font-bold px-2 py-0.5 rounded-full border ${mag.badgeBg}`}>
                            {mag.timeRangeLabel}
                          </span>
                        </div>
                        <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                          {mag.description}
                        </p>
                        <p className="text-[11px] text-slate-400 italic mt-1">
                          <strong>Ejemplos:</strong> {mag.examples}
                        </p>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
              <button
                type="button"
                onClick={() => setHabitConfigModalOpen(false)}
                className="px-4 py-2.5 rounded-xl text-xs font-semibold text-slate-600 hover:bg-slate-100"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={() => handleSaveHabitConfig(editHabitName, editMagnitude)}
                className="px-5 py-2.5 rounded-xl text-xs font-bold bg-brand-600 hover:bg-brand-700 text-white shadow-xs"
              >
                Guardar y Calibrar Calendario
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TOP BAR / NAVIGATION */}
      <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/80 shadow-2xs">
        <div className="max-w-7xl mx-auto px-3 sm:px-6 lg:px-8">
          <div className="flex flex-wrap sm:flex-nowrap items-center justify-between gap-2 py-2.5 sm:py-0 sm:h-20">
            {/* Brand & Habit Title */}
            <div className="flex items-center gap-3">
              <div 
                onClick={() => navigateTo('dashboard')}
                className="cursor-pointer flex items-center gap-2.5 group"
              >
                <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 to-growth-500 flex items-center justify-center text-white shadow-xs group-hover:scale-105 transition-transform">
                  <Icons.Leaf />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-extrabold text-lg sm:text-xl tracking-tight text-slate-900">HabitFlow</span>
                    <span className="text-[11px] font-bold px-2 py-0.5 rounded-full bg-brand-100 text-brand-800 border border-brand-200">21 Días</span>
                    <span className={`text-[11px] font-bold px-2 py-0.5 rounded-full border hidden sm:inline-block ${currentMagnitudeConfig.badgeBg}`}>
                      {currentMagnitudeConfig.icon} {currentMagnitudeConfig.shortName}
                    </span>
                  </div>
                  <p className="text-xs text-slate-500 font-medium hidden sm:block truncate max-w-sm">
                    {appState.activeHabit}
                  </p>
                </div>
              </div>
            </div>

            {/* Navigation Views */}
            <nav className="w-full sm:w-auto flex items-center justify-between sm:justify-end gap-1 sm:gap-2 overflow-x-auto pb-0.5 sm:pb-0">
              <button
                type="button"
                onClick={() => navigateTo('dashboard')}
                className={`shrink-0 flex items-center gap-1.5 px-2.5 sm:px-3 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                  appState.currentView === 'dashboard'
                    ? 'bg-slate-900 text-white shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Icons.Calendar />
                <span className="hidden md:inline">Dashboard y</span> Calendario
              </button>

              <button
                type="button"
                onClick={() => navigateTo('activity', appState.currentViewingDay)}
                className={`shrink-0 flex items-center gap-1.5 px-2.5 sm:px-3 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                  appState.currentView === 'activity'
                    ? 'bg-brand-600 text-white shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Icons.Target />
                <span>Actividad</span>
                <span className="hidden sm:inline">Diaria</span>
                <span className="ml-0.5 sm:ml-1 text-[11px] px-1.5 py-0.2 rounded-full bg-white/20 font-mono">
                  D{appState.currentViewingDay}
                </span>
              </button>

              <button
                type="button"
                onClick={() => navigateTo('metrics')}
                className={`shrink-0 flex items-center gap-1.5 px-2.5 sm:px-3 py-1.5 sm:py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all ${
                  appState.currentView === 'metrics'
                    ? 'bg-growth-600 text-white shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Icons.BarChart />
                <span className="hidden sm:inline">Progreso y</span> Métricas
              </button>

              <button
                type="button"
                onClick={() => setDarkMode((prev) => !prev)}
                className="shrink-0 px-2.5 py-1.5 sm:py-2 rounded-xl text-xs font-semibold border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-all"
                title="Alternar modo claro / oscuro"
              >
                {darkMode ? 'Claro' : 'Oscuro'}
              </button>
            </nav>
          </div>
        </div>
      </header>

      {/* MAIN CONTENT AREA */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        {appState.currentView === 'dashboard' && (
          <DashboardView 
            appState={appState} 
            globalPercentage={globalPercentage}
            completedDaysCount={completedDaysCount}
            plantPhase={plantPhase}
            currentMagnitudeConfig={currentMagnitudeConfig}
            isDayLocked={isDayLocked}
            isDayCompleted={isDayCompleted}
            onSelectDay={(dayNum) => navigateTo('activity', dayNum)}
            onGoToMetrics={() => navigateTo('metrics')}
            onOpenConfig={openConfigModal}
            onChangeMagnitude={(mag) => handleSaveHabitConfig(appState.activeHabit, mag)}
          />
        )}

        {appState.currentView === 'activity' && (
          <DailyActivityView 
            dayNumber={appState.currentViewingDay}
            appState={appState}
            csvCatalog={csvCatalog}
            currentMagnitudeConfig={currentMagnitudeConfig}
            onUpdateField={handleUpdateDayField}
            onTimerComplete={handleTimerFinished}
            onFinalizeDay={handleFinalizeDay}
            isCompleted={isDayCompleted(appState.currentViewingDay)}
            isLocked={isDayLocked(appState.currentViewingDay)}
            calculateDayCompletion={calculateDayCompletion}
            onBackToDashboard={() => navigateTo('dashboard')}
            onNavigateDay={(dayNum) => navigateTo('activity', dayNum)}
            onChangeMagnitude={(mag) => handleSaveHabitConfig(appState.activeHabit, mag)}
          />
        )}

        {appState.currentView === 'metrics' && (
          <MetricsView 
            appState={appState}
            currentMagnitudeConfig={currentMagnitudeConfig}
            completedDaysCount={completedDaysCount}
            globalPercentage={globalPercentage}
            plantPhase={plantPhase}
            onResetAll={handleResetAllProgress}
            onSelectDay={(d) => navigateTo('activity', d)}
            onOpenConfig={openConfigModal}
          />
        )}
      </main>

      {/* FOOTER */}
      <footer className="mt-auto bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <span className="font-semibold text-slate-700">HabitFlow 21 Días</span>
            <span>•</span>
            <span>Calendario Calibrado: {currentMagnitudeConfig.name}</span>
          </div>
          <div className="text-slate-400">
            HTML5 localStorage persistente • React 18 & Tailwind CSS
          </div>
        </div>
      </footer>
    </div>
  );
};

const rootElement = document.getElementById('root');
const root = ReactDOM.createRoot(rootElement);
root.render(<HabitFlowApp />);
