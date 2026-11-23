/**
 * HabitFlow: Recovery - calendar.js
 * Calendario interactivo mensual de sobriedad, visualización de días limpios
 * e inspección de tentaciones y hábitos de reemplazo por fecha.
 */

const Calendar = {
  currentDate: new Date(),
  selectedDateStr: formatDate(new Date()),

  init() {
    this.render();
  },

  prevMonth() {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1);
    this.render();
  },

  nextMonth() {
    this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1);
    this.render();
  },

  goToToday() {
    this.currentDate = new Date();
    this.selectedDateStr = formatDate(new Date());
    this.render();
    this.showDayDetail(this.selectedDateStr);
  },

  render() {
    const year = this.currentDate.getFullYear();
    const month = this.currentDate.getMonth();

    const monthNames = [
      'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
      'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
    ];
    const monthTitleEl = document.getElementById('calendar-month-title');
    if (monthTitleEl) {
      monthTitleEl.textContent = `${monthNames[month]} ${year}`;
    }

    const gridEl = document.getElementById('calendar-days-grid');
    if (!gridEl) return;
    gridEl.innerHTML = '';

    const firstDayIndex = new Date(year, month, 1).getDay(); // 0 = Domingo
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    const todayStr = formatDate(new Date());

    let cleanDaysCount = 0;
    let cravingsCount = 0;

    // Celdas del mes anterior
    for (let i = firstDayIndex - 1; i >= 0; i--) {
      const prevDate = new Date(year, month - 1, daysInPrevMonth - i);
      const cell = this.createDayCell(prevDate, true, todayStr);
      gridEl.appendChild(cell);
    }

    // Celdas del mes actual
    for (let day = 1; day <= daysInMonth; day++) {
      const dateObj = new Date(year, month, day);
      const dateStr = formatDate(dateObj);
      const summary = Tracker.getDaySummary(dateStr);

      if (summary.clean) cleanDaysCount++;
      cravingsCount += summary.cravingsResisted;

      const cell = this.createDayCell(dateObj, false, todayStr, summary);
      gridEl.appendChild(cell);
    }

    // Celdas del mes siguiente
    const totalCells = firstDayIndex + daysInMonth;
    const remaining = (7 - (totalCells % 7)) % 7;
    for (let j = 1; j <= remaining; j++) {
      const nextDate = new Date(year, month + 1, j);
      const cell = this.createDayCell(nextDate, true, todayStr);
      gridEl.appendChild(cell);
    }

    // Actualizar métricas del mes
    const elClean = document.getElementById('cal-stat-clean-days');
    const elCravings = document.getElementById('cal-stat-cravings');
    if (elClean) elClean.textContent = `${cleanDaysCount} días`;
    if (elCravings) elCravings.textContent = `${cravingsCount} vencidas`;
  },

  createDayCell(dateObj, isOtherMonth, todayStr, summary = null) {
    const dateStr = formatDate(dateObj);
    const dayNum = dateObj.getDate();
    const isToday = dateStr === todayStr;
    const isSelected = dateStr === this.selectedDateStr;

    const cell = document.createElement('button');
    cell.type = 'button';
    cell.className = 'calendar-day-cell relative flex flex-col items-center justify-between p-2 rounded-xl border text-left transition-all duration-150 focus:outline-none';

    if (isOtherMonth) {
      cell.classList.add('opacity-25', 'border-transparent', 'bg-slate-100/50', 'dark:bg-slate-800/20');
      cell.innerHTML = `<span class="text-xs text-slate-400 font-medium">${dayNum}</span>`;
      cell.onclick = () => {
        this.currentDate = new Date(dateObj.getFullYear(), dateObj.getMonth(), 1);
        this.selectedDateStr = dateStr;
        this.render();
        this.showDayDetail(dateStr);
      };
      return cell;
    }

    if (!summary) summary = Tracker.getDaySummary(dateStr);

    let bgColor = 'bg-white dark:bg-slate-800/60';
    let borderColor = 'border-slate-200 dark:border-slate-700/60';
    let badgeHtml = '';

    if (isToday) {
      borderColor = 'border-indigo-500 ring-2 ring-indigo-500/30';
    }

    if (isSelected) {
      cell.classList.add('shadow-md');
      borderColor = 'border-emerald-500 dark:border-emerald-400';
    }

    if (summary.clean) {
      if (summary.cravingsResisted > 0) {
        // Día limpio con tentación superada
        bgColor = 'bg-emerald-50 dark:bg-emerald-950/40 border-emerald-300 dark:border-emerald-700/70';
        badgeHtml = `
          <div class="w-full flex items-center justify-between text-[11px] font-bold text-emerald-600 dark:text-emerald-400">
            <span>🛡️ ${summary.cravingsResisted}</span>
            <span class="text-[10px] bg-emerald-200/70 dark:bg-emerald-800/50 px-1 rounded">Limpio</span>
          </div>`;
      } else {
        // Día limpio normal
        bgColor = 'bg-teal-50/50 dark:bg-teal-950/20 border-teal-200 dark:border-teal-800/40';
        badgeHtml = `
          <div class="w-full flex items-center justify-between text-[11px] font-semibold text-teal-600 dark:text-teal-400">
            <span>✓ Limpio</span>
          </div>`;
      }
    } else {
      // Recaída
      bgColor = 'bg-rose-50/60 dark:bg-rose-950/30 border-rose-200 dark:border-rose-900/50';
      badgeHtml = `
        <div class="w-full flex items-center justify-between text-[11px] font-bold text-rose-500">
          <span>Recaída</span>
        </div>`;
    }

    cell.className += ` ${bgColor} ${borderColor}`;
    cell.innerHTML = `
      <div class="w-full flex items-center justify-between">
        <span class="text-xs font-semibold ${isToday ? 'text-indigo-600 dark:text-indigo-400 font-bold' : 'text-slate-700 dark:text-slate-300'}">
          ${dayNum} ${isToday ? '•' : ''}
        </span>
        ${summary.clean ? '<span class="text-xs">🛡️</span>' : ''}
      </div>
      ${badgeHtml}
    `;

    cell.onclick = () => {
      this.selectedDateStr = dateStr;
      this.render();
      this.showDayDetail(dateStr);
    };

    return cell;
  },

  // Mostrar detalle de un día
  showDayDetail(dateStr) {
    this.selectedDateStr = dateStr;
    const dateObj = parseDate(dateStr);
    const summary = Tracker.getDaySummary(dateStr);

    const detailPanel = document.getElementById('calendar-day-detail');
    if (!detailPanel) return;

    const options = { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };
    const dateTitle = dateObj.toLocaleDateString('es-ES', options);

    const replacementsListHtml = summary.replacements.map(rep => `
      <div class="flex items-center justify-between p-2.5 rounded-xl border border-slate-100 dark:border-slate-700/50 bg-slate-50 dark:bg-slate-800/50">
        <div class="flex items-center gap-2">
          <span>${rep.icon}</span>
          <span class="text-xs font-semibold text-slate-800 dark:text-slate-200 ${rep.completed ? 'line-through text-slate-400' : ''}">${rep.title}</span>
        </div>
        <input type="checkbox" ${rep.completed ? 'checked' : ''} 
          onchange="Calendar.handleToggleReplacement('${rep.id}', '${dateStr}')" 
          class="habit-checkbox" />
      </div>
    `).join('');

    detailPanel.innerHTML = `
      <div class="bg-white dark:bg-slate-800/90 rounded-2xl p-5 border border-slate-200 dark:border-slate-700/60 shadow-sm space-y-4">
        <div class="flex items-center justify-between pb-3 border-b border-slate-100 dark:border-slate-700/50">
          <div>
            <span class="text-[10px] font-bold uppercase tracking-wider text-emerald-500">Historial Diario</span>
            <h4 class="text-sm font-bold text-slate-900 dark:text-white capitalize">${dateTitle}</h4>
          </div>
          <span class="text-xs font-extrabold px-2.5 py-1 rounded-full ${summary.clean ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-950/80 dark:text-emerald-300' : 'bg-rose-100 text-rose-700 dark:bg-rose-950/80 dark:text-rose-300'}">
            ${summary.clean ? 'Día Limpio 🛡️' : 'Recaída 🔄'}
          </span>
        </div>

        <!-- Métricas del día -->
        <div class="grid grid-cols-2 gap-2 text-center text-xs">
          <div class="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700">
            <span class="text-slate-400 block text-[10px]">Tentaciones Vencidas</span>
            <span class="font-bold text-emerald-500 text-base">${summary.cravingsResisted}</span>
          </div>
          <div class="p-2.5 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700">
            <span class="text-slate-400 block text-[10px]">Hábitos de Reemplazo</span>
            <span class="font-bold text-indigo-500 text-base">${summary.completedReplacements}/${summary.totalReplacements}</span>
          </div>
        </div>

        <!-- Hábitos de reemplazo del día -->
        <div class="space-y-2">
          <span class="text-xs font-bold text-slate-700 dark:text-slate-300 block">Hábitos de reemplazo:</span>
          ${replacementsListHtml}
        </div>

        ${summary.notes ? `
          <div class="p-3 rounded-xl bg-indigo-50/70 dark:bg-indigo-950/30 border border-indigo-100 dark:border-indigo-900/40 text-xs">
            <span class="font-bold text-indigo-700 dark:text-indigo-300 block mb-0.5">Reflexión guardada:</span>
            <p class="text-slate-700 dark:text-slate-300 italic">"${summary.notes}"</p>
          </div>
        ` : ''}
      </div>
    `;
  },

  handleToggleReplacement(repId, dateStr) {
    Recovery.toggleDailyReplacement(repId, dateStr);
    this.render();
    this.showDayDetail(dateStr);
    if (typeof UI !== 'undefined' && UI.refreshCurrentView) {
      UI.refreshCurrentView();
    }
  }
};
