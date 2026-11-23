/**
 * HabitFlow: Recovery - stats.js
 * Visualización gráfica de sobriedad, análisis de disparadores (triggers)
 * y estadísticas de recuperación con Chart.js.
 */

const Stats = {
  charts: {
    history: null,
    triggers: null,
    cravingsWeek: null
  },

  render() {
    const stats = Tracker.getGlobalStats();
    this.renderMetricCards(stats);
    this.renderCharts(stats);
  },

  renderMetricCards(stats) {
    const elCurrent = document.getElementById('stat-current-clean');
    const elBest = document.getElementById('stat-best-clean');
    const elCravings = document.getElementById('stat-total-cravings');
    const elSaved = document.getElementById('stat-total-saved');
    const elSavedLabel = document.getElementById('stat-saved-label');

    if (elCurrent) elCurrent.textContent = `${stats.currentStreak} días`;
    if (elBest) elBest.textContent = `${stats.bestStreak} días`;
    if (elCravings) elCravings.textContent = `${stats.totalCravingsResisted}`;

    if (elSaved) {
      if (stats.unitType === 'hours') {
        elSaved.textContent = `${stats.savedAmount} hrs`;
      } else {
        elSaved.textContent = `$${stats.savedAmount}`;
      }
    }
    if (elSavedLabel) elSavedLabel.textContent = stats.impactLabel || 'Impacto positivo acumulado';

    // Gamificación
    const elLevelBadge = document.getElementById('stat-level-badge');
    const elLevelTitle = document.getElementById('stat-level-title');
    const elLevelXP = document.getElementById('stat-level-xp');
    const elLevelBar = document.getElementById('stat-level-bar');

    if (elLevelBadge) elLevelBadge.textContent = `Nivel ${stats.gamification.levelNum}`;
    if (elLevelTitle) elLevelTitle.textContent = stats.gamification.levelTitle;
    if (elLevelXP) elLevelXP.textContent = `${stats.gamification.totalXP} XP`;
    if (elLevelBar) elLevelBar.style.width = `${stats.gamification.levelProgress}%`;
  },

  renderCharts(stats) {
    if (typeof Chart === 'undefined') return;

    const isDark = document.documentElement.classList.contains('dark');
    const textColor = isDark ? '#94a3b8' : '#64748b';
    const gridColor = isDark ? 'rgba(51, 65, 85, 0.4)' : 'rgba(226, 232, 240, 0.8)';

    // 1. Gráfico de Hábitos de Reemplazo en los últimos 14 días
    const histCanvas = document.getElementById('chart-recovery-trend');
    if (histCanvas) {
      if (this.charts.history) this.charts.history.destroy();

      const labels = stats.last14Days.map(d => {
        const [y, m, day] = d.dateStr.split('-');
        return `${day}/${m}`;
      });
      const dataReps = stats.last14Days.map(d => d.repRate);

      this.charts.history = new Chart(histCanvas, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'Cumplimiento de Hábitos de Reemplazo (%)',
            data: dataReps,
            borderColor: '#10b981',
            backgroundColor: 'rgba(16, 185, 129, 0.1)',
            fill: true,
            tension: 0.3,
            borderWidth: 3,
            pointBackgroundColor: '#10b981',
            pointRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { display: false }
          },
          scales: {
            y: {
              beginAtZero: true,
              max: 100,
              grid: { color: gridColor },
              ticks: { color: textColor, callback: v => `${v}%` }
            },
            x: {
              grid: { display: false },
              ticks: { color: textColor }
            }
          }
        }
      });
    }

    // 2. Gráfico de Disparadores (Doughnut)
    const trigCanvas = document.getElementById('chart-triggers');
    if (trigCanvas) {
      if (this.charts.triggers) this.charts.triggers.destroy();

      const triggers = stats.triggerCounts || {};
      const labels = Object.keys(triggers);
      const dataVals = Object.values(triggers);

      const defaultLabels = labels.length > 0 ? labels : ['Aburrimiento', 'Estrés', 'Cansancio'];
      const defaultData = dataVals.length > 0 ? dataVals : [3, 2, 1];
      const colors = ['#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#10b981', '#ec4899'];

      this.charts.triggers = new Chart(trigCanvas, {
        type: 'doughnut',
        data: {
          labels: defaultLabels,
          datasets: [{
            data: defaultData,
            backgroundColor: colors.slice(0, defaultLabels.length),
            borderWidth: 2,
            borderColor: isDark ? '#1e293b' : '#ffffff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: textColor, boxWidth: 12, padding: 10, font: { size: 11 } }
            }
          },
          cutout: '65%'
        }
      });
    }
  }
};
