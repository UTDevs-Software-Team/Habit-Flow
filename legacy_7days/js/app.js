/**
 * HABITFLOW - app.js (v2.5)
 * Controlador integral de la plataforma web interactiva de mejora de hábitos
 * Gestiona navegación por pestañas, las 8 etapas oficiales, los 6 métodos,
 * el jardín de la planta virtual, el simulador de días y la evaluación final.
 */

const HabitApp = (() => {
  let state = null;
  let activeTab = 'dashboard'; // 'dashboard' | 'plant' | 'methods' | 'evaluation' | 'home'
  let activeTimer = null;
  let timerRemainingSeconds = 0;
  let timerInitialSeconds = 0;
  let isTimerRunning = false;
  let plantPreviewStage = null; // Para previsualizar etapas en la pestaña Jardín

  /**
   * Inicialización al cargar la aplicación
   */
  function init() {
    state = HabitStorage.load();

    // Si es un usuario nuevo sin hábito seleccionado, preconfigurar por defecto Procrastinación
    // para que la aplicación muestre inmediatamente la experiencia completa y no solo una pantalla vacía
    if (!state.user.ageRange) {
      state.user.ageRange = '16-18';
    }
    if (!state.habit.id) {
      state.habit.id = 'procrastinacion';
      state.habit.currentDay = 1;
      state.habit.viewingDay = 1;
      HabitStorage.save(state);
    }

    setupTheme();
    renderApp();
    setupGlobalEvents();
    console.log('🌱 HabitFlow v2.5 activo. Estado actual:', state);
  }

  /**
   * Configuración de tema claro / oscuro
   */
  function setupTheme() {
    const savedTheme = localStorage.getItem('habitflow_theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  function toggleTheme() {
    const isDark = document.documentElement.classList.toggle('dark');
    localStorage.setItem('habitflow_theme', isDark ? 'dark' : 'light');
    const themeIcon = document.getElementById('theme-toggle-icon');
    if (themeIcon) {
      themeIcon.textContent = isDark ? '☀️' : '🌙';
    }
  }

  /**
   * Cambio de pestaña superior
   */
  function switchTab(tabName) {
    activeTab = tabName;
    plantPreviewStage = null;
    renderApp();
  }

  /**
   * Renderizado principal
   */
  function renderApp() {
    state = HabitStorage.load();
    updateNavigationTabs();

    const mainContainer = document.getElementById('app-main-container');
    if (!mainContainer) return;

    switch (activeTab) {
      case 'home':
        renderWelcomeView(mainContainer);
        break;
      case 'plant':
        renderPlantGardenView(mainContainer);
        break;
      case 'methods':
        renderAllMethodsCatalogView(mainContainer);
        break;
      case 'evaluation':
        renderEvaluationFullView(mainContainer);
        break;
      case 'dashboard':
      default:
        renderDashboardView(mainContainer);
        break;
    }
  }

  /**
   * Actualiza el estilo activo de los botones de navegación
   */
  function updateNavigationTabs() {
    document.querySelectorAll('.nav-tab-btn').forEach(btn => {
      const navTarget = btn.getAttribute('data-nav');
      if (navTarget === activeTab) {
        btn.className = 'nav-tab-btn flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-white dark:bg-slate-800 text-emerald-600 dark:text-emerald-400 font-bold shadow-sm border border-slate-200 dark:border-slate-700 transition-all';
      } else {
        btn.className = 'nav-tab-btn flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-slate-600 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white transition-all font-semibold';
      }
    });
  }

  /* ==========================================================================
     VISTA 1: BIENVENIDA & ONBOARDING
     ========================================================================== */
  function renderWelcomeView(container) {
    container.innerHTML = `
      <div class="max-w-5xl mx-auto px-4 py-8 fade-in space-y-8">
        <!-- Hero Section -->
        <div class="text-center space-y-6 pt-2 pb-6">
          <div class="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-emerald-100 dark:bg-emerald-950/80 border border-emerald-300 dark:border-emerald-800 text-emerald-800 dark:text-emerald-300 text-xs font-bold uppercase tracking-wider shadow-sm">
            <span>🌱</span> Plataforma Interactiva de 7 Días
          </div>

          <h1 class="text-4xl sm:text-5xl lg:text-6xl font-extrabold tracking-tight text-slate-900 dark:text-white leading-tight">
            Desarrolla mejores hábitos con <span class="bg-gradient-to-r from-emerald-600 via-teal-600 to-indigo-600 bg-clip-text text-transparent">HabitFlow</span>
          </h1>

          <p class="max-w-2xl mx-auto text-base sm:text-lg text-slate-600 dark:text-slate-300">
            Diseñado para adolescentes y adultos jóvenes de <strong>13 a 20 años</strong>. Métodos estructurados de una semana, seguimiento diario y motivación visual con una <strong>planta virtual</strong>.
          </p>

          <div class="pt-2 flex flex-col sm:flex-row items-center justify-center gap-4">
            <button type="button" onclick="HabitApp.switchTab('dashboard')" 
              class="w-full sm:w-auto px-8 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-2xl shadow-lg shadow-emerald-600/30 active:scale-95 transition-all text-base flex items-center justify-center gap-2">
              <span>🚀</span> Ir a mi Reto de 7 Días
            </button>
            <button type="button" onclick="HabitApp.switchTab('methods')"
              class="w-full sm:w-auto px-6 py-3.5 bg-white dark:bg-slate-800 hover:bg-slate-50 dark:hover:bg-slate-700 border border-slate-300 dark:border-slate-700 font-semibold rounded-2xl text-slate-700 dark:text-slate-200 transition-all text-base flex items-center justify-center gap-2">
              <span>📚</span> Explorar los 6 Métodos
            </button>
            <button type="button" onclick="HabitApp.switchTab('plant')"
              class="w-full sm:w-auto px-6 py-3.5 bg-sky-50 dark:bg-sky-950/40 text-sky-700 dark:text-sky-300 border border-sky-200 dark:border-sky-800 font-semibold rounded-2xl transition-all text-base flex items-center justify-center gap-2">
              <span>🪴</span> Jardín de la Planta
            </button>
          </div>
        </div>

        <!-- 3 Pilares del Método -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="glass-card p-6 rounded-3xl shadow-sm border border-slate-200/80 dark:border-slate-800 text-center space-y-3">
            <div class="w-14 h-14 mx-auto rounded-2xl bg-amber-100 dark:bg-amber-950/60 text-amber-700 dark:text-amber-300 flex items-center justify-center text-2xl font-black">
              🎯
            </div>
            <h3 class="text-lg font-bold text-slate-900 dark:text-white">Un hábito a la vez</h3>
            <p class="text-xs sm:text-sm text-slate-600 dark:text-slate-400">
              Escoge solo un hábito de los 6 disponibles para concentrar tu energía en 7 días continuos y medibles.
            </p>
          </div>

          <div class="glass-card p-6 rounded-3xl shadow-sm border border-slate-200/80 dark:border-slate-800 text-center space-y-3">
            <div class="w-14 h-14 mx-auto rounded-2xl bg-emerald-100 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-300 flex items-center justify-center text-2xl font-black">
              🪴
            </div>
            <h3 class="text-lg font-bold text-slate-900 dark:text-white">Planta virtual viva</h3>
            <p class="text-xs sm:text-sm text-slate-600 dark:text-slate-400">
              Evoluciona a través de 5 etapas: Semilla, Brote, Planta pequeña, Desarrollada y Florecida. ¡Nunca pierde su crecimiento!
            </p>
          </div>

          <div class="glass-card p-6 rounded-3xl shadow-sm border border-slate-200/80 dark:border-slate-800 text-center space-y-3">
            <div class="w-14 h-14 mx-auto rounded-2xl bg-indigo-100 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 flex items-center justify-center text-2xl font-black">
              🧠
            </div>
            <h3 class="text-lg font-bold text-slate-900 dark:text-white">Adaptado a tu edad</h3>
            <p class="text-xs sm:text-sm text-slate-600 dark:text-slate-400">
              Personalizado para 13-15 años (colegio/gaming), 16-18 años (preparatoria) o 19-20 años (universidad/trabajo).
            </p>
          </div>
        </div>

        <!-- Acceso directo a los 6 métodos -->
        <div class="glass-card p-6 sm:p-8 rounded-3xl border border-slate-200/80 dark:border-slate-800">
          <div class="flex items-center justify-between mb-6 flex-wrap gap-2">
            <div>
              <h2 class="text-xl font-extrabold text-slate-900 dark:text-white">
                Catálogo de Métodos de 7 Días
              </h2>
              <p class="text-xs text-slate-500 dark:text-slate-400">Selecciona cualquiera para comenzar tu reto de inmediato:</p>
            </div>
            <span class="text-xs font-bold px-3 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300">
              6 métodos disponibles
            </span>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            ${HABITFLOW_DATA.methods.map(m => `
              <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-900/60 border border-slate-200/70 dark:border-slate-800 hover:border-emerald-500 transition-all flex flex-col justify-between space-y-3">
                <div class="flex items-start gap-3">
                  <span class="text-3xl p-2 rounded-xl bg-white dark:bg-slate-800 shadow-sm">${m.icon}</span>
                  <div>
                    <h4 class="text-sm font-bold text-slate-900 dark:text-white">${m.title}</h4>
                    <p class="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 line-clamp-2">${m.objective}</p>
                  </div>
                </div>
                <button type="button" onclick="HabitApp.selectHabitDirectly('${m.id}')"
                  class="w-full py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl text-xs shadow-sm transition-all flex items-center justify-center gap-1.5">
                  <span>Comenzar este hábito</span> <span>→</span>
                </button>
              </div>
            `).join('')}
          </div>
        </div>
      </div>
    `;
  }

  /* ==========================================================================
     VISTA 2: PANEL PRINCIPAL (DASHBOARD - RETO 7 DÍAS + PLANTA VIRTUAL)
     ========================================================================== */
  function renderDashboardView(container) {
    const habitData = HABITFLOW_DATA.methods.find(m => m.id === state.habit.id) || HABITFLOW_DATA.methods[0];
    const viewingDayNum = state.habit.viewingDay || 1;
    const currentSimulatedDay = state.habit.currentDay || 1;
    const dayData = habitData.days.find(d => d.day === viewingDayNum) || habitData.days[0];
    const completedDaysSet = new Set(state.habit.completedDays || []);
    const isViewingDayCompleted = completedDaysSet.has(viewingDayNum);
    const isFutureDay = viewingDayNum > currentSimulatedDay;

    // Calcular estado de la planta
    const plantState = VirtualPlant.calculatePlantState(
      completedDaysSet.size,
      state.habit.maxProgressPercentage
    );

    const userAge = state.user.ageRange || '16-18';
    const ageTip = dayData.ageAdaptations[userAge] || dayData.description;

    container.innerHTML = `
      <div class="max-w-6xl mx-auto px-4 py-6 fade-in space-y-6">
        
        <!-- SELECTORES SUPERIORES RÁPIDOS: HÁBITO ACTIVO + RANGO DE EDAD -->
        <div class="glass-card p-4 sm:p-5 rounded-3xl border border-slate-200/80 dark:border-slate-800 flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          
          <!-- Hábito Seleccionado -->
          <div class="flex items-center gap-3">
            <span class="text-3xl p-2.5 rounded-2xl bg-emerald-100 dark:bg-emerald-950/80 shadow-sm">${habitData.icon}</span>
            <div>
              <div class="flex items-center gap-2 flex-wrap">
                <span class="text-xs font-bold px-2 py-0.5 rounded-full ${habitData.badgeColor}">Método #${habitData.number}</span>
                <span class="text-xs text-slate-500 dark:text-slate-400">7 Días Guiados</span>
                <!-- Selector rápido de edad -->
                <div class="inline-flex items-center gap-1 text-[11px] bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-full font-semibold text-slate-700 dark:text-slate-300">
                  <span>Edad:</span>
                  <select onchange="HabitApp.changeAgeRangeDirectly(this.value)" class="bg-transparent font-bold text-emerald-600 dark:text-emerald-400 focus:outline-none cursor-pointer">
                    <option value="13-15" ${userAge === '13-15' ? 'selected' : ''}>13 a 15 años</option>
                    <option value="16-18" ${userAge === '16-18' ? 'selected' : ''}>16 a 18 años</option>
                    <option value="19-20" ${userAge === '19-20' ? 'selected' : ''}>19 a 20 años</option>
                  </select>
                </div>
              </div>
              <h2 class="text-xl sm:text-2xl font-extrabold text-slate-900 dark:text-white mt-0.5">
                ${habitData.title}
              </h2>
            </div>
          </div>

          <!-- Selector Rápido de los 6 Hábitos -->
          <div class="flex items-center gap-2 flex-wrap">
            <label class="text-xs font-bold text-slate-500 dark:text-slate-400">Cambiar a otro hábito:</label>
            <select onchange="HabitApp.selectHabitDirectly(this.value)"
              class="px-3 py-1.5 rounded-xl bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs font-bold text-slate-800 dark:text-white focus:ring-2 focus:ring-emerald-500 cursor-pointer">
              ${HABITFLOW_DATA.methods.map(m => `
                <option value="${m.id}" ${m.id === habitData.id ? 'selected' : ''}>
                  ${m.icon} ${m.title}
                </option>
              `).join('')}
            </select>
            <button type="button" onclick="HabitApp.switchTab('evaluation')"
              class="px-3.5 py-1.5 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-700 dark:text-indigo-300 hover:bg-indigo-100 border border-indigo-200 dark:border-indigo-800 text-xs font-bold transition-all flex items-center gap-1.5">
              <span>📊</span> Evaluación
            </button>
          </div>
        </div>

        <!-- ETAPA 4: OBJETIVO E INDICADOR OFICIAL -->
        <div class="p-4 rounded-2xl bg-emerald-50/70 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800 text-xs sm:text-sm text-slate-700 dark:text-slate-300 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div class="flex items-center gap-2">
            <span class="text-lg">🎯</span>
            <div>
              <strong>Objetivo del método:</strong> ${habitData.objective}
            </div>
          </div>
          <div class="text-xs text-emerald-800 dark:text-emerald-300 font-semibold bg-emerald-100 dark:bg-emerald-900/50 px-2.5 py-1 rounded-xl shrink-0">
            📌 Indicador: ${habitData.indicator}
          </div>
        </div>

        <!-- LAYOUT DE 2 COLUMNAS (CALENDARIO & CHECKLIST / PLANTA VIRTUAL) -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">

          <!-- COLUMNA IZQUIERDA: CALENDARIO & ACTIVIDAD DIARIA (7 Cols) -->
          <div class="lg:col-span-7 space-y-6">

            <!-- ETAPA 6: CALENDARIO INTERACTIVO DE 7 DÍAS -->
            <div class="glass-card p-5 rounded-3xl border border-slate-200/80 dark:border-slate-800">
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-2">
                  <span class="text-xl">📅</span>
                  <h3 class="text-base font-bold text-slate-900 dark:text-white">Calendario de 7 Días</h3>
                </div>
                <div class="text-xs text-slate-500 dark:text-slate-400">
                  ${completedDaysSet.size} de 7 completados (${plantState.effectivePercent}%)
                </div>
              </div>

              <!-- Strip de los 7 Días -->
              <div class="grid grid-cols-7 gap-2">
                ${habitData.days.map(d => {
                  const isDone = completedDaysSet.has(d.day);
                  const isViewing = d.day === viewingDayNum;
                  const isFuture = d.day > currentSimulatedDay;

                  let borderClass = 'border-slate-200 dark:border-slate-800';
                  let bgClass = 'bg-white dark:bg-slate-900';
                  let textClass = 'text-slate-700 dark:text-slate-300';
                  let icon = '⚪';

                  if (isDone) {
                    borderClass = 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/40';
                    textClass = 'text-emerald-700 dark:text-emerald-300 font-bold';
                    icon = '✅';
                  } else if (isFuture) {
                    borderClass = 'border-dashed border-slate-300 dark:border-slate-700 bg-slate-50 dark:bg-slate-900/40 opacity-70';
                    icon = '🔒';
                  } else {
                    borderClass = 'border-amber-300 dark:border-amber-700/60 bg-amber-50/50 dark:bg-amber-950/20';
                    icon = '⏳';
                  }

                  if (isViewing) {
                    borderClass += ' ring-2 ring-indigo-500 dark:ring-indigo-400 shadow-md';
                  }

                  return `
                    <button type="button" onclick="HabitApp.viewDay(${d.day})"
                      class="day-card p-2 sm:p-3 rounded-2xl border ${borderClass} ${bgClass} text-center flex flex-col items-center justify-between min-h-[75px] transition-all">
                      <span class="text-[10px] font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">Día ${d.day}</span>
                      <span class="text-base my-0.5">${icon}</span>
                      <span class="text-[9px] ${textClass} leading-tight line-clamp-1">${isDone ? 'Hecho' : isFuture ? 'Futuro' : 'Activo'}</span>
                    </button>
                  `;
                }).join('')}
              </div>

              <!-- Control de Simulación de Días -->
              <div class="mt-4 pt-4 border-t border-slate-200/80 dark:border-slate-800 flex items-center justify-between text-xs text-slate-500 dark:text-slate-400 flex-wrap gap-2">
                <div class="flex items-center gap-1.5">
                  <span>Día actual del reto:</span>
                  <span class="font-bold text-slate-800 dark:text-slate-200">Día ${currentSimulatedDay} de 7</span>
                </div>
                <div class="flex items-center gap-1">
                  <span class="text-[11px] text-slate-400 mr-1">Simular avance:</span>
                  ${[1, 2, 3, 4, 5, 6, 7].map(num => `
                    <button type="button" onclick="HabitApp.setSimulatedDay(${num})"
                      class="px-2 py-0.5 rounded text-[10px] font-semibold transition-all ${num === currentSimulatedDay ? 'bg-indigo-600 text-white shadow-sm' : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-200'}">
                      D${num}
                    </button>
                  `).join('')}
                </div>
              </div>
            </div>

            <!-- ETAPA 5: SEGUIMIENTO Y CHECKLIST DIARIO -->
            <div class="glass-card p-6 rounded-3xl border border-slate-200/80 dark:border-slate-800 space-y-5">
              
              <!-- Cabecera de la Actividad -->
              <div class="flex items-start justify-between gap-3">
                <div>
                  <div class="flex items-center gap-2 mb-1">
                    <span class="text-xs font-bold px-2.5 py-0.5 rounded-full ${isViewingDayCompleted ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300' : 'bg-amber-100 text-amber-800 dark:bg-amber-950 dark:text-amber-300'}">
                      Día ${dayData.day} de 7
                    </span>
                    <span class="text-xs text-slate-500 dark:text-slate-400">
                      ${isViewingDayCompleted ? '✓ Actividad realizada' : '⏳ Actividad pendiente'}
                    </span>
                  </div>
                  <h3 class="text-xl font-extrabold text-slate-900 dark:text-white">
                    ${dayData.title}
                  </h3>
                </div>

                <!-- Botones de Navegación entre Días -->
                <div class="flex items-center gap-1">
                  <button type="button" onclick="HabitApp.viewDay(${Math.max(1, viewingDayNum - 1)})" ${viewingDayNum === 1 ? 'disabled' : ''}
                    class="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-30 disabled:cursor-not-allowed hover:bg-slate-200" title="Día anterior">
                    ←
                  </button>
                  <button type="button" onclick="HabitApp.viewDay(${Math.min(7, viewingDayNum + 1)})" ${viewingDayNum === 7 ? 'disabled' : ''}
                    class="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 disabled:opacity-30 disabled:cursor-not-allowed hover:bg-slate-200" title="Día siguiente">
                    →
                  </button>
                </div>
              </div>

              <!-- Instrucción Oficial -->
              <div class="p-4 rounded-2xl bg-slate-50 dark:bg-slate-900/60 border border-slate-200/80 dark:border-slate-800/80">
                <div class="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 mb-1">
                  📋 Instrucción oficial:
                </div>
                <p class="text-sm text-slate-800 dark:text-slate-200 leading-relaxed font-medium">
                  ${dayData.description}
                </p>
              </div>

              <!-- Guía adaptada por edad -->
              <div class="p-4 rounded-2xl bg-indigo-50/70 dark:bg-indigo-950/30 border border-indigo-200 dark:border-indigo-800">
                <div class="flex items-center gap-2 text-xs font-bold text-indigo-900 dark:text-indigo-300 mb-1">
                  <span>💡</span> <span>Guía adaptada para tu edad (${userAge} años):</span>
                </div>
                <p class="text-xs sm:text-sm text-indigo-950 dark:text-indigo-200 leading-relaxed">
                  ${ageTip}
                </p>
              </div>

              <!-- Campo Interactivo Personalizado -->
              <div>
                ${renderInteractiveField(dayData, viewingDayNum)}
              </div>

              <!-- CASILLA DE VERIFICACIÓN MAESTRA -->
              <div class="pt-4 border-t border-slate-200/80 dark:border-slate-800">
                ${isFutureDay ? `
                  <div class="p-3.5 rounded-2xl bg-slate-100 dark:bg-slate-900 border border-dashed border-slate-300 dark:border-slate-700 text-xs text-slate-500 dark:text-slate-400 flex items-center gap-2">
                    <span class="text-base">🔒</span>
                    <div>
                      <strong>Día futuro bloqueado:</strong> Puedes consultar las instrucciones previas, pero no puedes marcar actividades futuras como completadas antes de realizarlas (Regla oficial de HabitFlow). Usa el selector de simulación arriba si deseas probarlo.
                    </div>
                  </div>
                ` : `
                  <label class="custom-checkbox-wrapper flex items-center gap-4 p-4 rounded-2xl cursor-pointer select-none transition-all ${isViewingDayCompleted ? 'bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-300 dark:border-emerald-700' : 'bg-slate-50 dark:bg-slate-900/60 border border-slate-200 dark:border-slate-800 hover:border-emerald-400'}">
                    <input type="checkbox" id="master-day-checkbox" ${isViewingDayCompleted ? 'checked' : ''} onchange="HabitApp.toggleDayCheckbox(${viewingDayNum})" class="hidden">
                    <div class="w-8 h-8 rounded-xl border-2 flex items-center justify-center transition-all ${isViewingDayCompleted ? 'bg-emerald-500 border-emerald-500 text-white' : 'border-slate-400 dark:border-slate-600 bg-white dark:bg-slate-800'}">
                      <svg class="w-5 h-5 text-white ${isViewingDayCompleted ? 'opacity-100' : 'opacity-0'} transition-opacity" fill="none" stroke="currentColor" stroke-width="3" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"></path>
                      </svg>
                    </div>
                    <div class="flex-1">
                      <div class="text-sm font-bold text-slate-900 dark:text-white">
                        ${isViewingDayCompleted ? '¡Actividad marcada como completada!' : `Marcar actividad del Día ${viewingDayNum} como realizada`}
                      </div>
                      <div class="text-xs text-slate-500 dark:text-slate-400">
                        ${isViewingDayCompleted ? 'Haz clic para desmarcar si necesitas corregir.' : 'Tu planta virtual crecerá al confirmar el cumplimiento.'}
                      </div>
                    </div>
                  </label>
                `}
              </div>

            </div>
          </div>

          <!-- COLUMNA DERECHA: ETAPA 7: PLANTA VIRTUAL (5 Cols) -->
          <div class="lg:col-span-5 space-y-6">

            <div class="glass-card p-6 rounded-3xl border border-slate-200/80 dark:border-slate-800 space-y-5 text-center relative overflow-hidden" id="plant-container-card">
              
              <!-- Cabecera de la Planta -->
              <div class="flex items-center justify-between text-left">
                <div>
                  <div class="text-xs font-bold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">
                    Motivación Visual
                  </div>
                  <h3 class="text-lg font-extrabold text-slate-900 dark:text-white">
                    Tu Planta Virtual
                  </h3>
                </div>
                <span class="text-xs font-bold px-2.5 py-1 rounded-full bg-emerald-100 text-emerald-800 dark:bg-emerald-950 dark:text-emerald-300">
                  ${plantState.stageData.levelName}
                </span>
              </div>

              <!-- SVG de la Planta -->
              <div class="relative py-2 flex items-center justify-center plant-svg-wrapper min-h-[290px]" id="virtual-plant-svg-box">
                ${VirtualPlant.renderPlantSVG(plantState.stageNumber, true)}
              </div>

              <!-- Mensaje Motivacional -->
              <div class="p-3 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200/80 dark:border-emerald-800/80 text-xs text-emerald-900 dark:text-emerald-200 font-medium">
                ${plantState.stageData.message}
              </div>

              <!-- Barra de Progreso y Fórmula -->
              <div class="space-y-2 text-left">
                <div class="flex items-center justify-between text-xs">
                  <span class="font-bold text-slate-700 dark:text-slate-300">Progreso semanal:</span>
                  <span class="font-extrabold text-emerald-600 dark:text-emerald-400 text-sm">
                    ${plantState.effectivePercent}%
                  </span>
                </div>
                <div class="w-full h-3 rounded-full bg-slate-200 dark:bg-slate-800 overflow-hidden">
                  <div class="h-full rounded-full bg-gradient-to-r from-emerald-500 via-teal-500 to-emerald-400 transition-all duration-700 ease-out"
                    style="width: ${plantState.effectivePercent}%"></div>
                </div>
                <div class="flex items-center justify-between text-[11px] text-slate-500 dark:text-slate-400 pt-0.5">
                  <span>(${completedDaysSet.size} de 7 actividades)</span>
                  <span class="italic font-mono">Progreso = (Actividades / 7) × 100</span>
                </div>
              </div>

              <!-- Regla de crecimiento -->
              <div class="text-[11px] text-slate-500 dark:text-slate-400 bg-slate-100 dark:bg-slate-900/60 p-2.5 rounded-xl text-center">
                🌱 <strong>Regla:</strong> La planta nunca pierde su nivel si dejas una actividad pendiente.
              </div>

              <!-- Botón interactivo: Regar -->
              <div class="pt-2">
                <button type="button" onclick="HabitApp.waterCurrentPlant()"
                  class="w-full py-2.5 rounded-2xl bg-sky-500 hover:bg-sky-600 active:scale-95 text-white font-bold text-xs shadow-md shadow-sky-500/25 transition-all flex items-center justify-center gap-2">
                  <span>🚿</span> <span>Regar mi planta</span>
                </button>
              </div>

            </div>

            <!-- Acceso a Evaluación y Jardín -->
            <div class="grid grid-cols-2 gap-3">
              <button type="button" onclick="HabitApp.switchTab('plant')"
                class="p-3.5 rounded-2xl bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-left transition-all">
                <div class="text-base">🪴</div>
                <div class="text-xs font-bold text-slate-800 dark:text-slate-200 mt-1">Ver Jardín Botánico</div>
                <div class="text-[10px] text-slate-500">Explora las 5 etapas</div>
              </button>

              <button type="button" onclick="HabitApp.switchTab('evaluation')"
                class="p-3.5 rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 border border-indigo-200 dark:border-indigo-800 hover:bg-indigo-100 text-left transition-all">
                <div class="text-base">📊</div>
                <div class="text-xs font-bold text-indigo-700 dark:text-indigo-300 mt-1">Evaluación Final</div>
                <div class="text-[10px] text-indigo-600/70 dark:text-indigo-400/70">Resumen y reflexión</div>
              </button>
            </div>

          </div>

        </div>

      </div>
    `;

    // Restaurar temporizador
    if (dayData.timerMinutes && !isTimerRunning) {
      timerInitialSeconds = dayData.timerMinutes * 60;
      timerRemainingSeconds = timerInitialSeconds;
      updateTimerDisplay();
    }
  }

  /* ==========================================================================
     VISTA 3: JARDÍN BOTÁNICO DE LA PLANTA VIRTUAL
     ========================================================================== */
  function renderPlantGardenView(container) {
    const completedCount = (state.habit.completedDays || []).length;
    const realPlantState = VirtualPlant.calculatePlantState(completedCount, state.habit.maxProgressPercentage);
    const displayStage = plantPreviewStage || realPlantState.stageNumber;
    const currentStageData = HABITFLOW_DATA.plantStages.find(s => s.stage === displayStage) || HABITFLOW_DATA.plantStages[0];

    container.innerHTML = `
      <div class="max-w-4xl mx-auto px-4 py-8 fade-in space-y-6">
        <div class="text-center space-y-2">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 text-xs font-bold uppercase">
            <span>🪴</span> Sistema de Motivación Visual
          </div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white">
            El Jardín Botánico de HabitFlow
          </h2>
          <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400 max-w-lg mx-auto">
            Tu planta evoluciona en 5 etapas botánicas conforme completas tus actividades de 7 días.
          </p>
        </div>

        <!-- Selector de Etapas para Exploración -->
        <div class="grid grid-cols-2 sm:grid-cols-5 gap-2">
          ${HABITFLOW_DATA.plantStages.map(s => {
            const isSelected = s.stage === displayStage;
            const isCurrent = s.stage === realPlantState.stageNumber;
            return `
              <button type="button" onclick="HabitApp.previewStage(${s.stage})"
                class="p-3 rounded-2xl border text-center transition-all ${isSelected ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-950/40 ring-2 ring-emerald-500/20' : 'border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900'}">
                <div class="text-xs font-bold text-slate-800 dark:text-slate-200">${s.name}</div>
                <div class="text-[10px] text-slate-500 dark:text-slate-400">${Math.round(s.minPercent)}% - ${Math.round(s.maxPercent)}%</div>
                ${isCurrent ? '<span class="inline-block mt-1 text-[9px] font-black px-1.5 py-0.2 rounded bg-emerald-500 text-white">Tu etapa</span>' : ''}
              </button>
            `;
          }).join('')}
        </div>

        <!-- Tarjeta Principal de la Planta -->
        <div class="glass-card p-8 rounded-3xl border border-slate-200/80 dark:border-slate-800 text-center space-y-6 relative overflow-hidden">
          
          <div class="flex items-center justify-between text-left">
            <div>
              <span class="text-xs font-bold text-emerald-600 dark:text-emerald-400">Etapa Botánica #${displayStage} de 5</span>
              <h3 class="text-2xl font-black text-slate-900 dark:text-white">${currentStageData.name}</h3>
            </div>
            <div class="text-right">
              <span class="text-xs font-bold text-slate-500 dark:text-slate-400">Tu progreso real:</span>
              <div class="text-xl font-black text-emerald-600 dark:text-emerald-400">${realPlantState.effectivePercent}%</div>
            </div>
          </div>

          <div class="plant-svg-wrapper min-h-[300px] flex items-center justify-center" id="garden-plant-box">
            ${VirtualPlant.renderPlantSVG(displayStage, true)}
          </div>

          <p class="text-sm text-slate-700 dark:text-slate-300 max-w-md mx-auto italic">
            "${currentStageData.description}"
          </p>

          <div class="flex items-center justify-center gap-3">
            <button type="button" onclick="HabitApp.waterGardenPlant()"
              class="px-6 py-2.5 rounded-2xl bg-sky-500 hover:bg-sky-600 active:scale-95 text-white font-bold text-xs shadow-md shadow-sky-500/25 transition-all flex items-center gap-2">
              <span>🚿</span> <span>Regar esta planta</span>
            </button>
            <button type="button" onclick="HabitApp.switchTab('dashboard')"
              class="px-6 py-2.5 rounded-2xl bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white font-bold text-xs shadow-md transition-all flex items-center gap-2">
              <span>📌</span> <span>Volver a mi Reto</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }

  function previewStage(stageNum) {
    plantPreviewStage = stageNum;
    renderApp();
  }

  function waterGardenPlant() {
    VirtualPlant.waterPlant('garden-plant-box');
  }

  /* ==========================================================================
     VISTA 4: CATÁLOGO COMPLETO DE LOS 6 MÉTODOS Y SUS 7 DÍAS
     ========================================================================== */
  function renderAllMethodsCatalogView(container) {
    const userAge = state.user.ageRange || '16-18';

    container.innerHTML = `
      <div class="max-w-5xl mx-auto px-4 py-8 fade-in space-y-6">
        <div class="flex items-center justify-between flex-wrap gap-4">
          <div>
            <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 dark:bg-emerald-950 text-emerald-700 dark:text-emerald-300 text-xs font-bold uppercase">
              <span>📚</span> Catálogo Completo de Métodos
            </div>
            <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white mt-1">
              Los 6 Métodos de Mejora de Hábitos
            </h2>
          </div>

          <!-- Selector de rango de edad para previsualizar adaptaciones -->
          <div class="flex items-center gap-2 text-xs">
            <span class="font-bold text-slate-600 dark:text-slate-400">Ver adaptaciones para:</span>
            <select onchange="HabitApp.changeAgeRangeDirectly(this.value)"
              class="px-3 py-1.5 rounded-xl bg-white dark:bg-slate-800 border border-slate-300 dark:border-slate-700 font-bold text-emerald-600 dark:text-emerald-400">
              <option value="13-15" ${userAge === '13-15' ? 'selected' : ''}>13 a 15 años</option>
              <option value="16-18" ${userAge === '16-18' ? 'selected' : ''}>16 a 18 años</option>
              <option value="19-20" ${userAge === '19-20' ? 'selected' : ''}>19 a 20 años</option>
            </select>
          </div>
        </div>

        <!-- Los 6 métodos con acordeón o tarjetas detalladas -->
        <div class="space-y-6">
          ${HABITFLOW_DATA.methods.map(method => {
            const isCurrentlyActive = method.id === state.habit.id;
            return `
              <div class="glass-card rounded-3xl p-6 border ${isCurrentlyActive ? 'border-emerald-500 ring-2 ring-emerald-500/20' : 'border-slate-200 dark:border-slate-800'} space-y-4">
                <div class="flex items-start justify-between flex-wrap gap-3">
                  <div class="flex items-center gap-3">
                    <span class="text-3xl p-2.5 rounded-2xl bg-white dark:bg-slate-800 shadow-sm">${method.icon}</span>
                    <div>
                      <div class="flex items-center gap-2">
                        <span class="text-xs font-bold px-2 py-0.5 rounded-full ${method.badgeColor}">Método #${method.number}</span>
                        ${isCurrentlyActive ? '<span class="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-500 text-white">Actualmente en curso</span>' : ''}
                      </div>
                      <h3 class="text-xl font-extrabold text-slate-900 dark:text-white mt-0.5">${method.title}</h3>
                    </div>
                  </div>

                  <button type="button" onclick="HabitApp.selectHabitDirectly('${method.id}')"
                    class="px-4 py-2 rounded-xl ${isCurrentlyActive ? 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 font-semibold' : 'bg-emerald-600 hover:bg-emerald-700 text-white font-bold shadow-md shadow-emerald-600/20'} text-xs transition-all">
                    ${isCurrentlyActive ? 'Continuar este método' : 'Activar este método ahora'}
                  </button>
                </div>

                <div class="p-3 rounded-2xl bg-slate-50 dark:bg-slate-900/60 border border-slate-200/60 dark:border-slate-800/80 text-xs text-slate-700 dark:text-slate-300 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                  <div><strong>Objetivo:</strong> ${method.objective}</div>
                  <div class="text-slate-500"><strong>Indicador:</strong> ${method.indicator}</div>
                </div>

                <!-- Los 7 Días del Método -->
                <div class="pt-2">
                  <h4 class="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 mb-2">
                    Las 7 actividades estructuradas:
                  </h4>
                  <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-2.5">
                    ${method.days.map(d => `
                      <div class="p-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 space-y-1">
                        <div class="flex items-center justify-between text-[11px] font-bold text-slate-400">
                          <span>DÍA ${d.day}</span>
                          ${d.timerMinutes ? `<span>⏱️ ${d.timerMinutes}m</span>` : ''}
                        </div>
                        <div class="text-xs font-bold text-slate-900 dark:text-white line-clamp-1">${d.title}</div>
                        <p class="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-2">${d.description}</p>
                      </div>
                    `).join('')}
                  </div>
                </div>
              </div>
            `;
          }).join('')}
        </div>
      </div>
    `;
  }

  /* ==========================================================================
     VISTA 5: EVALUACIÓN FINAL COMPLETA
     ========================================================================== */
  function renderEvaluationFullView(container) {
    const habitData = HABITFLOW_DATA.methods.find(m => m.id === state.habit.id) || HABITFLOW_DATA.methods[0];
    const completedCount = (state.habit.completedDays || []).length;
    const pendingCount = 7 - completedCount;
    const plantState = VirtualPlant.calculatePlantState(completedCount, state.habit.maxProgressPercentage);

    container.innerHTML = `
      <div class="max-w-3xl mx-auto px-4 py-8 fade-in space-y-6">
        
        <div class="text-center space-y-2">
          <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-indigo-100 dark:bg-indigo-950/80 text-indigo-700 dark:text-indigo-300 text-xs font-bold uppercase tracking-wider">
            <span>🏆</span> Etapa 8: Evaluación Final
          </div>
          <h2 class="text-3xl font-extrabold text-slate-900 dark:text-white">
            Resumen de Resultados de 7 Días
          </h2>
          <p class="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Balance completo de tu semana para el método de <strong>${habitData.title}</strong>.
          </p>
        </div>

        <!-- 4 Métricas Oficiales de la Sección 8 -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <div class="p-4 rounded-2xl bg-white dark:bg-slate-900 text-center border border-slate-200 dark:border-slate-800 shadow-sm">
            <div class="text-xs text-slate-500 dark:text-slate-400">Hábito</div>
            <div class="text-sm font-extrabold text-slate-900 dark:text-white mt-1 truncate">
              ${habitData.icon} ${habitData.title}
            </div>
          </div>

          <div class="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/40 text-center border border-emerald-200 dark:border-emerald-800">
            <div class="text-xs text-emerald-700 dark:text-emerald-300 font-semibold">Completadas</div>
            <div class="text-2xl font-black text-emerald-600 dark:text-emerald-400 mt-0.5">
              ${completedCount} <span class="text-xs font-bold text-slate-400">/ 7</span>
            </div>
          </div>

          <div class="p-4 rounded-2xl bg-amber-50 dark:bg-amber-950/40 text-center border border-amber-200 dark:border-amber-800">
            <div class="text-xs text-amber-700 dark:text-amber-300 font-semibold">Pendientes</div>
            <div class="text-2xl font-black text-amber-600 dark:text-amber-400 mt-0.5">
              ${pendingCount}
            </div>
          </div>

          <div class="p-4 rounded-2xl bg-indigo-50 dark:bg-indigo-950/40 text-center border border-indigo-200 dark:border-indigo-800">
            <div class="text-xs text-indigo-700 dark:text-indigo-300 font-semibold">Cumplimiento</div>
            <div class="text-2xl font-black text-indigo-600 dark:text-indigo-400 mt-0.5">
              ${plantState.effectivePercent}%
            </div>
          </div>
        </div>

        <!-- Etapa Final de la Planta -->
        <div class="glass-card p-6 rounded-3xl border border-slate-200/80 dark:border-slate-800 flex flex-col sm:flex-row items-center gap-6">
          <div class="w-32 h-32 rounded-2xl bg-white dark:bg-slate-900 p-2 shrink-0 flex items-center justify-center shadow-md">
            ${VirtualPlant.renderPlantSVG(plantState.stageNumber, false)}
          </div>
          <div class="space-y-1 text-center sm:text-left">
            <div class="text-xs font-bold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">
              Etapa final de la planta:
            </div>
            <h4 class="text-xl font-extrabold text-slate-900 dark:text-white">
              ${plantState.stageData.levelName} (${plantState.stageData.name})
            </h4>
            <p class="text-xs sm:text-sm text-slate-600 dark:text-slate-300 leading-relaxed">
              ${plantState.stageData.description}
            </p>
          </div>
        </div>

        <!-- Pregunta de Reflexión Oficial -->
        <div class="glass-card p-6 rounded-3xl border border-slate-200 dark:border-slate-800 space-y-3">
          <label class="block text-sm font-bold text-slate-800 dark:text-slate-200">
            🤔 Pregunta de reflexión:
            <span class="block text-xs font-normal text-slate-500 dark:text-slate-400 mt-1">
              ¿Qué estrategia o cambio te resultó más útil esta semana y cómo planeas mantener este hábito?
            </span>
          </label>
          <textarea id="final-reflection-textarea" rows="4"
            oninput="HabitApp.handleReflectionInput(this.value)"
            placeholder="Escribe aquí tu balance y conclusiones de estos 7 días..."
            class="w-full px-4 py-3 rounded-2xl bg-white dark:bg-slate-900 border border-slate-300 dark:border-slate-700 text-xs sm:text-sm text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500">${state.habit.reflection || ''}</textarea>
          <div class="text-[11px] text-slate-400 text-right">Se guarda automáticamente en LocalStorage</div>
        </div>

        <!-- Acciones: Repetir o Elegir Nuevo Hábito -->
        <div class="flex flex-col sm:flex-row items-center justify-between gap-3 pt-2">
          <button type="button" onclick="HabitApp.repeatMethod()"
            class="w-full sm:w-auto px-6 py-3 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md transition-all flex items-center justify-center gap-2">
            <span>🔄</span> Repetir este método 7 días más
          </button>
          
          <button type="button" onclick="HabitApp.switchTab('methods')"
            class="w-full sm:w-auto px-6 py-3 rounded-2xl bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs shadow-md transition-all flex items-center justify-center gap-2">
            <span>🌱</span> Elegir otro hábito
          </button>
        </div>

      </div>
    `;
  }

  /* ==========================================================================
     CAMPOS INTERACTIVOS PERSONALIZADOS POR ACTIVIDAD
     ========================================================================== */
  function renderInteractiveField(dayData, dayNum) {
    const savedInputs = (state.habit.dayInputs && state.habit.dayInputs[dayNum]) || {};

    switch (dayData.interactiveType) {
      case 'inputs_procrastination_d1':
        return `
          <div class="space-y-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                1. Escribe la tarea que normalmente pospones:
              </label>
              <input type="text" value="${savedInputs.pendingTask || ''}" 
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'pendingTask', this.value)"
                placeholder="Ej. Terminar el reporte de química, ordenar mi armario..."
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500">
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                2. ¿Qué suele distraerte o impedirte comenzar?
              </label>
              <input type="text" value="${savedInputs.distractionReason || ''}"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'distractionReason', this.value)"
                placeholder="Ej. Quedarme viendo videos cortos, pereza de buscar los libros..."
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500">
            </div>
          </div>
        `;

      case 'task_split_3':
        return `
          <div class="space-y-2 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            <div class="text-xs font-bold text-slate-700 dark:text-slate-300 mb-2">
              Divide una tarea grande en 3 pasos pequeños:
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold text-emerald-600">Paso 1:</span>
              <input type="text" value="${savedInputs.step1 || ''}"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'step1', this.value)"
                placeholder="Micro-paso 1 (haz este hoy)"
                class="flex-1 px-3 py-1.5 rounded-lg bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold text-slate-400">Paso 2:</span>
              <input type="text" value="${savedInputs.step2 || ''}"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'step2', this.value)"
                placeholder="Micro-paso 2"
                class="flex-1 px-3 py-1.5 rounded-lg bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            </div>
            <div class="flex items-center gap-2">
              <span class="text-xs font-bold text-slate-400">Paso 3:</span>
              <input type="text" value="${savedInputs.step3 || ''}"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'step3', this.value)"
                placeholder="Micro-paso 3"
                class="flex-1 px-3 py-1.5 rounded-lg bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            </div>
          </div>
        `;

      case 'list_5_items':
        return `
          <div class="space-y-2 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            <div class="text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
              Escribe tus 5 actividades o responsabilidades pendientes de la semana:
            </div>
            ${[1, 2, 3, 4, 5].map(i => `
              <input type="text" value="${savedInputs['item' + i] || ''}"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'item${i}', this.value)"
                placeholder="Responsabilidad #${i}"
                class="w-full px-3 py-1.5 rounded-lg bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            `).join('')}
          </div>
        `;

      case 'reading_note_1_idea':
      case 'reading_notes_3_ideas':
        return `
          <div class="space-y-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            ${renderTimerComponent(dayData.timerMinutes)}
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                ${dayData.interactiveType === 'reading_notes_3_ideas' ? 'Anota 3 ideas o aprendizajes que recuerdes de tu lectura:' : 'Escribe una idea o información interesante que hayas leído:'}
              </label>
              <textarea rows="2"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'readingNotes', this.value)"
                placeholder="Escribe aquí tus ideas o citas favoritas..."
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500">${savedInputs.readingNotes || ''}</textarea>
            </div>
          </div>
        `;

      case 'sleep_schedule_log':
        return `
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Hora a la que te duermes aprox:
              </label>
              <input type="time" value="${savedInputs.bedTime || '23:00'}"
                onchange="HabitApp.saveDayFieldValue(${dayNum}, 'bedTime', this.value)"
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Hora a la que te despiertas aprox:
              </label>
              <input type="time" value="${savedInputs.wakeTime || '07:00'}"
                onchange="HabitApp.saveDayFieldValue(${dayNum}, 'wakeTime', this.value)"
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
            </div>
          </div>
        `;

      case 'screentime_log_initial':
      case 'screentime_compare_results':
        return `
          <div class="space-y-3 bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
            <div>
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Tiempo de pantalla registrado en tu móvil hoy:
              </label>
              <div class="flex items-center gap-2">
                <input type="number" min="0" max="24" value="${savedInputs.screenHours || ''}"
                  oninput="HabitApp.saveDayFieldValue(${dayNum}, 'screenHours', this.value)"
                  placeholder="Horas" class="w-24 px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
                <span class="text-xs text-slate-500">horas</span>
                <input type="number" min="0" max="59" value="${savedInputs.screenMinutes || ''}"
                  oninput="HabitApp.saveDayFieldValue(${dayNum}, 'screenMinutes', this.value)"
                  placeholder="Minutos" class="w-24 px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
                <span class="text-xs text-slate-500">minutos</span>
              </div>
            </div>
            ${dayData.interactiveType === 'screentime_compare_results' ? `
              <div>
                <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                  ¿Qué estrategia te resultó más útil para reducirlo?
                </label>
                <input type="text" value="${savedInputs.bestStrategy || ''}"
                  oninput="HabitApp.saveDayFieldValue(${dayNum}, 'bestStrategy', this.value)"
                  placeholder="Ej. Apagar notificaciones, dejar el celular en otra pieza..."
                  class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs">
              </div>
            ` : ''}
          </div>
        `;

      default:
        if (dayData.timerMinutes) {
          return `
            <div class="bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
              ${renderTimerComponent(dayData.timerMinutes)}
            </div>
          `;
        } else {
          return `
            <div class="bg-white dark:bg-slate-900 p-4 rounded-2xl border border-slate-200 dark:border-slate-800">
              <label class="block text-xs font-bold text-slate-700 dark:text-slate-300 mb-1">
                Notas y observaciones de tu práctica de hoy:
              </label>
              <textarea rows="2"
                oninput="HabitApp.saveDayFieldValue(${dayNum}, 'generalNotes', this.value)"
                placeholder="Escribe aquí cómo te fue con la actividad de hoy..."
                class="w-full px-3 py-2 rounded-xl bg-slate-50 dark:bg-slate-800 border border-slate-300 dark:border-slate-700 text-xs text-slate-900 dark:text-white focus:ring-2 focus:ring-emerald-500">${savedInputs.generalNotes || ''}</textarea>
            </div>
          `;
        }
    }
  }

  function renderTimerComponent(minutes) {
    if (!minutes) return '';
    return `
      <div class="p-3.5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-700/80 flex items-center justify-between gap-3 flex-wrap">
        <div class="flex items-center gap-2.5">
          <span class="text-xl">⏱️</span>
          <div>
            <div class="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">Temporizador (${minutes} min)</div>
            <div id="interactive-timer-display" class="timer-display text-xl font-black text-slate-900 dark:text-white">
              ${String(Math.floor(minutes)).padStart(2, '0')}:00
            </div>
          </div>
        </div>
        <div class="flex items-center gap-1.5">
          <button type="button" id="timer-start-btn" onclick="HabitApp.toggleTimer(${minutes})"
            class="px-3.5 py-1.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-sm transition-all">
            Iniciar
          </button>
          <button type="button" onclick="HabitApp.resetTimer(${minutes})"
            class="px-2.5 py-1.5 rounded-xl bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300 text-xs font-semibold hover:bg-slate-300 transition-all">
            Reiniciar
          </button>
        </div>
      </div>
    `;
  }

  function toggleTimer(defaultMinutes) {
    if (isTimerRunning) {
      clearInterval(activeTimer);
      isTimerRunning = false;
      const startBtn = document.getElementById('timer-start-btn');
      if (startBtn) startBtn.textContent = 'Reanudar';
    } else {
      if (!timerRemainingSeconds || timerRemainingSeconds <= 0) {
        timerInitialSeconds = defaultMinutes * 60;
        timerRemainingSeconds = timerInitialSeconds;
      }
      isTimerRunning = true;
      const startBtn = document.getElementById('timer-start-btn');
      if (startBtn) startBtn.textContent = 'Pausar';

      activeTimer = setInterval(() => {
        timerRemainingSeconds--;
        updateTimerDisplay();

        if (timerRemainingSeconds <= 0) {
          clearInterval(activeTimer);
          isTimerRunning = false;
          VirtualPlant.SoundFX.playGrowthChime();
          alert('⏰ ¡Tiempo cumplido! Gran trabajo con la actividad de hoy.');
          if (startBtn) startBtn.textContent = 'Completado';
        }
      }, 1000);
    }
  }

  function resetTimer(defaultMinutes) {
    if (activeTimer) clearInterval(activeTimer);
    isTimerRunning = false;
    timerInitialSeconds = defaultMinutes * 60;
    timerRemainingSeconds = timerInitialSeconds;
    updateTimerDisplay();
    const startBtn = document.getElementById('timer-start-btn');
    if (startBtn) startBtn.textContent = 'Iniciar';
  }

  function updateTimerDisplay() {
    const display = document.getElementById('interactive-timer-display');
    if (!display) return;
    const mins = Math.floor(timerRemainingSeconds / 60);
    const secs = timerRemainingSeconds % 60;
    display.textContent = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  }

  /* ==========================================================================
     INTERACCIONES DEL CALENDARIO Y CAMBIOS DIRECTOS
     ========================================================================== */
  function viewDay(dayNum) {
    state = HabitStorage.setViewingDay(dayNum);
    renderApp();
  }

  function setSimulatedDay(dayNum) {
    state = HabitStorage.setCurrentDay(dayNum);
    renderApp();
  }

  function toggleDayCheckbox(dayNum) {
    const result = HabitStorage.toggleDayCompletion(dayNum);
    if (!result.success) {
      alert(result.message);
      return;
    }

    state = result.state;

    if (result.isNowCompleted) {
      VirtualPlant.SoundFX.playGrowthChime();

      const completedCount = (state.habit.completedDays || []).length;
      if (completedCount >= 6) {
        VirtualPlant.SoundFX.playBloomCelebration();
        triggerConfettiCelebration();
      }
    }

    renderApp();
  }

  function saveDayFieldValue(dayNum, key, value) {
    HabitStorage.saveDayInput(dayNum, key, value);
  }

  function waterCurrentPlant() {
    VirtualPlant.waterPlant('virtual-plant-svg-box');
  }

  function selectHabitDirectly(habitId) {
    state = HabitStorage.selectHabit(habitId);
    activeTab = 'dashboard';
    VirtualPlant.SoundFX.playGrowthChime();
    renderApp();
  }

  function changeAgeRangeDirectly(ageId) {
    state = HabitStorage.setAgeRange(ageId);
    renderApp();
  }

  function handleReflectionInput(value) {
    HabitStorage.saveReflection(value);
  }

  function repeatMethod() {
    if (confirm('¿Deseas reiniciar este método para realizar otra semana de práctica?')) {
      state = HabitStorage.repeatCurrentHabit();
      activeTab = 'dashboard';
      renderApp();
    }
  }

  function showAboutModal() {
    alert(
      "🌱 HABITFLOW - FUNCIONAMIENTO OFICIAL\n\n" +
      "1. Duración: 7 días estructurados por hábito.\n" +
      "2. Un hábito a la vez: Procrastinación, Organización, Lectura, Ejercicio, Sueño o Celular.\n" +
      "3. Rangos de edad: 13-15, 16-18 y 19-20 años para adaptar el lenguaje y ejemplos.\n" +
      "4. Calendario y Checklist: Marca las actividades al realizarlas. No puedes marcar días futuros antes de tiempo.\n" +
      "5. Planta Virtual: Crece en 5 etapas (Semilla, Brote, Planta pequeña, Desarrollada y Florecida). ¡Nunca pierde su crecimiento!\n" +
      "6. Evaluación: Al terminar los 7 días obtienes tu balance y reflexión."
    );
  }

  function triggerConfettiCelebration() {
    if (typeof confetti === 'function') {
      confetti({
        particleCount: 120,
        spread: 80,
        origin: { y: 0.6 }
      });
    }
  }

  function setupGlobalEvents() {
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        // Cierra modales si los hay
      }
    });
  }

  return {
    init,
    toggleTheme,
    switchTab,
    viewDay,
    setSimulatedDay,
    toggleDayCheckbox,
    saveDayFieldValue,
    waterCurrentPlant,
    waterGardenPlant,
    selectHabitDirectly,
    changeAgeRangeDirectly,
    handleReflectionInput,
    repeatMethod,
    showAboutModal,
    previewStage,
    toggleTimer,
    resetTimer
  };
})();

document.addEventListener('DOMContentLoaded', () => {
  HabitApp.init();
});

if (typeof window !== 'undefined') {
  window.HabitApp = HabitApp;
}
