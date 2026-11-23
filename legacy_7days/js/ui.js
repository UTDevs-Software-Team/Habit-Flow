/**
 * HabitFlow: Recovery - ui.js
 * Controlador principal de interfaz gráfica enfocado 100% en la superación de adicciones,
 * planes a corto/largo plazo, cronómetro de sobriedad, crisis SOS y diario de disparadores.
 */

const UI = {
  activeTab: 'today',
  selectedTodayDateStr: formatDate(new Date()),
  selectedAddictionOptionId: 'social_media',
  breathingInterval: null,
  clockInterval: null,

  init() {
    this.applyTheme();
    this.bindEvents();
    this.startRecoveryClock();
    this.switchTab('today');
  },

  applyTheme() {
    const data = Storage.get();
    const isDark = data.settings && data.settings.theme === 'dark';
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    const icon = document.getElementById('theme-toggle-icon');
    if (icon) icon.textContent = isDark ? '☀️' : '🌙';
  },

  toggleTheme() {
    const data = Storage.get();
    const current = data.settings?.theme === 'dark';
    data.settings.theme = current ? 'light' : 'dark';
    Storage.save(data);
    this.applyTheme();
    if (this.activeTab === 'stats') Stats.render();
  },

  playCelebrationSound() {
    const data = Storage.get();
    if (data.settings?.soundEnabled === false) return;

    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (!AudioCtx) return;
      const ctx = new AudioCtx();
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sine';
      const now = ctx.currentTime;
      osc.frequency.setValueAtTime(523.25, now);
      osc.frequency.setValueAtTime(659.25, now + 0.08);
      osc.frequency.setValueAtTime(783.99, now + 0.16);
      osc.frequency.setValueAtTime(1046.50, now + 0.24);

      gain.gain.setValueAtTime(0.15, now);
      gain.gain.exponentialRampToValueAtTime(0.001, now + 0.45);

      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start(now);
      osc.stop(now + 0.45);
    } catch (e) {}
  },

  triggerConfetti() {
    const data = Storage.get();
    if (data.settings?.confettiEnabled === false) return;
    if (typeof confetti === 'function') {
      confetti({ particleCount: 80, spread: 70, origin: { y: 0.6 } });
    }
  },

  showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    const bgClass = type === 'success' 
      ? 'bg-emerald-600 text-white' 
      : type === 'danger' 
      ? 'bg-rose-600 text-white' 
      : 'bg-indigo-600 text-white';

    toast.className = `flex items-center gap-2 px-4 py-3 rounded-xl shadow-lg ${bgClass} text-xs font-bold transition-all duration-300 transform translate-y-4 opacity-0`;
    toast.innerHTML = `<span>${type === 'success' ? '✓' : type === 'danger' ? '⚠️' : 'ℹ'}</span><span>${message}</span>`;

    container.appendChild(toast);
    requestAnimationFrame(() => {
      toast.classList.remove('translate-y-4', 'opacity-0');
      toast.classList.add('translate-y-0', 'opacity-100');
    });

    setTimeout(() => {
      toast.classList.add('opacity-0', 'translate-y-2');
      setTimeout(() => toast.remove(), 300);
    }, 2800);
  },

  // Navegación de pestañas
  switchTab(tabName) {
    this.activeTab = tabName;

    document.querySelectorAll('[data-tab-target]').forEach(btn => {
      const target = btn.getAttribute('data-tab-target');
      if (target === tabName) {
        btn.classList.add('bg-indigo-600', 'text-white', 'shadow-sm');
        btn.classList.remove('text-slate-600', 'dark:text-slate-300', 'hover:bg-slate-100', 'dark:hover:bg-slate-800');
      } else {
        btn.classList.remove('bg-indigo-600', 'text-white', 'shadow-sm');
        btn.classList.add('text-slate-600', 'dark:text-slate-300', 'hover:bg-slate-100', 'dark:hover:bg-slate-800');
      }
    });

    document.querySelectorAll('.tab-content-panel').forEach(panel => {
      panel.classList.add('hidden');
    });

    const activePanel = document.getElementById(`panel-${tabName}`);
    if (activePanel) {
      activePanel.classList.remove('hidden');
    }

    this.refreshCurrentView();
  },

  refreshCurrentView() {
    switch (this.activeTab) {
      case 'today':
        this.renderTodayView();
        break;
      case 'plan':
        this.renderPlanView();
        break;
      case 'calendar':
        Calendar.render();
        Calendar.showDayDetail(Calendar.selectedDateStr);
        break;
      case 'stats':
        Stats.render();
        break;
      case 'journal':
        this.renderJournalView();
        break;
      case 'settings':
        this.renderSettingsView();
        break;
    }
  },

  // ----------------------------------------------------
  // RELOJ DE SOBRIEDAD EN VIVO
  // ----------------------------------------------------
  startRecoveryClock() {
    if (this.clockInterval) clearInterval(this.clockInterval);
    this.clockInterval = setInterval(() => {
      this.tickRecoveryClock();
    }, 1000);
    this.tickRecoveryClock();
  },

  tickRecoveryClock() {
    const clean = Recovery.getCleanTime();
    if (!clean) return;

    // Actualizar nav badge
    const badge = document.getElementById('nav-clean-badge');
    if (badge) badge.textContent = `${clean.days}d`;

    // Actualizar números en reloj principal de Hoy
    const dEl = document.getElementById('clock-days');
    const hEl = document.getElementById('clock-hours');
    const mEl = document.getElementById('clock-mins');
    const sEl = document.getElementById('clock-secs');

    if (dEl) dEl.textContent = clean.days;
    if (hEl) hEl.textContent = clean.hours;
    if (mEl) mEl.textContent = clean.minutes;
    if (sEl) sEl.textContent = clean.seconds;

    // Impacto
    const impactEl = document.getElementById('clock-impact-text');
    if (impactEl) {
      if (clean.unitType === 'hours') {
        impactEl.textContent = `Has recuperado ${clean.savedAmount} horas de vida libre de adicción.`;
      } else {
        impactEl.textContent = `Has ahorrado $${clean.savedAmount} libre de consumo.`;
      }
    }
  },

  // ----------------------------------------------------
  // VISTA: HOY / CHECK-IN DE SOBRIEDAD
  // ----------------------------------------------------
  renderTodayView() {
    const plan = Recovery.getActivePlan();
    if (!plan) {
      this.openNewPlanModal();
      return;
    }

    const dateStr = this.selectedTodayDateStr;
    const summary = Tracker.getDaySummary(dateStr);
    const dateObj = parseDate(dateStr);
    const isToday = dateStr === formatDate(new Date());

    const titleEl = document.getElementById('today-date-title');
    if (titleEl) {
      const options = { weekday: 'long', day: 'numeric', month: 'long' };
      titleEl.textContent = `${isToday ? 'Hoy, ' : ''}${dateObj.toLocaleDateString('es-ES', options)}`;
    }

    // Cabecera del plan activo
    const iconEl = document.getElementById('today-addiction-icon');
    const nameEl = document.getElementById('today-addiction-name');
    const whyEl = document.getElementById('today-addiction-why');

    if (iconEl) iconEl.textContent = plan.icon;
    if (nameEl) nameEl.textContent = plan.title;
    if (whyEl) whyEl.textContent = `"${plan.whyReason}"`;

    // Estado de sobriedad de la fecha
    const statusPill = document.getElementById('today-sobriety-pill');
    if (statusPill) {
      if (summary.clean) {
        statusPill.className = 'px-3 py-1 rounded-full text-xs font-black bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
        statusPill.textContent = '🛡️ SOBRIO Y FIRME';
      } else {
        statusPill.className = 'px-3 py-1 rounded-full text-xs font-black bg-rose-500/20 text-rose-400 border border-rose-500/30';
        statusPill.textContent = '🔄 RECAÍDA REGISTRADA';
      }
    }

    // Selector de nivel de tentación
    document.querySelectorAll('[data-craving-level]').forEach(btn => {
      const lvl = btn.getAttribute('data-craving-level');
      if (lvl === summary.cravingLevel) {
        btn.classList.add('bg-indigo-600', 'text-white');
        btn.classList.remove('bg-slate-100', 'dark:bg-slate-800', 'text-slate-600', 'dark:text-slate-300');
      } else {
        btn.classList.remove('bg-indigo-600', 'text-white');
        btn.classList.add('bg-slate-100', 'dark:bg-slate-800', 'text-slate-600', 'dark:text-slate-300');
      }
    });

    // Notas del día
    const notesInput = document.getElementById('today-notes-input');
    if (notesInput) notesInput.value = summary.notes || '';

    // Hábitos de reemplazo del día
    const repsList = document.getElementById('today-replacements-list');
    if (repsList) {
      repsList.innerHTML = summary.replacements.map(rep => `
        <div class="flex items-center justify-between p-3.5 rounded-2xl border ${rep.completed ? 'bg-emerald-50/60 dark:bg-emerald-950/20 border-emerald-300 dark:border-emerald-700/50' : 'bg-white dark:bg-slate-800/80 border-slate-200 dark:border-slate-700/60'} shadow-sm transition-all">
          <div class="flex items-center gap-3">
            <span class="text-2xl">${rep.icon}</span>
            <div>
              <h4 class="text-xs sm:text-sm font-bold text-slate-900 dark:text-white ${rep.completed ? 'line-through text-slate-400 dark:text-slate-500' : ''}">
                ${rep.title}
              </h4>
              <span class="text-[10px] text-slate-400">Hábito de reemplazo diario</span>
            </div>
          </div>

          <div class="flex items-center gap-2">
            <span class="text-xs font-semibold text-slate-500 hidden sm:inline">${rep.completed ? 'Cumplido' : 'Marcar'}</span>
            <input type="checkbox" ${rep.completed ? 'checked' : ''} 
              onchange="UI.handleToggleReplacement('${rep.id}')" 
              class="habit-checkbox" />
          </div>
        </div>
      `).join('');
    }
  },

  handleToggleReplacement(repId) {
    const isNowDone = Recovery.toggleDailyReplacement(repId, this.selectedTodayDateStr);
    if (isNowDone) {
      this.playCelebrationSound();
      this.showToast('¡Hábito de reemplazo cumplido! +15 XP', 'success');
    }
    this.refreshCurrentView();
  },

  handleCravingLevelSelect(level) {
    const summary = Tracker.getDaySummary(this.selectedTodayDateStr);
    Tracker.setDayNotesAndCraving(this.selectedTodayDateStr, level, summary.notes);
    this.showToast('Nivel de tentación guardado', 'info');
    this.renderTodayView();
  },

  saveTodayNotes() {
    const text = document.getElementById('today-notes-input').value;
    const summary = Tracker.getDaySummary(this.selectedTodayDateStr);
    Tracker.setDayNotesAndCraving(this.selectedTodayDateStr, summary.cravingLevel, text);
    this.showToast('Reflexión diaria guardada', 'success');
  },

  // ----------------------------------------------------
  // VISTA: PLAN & METAS (CORTO, MEDIANO Y LARGO PLAZO)
  // ----------------------------------------------------
  renderPlanView() {
    const plan = Recovery.getActivePlan();
    if (!plan) {
      this.openNewPlanModal();
      return;
    }

    const iconEl = document.getElementById('plan-icon');
    const titleEl = document.getElementById('plan-title');
    const whyEl = document.getElementById('plan-why-quote');

    if (iconEl) iconEl.textContent = plan.icon;
    if (titleEl) titleEl.textContent = plan.title;
    if (whyEl) whyEl.textContent = `"${plan.whyReason}"`;

    this.renderGoalsPhase('short', plan.shortTermGoals, 'plan-goals-short', 'plan-count-short');
    this.renderGoalsPhase('medium', plan.mediumTermGoals, 'plan-goals-medium', 'plan-count-medium');
    this.renderGoalsPhase('long', plan.longTermGoals, 'plan-goals-long', 'plan-count-long');

    // Renderizar medallas de hitos
    this.renderBadges();
  },

  renderGoalsPhase(phase, goals = [], containerId, countId) {
    const container = document.getElementById(containerId);
    const countEl = document.getElementById(countId);
    if (!container) return;

    const completed = goals.filter(g => g.completed).length;
    if (countEl) countEl.textContent = `${completed}/${goals.length}`;

    container.innerHTML = goals.map(goal => `
      <div class="flex items-start gap-3 p-3.5 rounded-2xl border ${goal.completed ? 'bg-emerald-50/50 dark:bg-emerald-950/20 border-emerald-300 dark:border-emerald-700/50' : 'bg-slate-50 dark:bg-slate-800/50 border-slate-200 dark:border-slate-700/60'} transition-all">
        <input type="checkbox" ${goal.completed ? 'checked' : ''} 
          onchange="UI.handleToggleGoal('${phase}', '${goal.id}')" 
          class="habit-checkbox mt-0.5" />
        <div class="flex-1">
          <div class="flex items-center justify-between gap-1 mb-0.5">
            <h5 class="text-xs font-bold text-slate-900 dark:text-white ${goal.completed ? 'line-through text-slate-400' : ''}">
              ${goal.title}
            </h5>
            <span class="text-[10px] text-indigo-500 font-extrabold uppercase">${goal.period}</span>
          </div>
          <p class="text-[11px] text-slate-500 dark:text-slate-400 leading-snug">
            ${goal.desc}
          </p>
        </div>
      </div>
    `).join('');
  },

  handleToggleGoal(phase, goalId) {
    const item = Recovery.toggleGoal(phase, goalId);
    if (item && item.completed) {
      this.playCelebrationSound();
      this.triggerConfetti();
      this.showToast(`¡Hito alcanzado: ${item.title}! +35 XP`, 'success');
    }
    this.renderPlanView();
  },

  renderBadges() {
    const grid = document.getElementById('plan-badges-grid');
    if (!grid) return;

    const badges = Recovery.getBadges();
    const data = Storage.get();
    const unlocked = data.unlockedBadges || [];

    grid.innerHTML = badges.map(badge => {
      const isUnlocked = unlocked.includes(badge.id);
      return `
        <div class="badge-card ${isUnlocked ? 'unlocked' : 'locked'} p-3 rounded-2xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-800/40 flex flex-col items-center text-center justify-between">
          <span class="badge-icon text-3xl mb-1">${badge.icon}</span>
          <h5 class="text-[11px] font-bold text-slate-900 dark:text-white leading-tight mb-0.5">${badge.name}</h5>
          <span class="text-[9px] font-semibold text-slate-400 block">${isUnlocked ? 'Desbloqueado ✨' : 'En camino 🔒'}</span>
        </div>
      `;
    }).join('');
  },

  // ----------------------------------------------------
  // VISTA: DIARIO DE DISPARADORES & VICTORIAS
  // ----------------------------------------------------
  renderJournalView() {
    const listEl = document.getElementById('journal-events-list');
    if (!listEl) return;

    const data = Storage.get();
    const events = data.eventsLog || [];

    if (events.length === 0) {
      listEl.innerHTML = `
        <div class="p-8 text-center bg-white dark:bg-slate-800/40 rounded-3xl border border-slate-200 dark:border-slate-800 text-slate-400">
          <p class="text-3xl mb-2">📖</p>
          <p class="text-sm font-semibold">Tu diario está esperando tu primera entrada.</p>
          <p class="text-xs mt-1">Registra aquí cuando enfrentes una tentación o quieras anotar una reflexión.</p>
        </div>
      `;
      return;
    }

    listEl.innerHTML = events.map(ev => {
      const dateObj = new Date(ev.date);
      const isVictory = ev.type === 'craving_resisted';

      return `
        <div class="p-4 rounded-2xl border ${isVictory ? 'border-emerald-200 dark:border-emerald-800/50 bg-emerald-50/30 dark:bg-emerald-950/20' : 'border-rose-200 dark:border-rose-900/50 bg-rose-50/30 dark:bg-rose-950/20'}">
          <div class="flex items-center justify-between gap-2 mb-2">
            <div class="flex items-center gap-2">
              <span class="text-lg">${isVictory ? '🛡️' : '🔄'}</span>
              <span class="text-xs font-black uppercase ${isVictory ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}">
                ${isVictory ? 'Tentación Vencida' : 'Recaída Registrada'}
              </span>
            </div>
            <span class="text-[10px] text-slate-400">${dateObj.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })}</span>
          </div>

          <div class="space-y-1 text-xs text-slate-700 dark:text-slate-300">
            <p><strong>Disparador:</strong> ${ev.trigger || ev.reason}</p>
            ${ev.toolUsed ? `<p><strong>Herramienta usada:</strong> ${ev.toolUsed}</p>` : ''}
            ${ev.notes || ev.reflection ? `<p class="italic text-slate-500 dark:text-slate-400 mt-1">"${ev.notes || ev.reflection}"</p>` : ''}
          </div>
        </div>
      `;
    }).join('');
  },

  // ----------------------------------------------------
  // ASISTENTE DE CRISIS S.O.S. Y RESPIRACIÓN 4-7-8
  // ----------------------------------------------------
  openSOSModal() {
    const modal = document.getElementById('sos-modal');
    if (!modal) return;

    const plan = Recovery.getActivePlan();
    const whyEl = document.getElementById('sos-user-why');
    if (whyEl) {
      whyEl.textContent = plan?.whyReason || 'Recuperar mi libertad, salud y enfoque personal.';
    }

    const tips = Recovery.getEmergencyTips();
    const tipsList = document.getElementById('sos-tips-list');
    if (tipsList) {
      tipsList.innerHTML = tips.map(tip => `
        <li class="flex items-start gap-2">
          <span class="text-emerald-500 font-bold">✓</span>
          <span>${tip}</span>
        </li>
      `).join('');
    }

    this.startBreathingGuide();
    modal.classList.remove('hidden');
    modal.classList.add('flex');
  },

  closeSOSModal() {
    const modal = document.getElementById('sos-modal');
    if (modal) {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    }
    if (this.breathingInterval) {
      clearInterval(this.breathingInterval);
      this.breathingInterval = null;
    }
  },

  startBreathingGuide() {
    const textEl = document.getElementById('sos-breathing-text');
    if (!textEl) return;

    let step = 0; // 0: inhala (4s), 1: retén (7s), 2: exhala (8s)
    let secondsLeft = 4;
    textEl.textContent = `Inhala (${secondsLeft}s)`;

    if (this.breathingInterval) clearInterval(this.breathingInterval);

    this.breathingInterval = setInterval(() => {
      secondsLeft--;
      if (secondsLeft <= 0) {
        if (step === 0) {
          step = 1;
          secondsLeft = 7;
        } else if (step === 1) {
          step = 2;
          secondsLeft = 8;
        } else {
          step = 0;
          secondsLeft = 4;
        }
      }

      const label = step === 0 ? 'Inhala' : step === 1 ? 'Retén el aire' : 'Exhala suave';
      textEl.textContent = `${label} (${secondsLeft}s)`;
    }, 1000);
  },

  finishSOSVictory() {
    Recovery.recordCravingResisted('Tentación aguda superada en modo SOS', 'Respiración 4-7-8');
    this.closeSOSModal();
    this.playCelebrationSound();
    this.triggerConfetti();
    this.showToast('¡Victoria absoluta! Superaste la tentación y ganaste +25 XP 🛡️', 'success');
    this.refreshCurrentView();
  },

  quickResistCraving() {
    Recovery.recordCravingResisted('Impulso repentino superado con fuerza de voluntad', 'Enfoque directo');
    this.playCelebrationSound();
    this.triggerConfetti();
    this.showToast('¡Resististe con éxito el impulso! +25 XP 💪', 'success');
    this.refreshCurrentView();
  },

  // ----------------------------------------------------
  // REINICIO COMPASIVO / RECAÍDA
  // ----------------------------------------------------
  openRelapseModal() {
    const modal = document.getElementById('relapse-modal');
    if (modal) {
      modal.classList.remove('hidden');
      modal.classList.add('flex');
    }
  },

  closeRelapseModal() {
    const modal = document.getElementById('relapse-modal');
    if (modal) {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    }
  },

  handleRelapseSubmit(e) {
    e.preventDefault();
    const reason = document.getElementById('relapse-reason').value;
    const reflection = document.getElementById('relapse-reflection').value;

    Recovery.recordRelapse(reason, reflection);
    this.closeRelapseModal();
    this.showToast('Contador reiniciado con actitud de aprendizaje. ¡Tu progreso continúa! 🚀', 'info');
    this.refreshCurrentView();
  },

  // ----------------------------------------------------
  // SELECCIONAR ADICCIÓN Y GENERAR PLAN
  // ----------------------------------------------------
  openNewPlanModal() {
    const modal = document.getElementById('new-plan-modal');
    if (!modal) return;

    this.renderAddictionCatalog();
    modal.classList.remove('hidden');
    modal.classList.add('flex');
  },

  closeNewPlanModal() {
    const modal = document.getElementById('new-plan-modal');
    if (modal) {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    }
  },

  renderAddictionCatalog() {
    const grid = document.getElementById('addiction-options-grid');
    if (!grid) return;

    const catalog = Recovery.getCatalog();
    const keys = Object.keys(catalog);

    grid.innerHTML = keys.map(key => {
      const item = catalog[key];
      const isSelected = item.id === this.selectedAddictionOptionId;

      return `
        <div onclick="UI.selectAddictionCard('${item.id}')" 
          class="cursor-pointer p-3.5 rounded-2xl border transition-all ${isSelected ? 'border-indigo-600 bg-indigo-50/70 dark:bg-indigo-950/60 shadow-sm ring-2 ring-indigo-500/20' : 'border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-800/40 hover:border-slate-300'}">
          <div class="flex items-center gap-2.5 mb-1">
            <span class="text-2xl">${item.icon}</span>
            <h5 class="text-xs font-bold text-slate-900 dark:text-white">${item.name}</h5>
          </div>
          <p class="text-[11px] text-slate-500 dark:text-slate-400 leading-snug line-clamp-2">
            ${item.tagline}
          </p>
        </div>
      `;
    }).join('');

    this.updateCostField();
  },

  selectAddictionCard(id) {
    this.selectedAddictionOptionId = id;
    this.renderAddictionCatalog();
  },

  updateCostField() {
    const catalog = Recovery.getCatalog();
    const item = catalog[this.selectedAddictionOptionId];
    if (!item) return;

    const label = document.getElementById('new-plan-cost-label');
    const input = document.getElementById('new-plan-cost-input');

    if (label) {
      label.textContent = item.unitType === 'hours' 
        ? 'Horas diarias estimadas que te consumía:' 
        : 'Costo diario promedio en dinero ($):';
    }
    if (input) {
      input.value = item.defaultDailyValue;
      input.max = item.unitType === 'hours' ? 24 : 1000;
    }
  },

  handleNewPlanSubmit(e) {
    e.preventDefault();
    const why = document.getElementById('new-plan-why').value.trim();
    const amount = Number(document.getElementById('new-plan-cost-input').value) || 4;

    Recovery.createPlan({
      addictionId: this.selectedAddictionOptionId,
      whyReason: why,
      dailyCostOrHours: amount
    });

    this.closeNewPlanModal();
    this.playCelebrationSound();
    this.triggerConfetti();
    this.showToast('¡Plan personalizado de superación generado con éxito!', 'success');
    this.switchTab('today');
  },

  // ----------------------------------------------------
  // AJUSTES
  // ----------------------------------------------------
  renderSettingsView() {
    const data = Storage.get();
    const sound = document.getElementById('setting-sound-toggle');
    const confetti = document.getElementById('setting-confetti-toggle');
    const name = document.getElementById('setting-user-name');

    if (sound) sound.checked = data.settings?.soundEnabled !== false;
    if (confetti) confetti.checked = data.settings?.confettiEnabled !== false;
    if (name) name.value = data.settings?.userName || '';
  },

  saveSettingsForm() {
    const data = Storage.get();
    const sound = document.getElementById('setting-sound-toggle');
    const confetti = document.getElementById('setting-confetti-toggle');
    const name = document.getElementById('setting-user-name');

    if (!data.settings) data.settings = {};
    if (sound) data.settings.soundEnabled = sound.checked;
    if (confetti) data.settings.confettiEnabled = confetti.checked;
    if (name) data.settings.userName = name.value.trim() || 'Usuario';

    Storage.save(data);
    this.showToast('Configuraciones guardadas', 'success');
  },

  bindEvents() {
    // Fecha en Hoy
    const picker = document.getElementById('today-date-picker');
    if (picker) {
      picker.addEventListener('change', (e) => {
        if (e.target.value) {
          this.selectedTodayDateStr = e.target.value;
          this.renderTodayView();
        }
      });
    }

    const prevBtn = document.getElementById('today-prev-day-btn');
    if (prevBtn) {
      prevBtn.addEventListener('click', () => {
        const cur = parseDate(this.selectedTodayDateStr);
        this.selectedTodayDateStr = formatDate(addDays(cur, -1));
        this.renderTodayView();
      });
    }

    const nextBtn = document.getElementById('today-next-day-btn');
    if (nextBtn) {
      nextBtn.addEventListener('click', () => {
        const cur = parseDate(this.selectedTodayDateStr);
        this.selectedTodayDateStr = formatDate(addDays(cur, 1));
        this.renderTodayView();
      });
    }

    const shortcutBtn = document.getElementById('today-shortcut-btn');
    if (shortcutBtn) {
      shortcutBtn.addEventListener('click', () => {
        this.selectedTodayDateStr = formatDate(new Date());
        this.renderTodayView();
      });
    }

    // Formularios
    const newPlanForm = document.getElementById('new-plan-form');
    if (newPlanForm) {
      newPlanForm.addEventListener('submit', (e) => this.handleNewPlanSubmit(e));
    }

    const relapseForm = document.getElementById('relapse-form');
    if (relapseForm) {
      relapseForm.addEventListener('submit', (e) => this.handleRelapseSubmit(e));
    }

    // Respaldo
    const exportBtn = document.getElementById('backup-export-btn');
    if (exportBtn) {
      exportBtn.addEventListener('click', () => {
        Storage.exportJSON();
        this.showToast('Respaldo descargado', 'success');
      });
    }

    const importFile = document.getElementById('backup-import-file');
    if (importFile) {
      importFile.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (event) => {
          const res = Storage.importJSON(event.target.result);
          if (res.success) {
            this.showToast('¡Datos restaurados con éxito!', 'success');
            this.refreshCurrentView();
          } else {
            alert('Error al importar: ' + res.error);
          }
        };
        reader.readAsText(file);
      });
    }

    const resetDemoBtn = document.getElementById('backup-reset-demo-btn');
    if (resetDemoBtn) {
      resetDemoBtn.addEventListener('click', () => {
        if (confirm('¿Restablecer datos de demostración de superación de adicción?')) {
          Storage.reset();
          this.showToast('Datos demo restaurados', 'success');
          this.refreshCurrentView();
        }
      });
    }

    const clearAllBtn = document.getElementById('backup-clear-all-btn');
    if (clearAllBtn) {
      clearAllBtn.addEventListener('click', () => {
        if (confirm('ATENCIÓN: ¿Seguro que deseas eliminar todos los datos e historial?')) {
          Storage.clearAll();
          this.showToast('Datos eliminados', 'danger');
          this.refreshCurrentView();
        }
      });
    }
  }
};
