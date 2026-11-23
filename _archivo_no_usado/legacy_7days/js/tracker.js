/**
 * HabitFlow: Recovery - tracker.js
 * Registro de sobriedad diaria, hábitos de reemplazo, cálculo de rachas limpias y gamificación.
 */

const Tracker = {
  // Obtener registro de un día específico
  getDaySummary(dateStr) {
    const data = Storage.get();
    const log = (data.dailyLogs && data.dailyLogs[dateStr]) || {
      clean: true,
      cravingLevel: 'none',
      cravingsResisted: 0,
      notes: '',
      replacements: {}
    };

    const plan = data.currentPlan;
    const replacements = plan ? plan.dailyReplacements : [];

    let completedReplacements = 0;
    const replacementsWithStatus = replacements.map(rep => {
      const isDone = Boolean(log.replacements && log.replacements[rep.id]);
      if (isDone) completedReplacements++;
      return {
        ...rep,
        completed: isDone
      };
    });

    const totalReps = replacements.length;
    const repRate = totalReps > 0 ? Math.round((completedReplacements / totalReps) * 100) : 100;

    return {
      dateStr,
      clean: Boolean(log.clean),
      cravingLevel: log.cravingLevel || 'none',
      cravingsResisted: Number(log.cravingsResisted) || 0,
      notes: log.notes || '',
      replacements: replacementsWithStatus,
      completedReplacements,
      totalReplacements: totalReps,
      replacementRate: repRate
    };
  },

  // Guardar notas o nivel de ansiedad para un día
  setDayNotesAndCraving(dateStr, cravingLevel, notes) {
    const data = Storage.get();
    if (!data.dailyLogs) data.dailyLogs = {};
    if (!data.dailyLogs[dateStr]) {
      data.dailyLogs[dateStr] = {
        clean: true,
        cravingLevel: 'none',
        cravingsResisted: 0,
        notes: '',
        replacements: {}
      };
    }

    data.dailyLogs[dateStr].cravingLevel = cravingLevel;
    data.dailyLogs[dateStr].notes = notes.trim();
    Storage.save(data);
  },

  // Estadísticas globales de recuperación y sobriedad
  getGlobalStats() {
    const data = Storage.get();
    const plan = data.currentPlan;
    const today = new Date();

    if (!plan) {
      return {
        currentStreak: 0,
        bestStreak: 0,
        totalCravingsResisted: 0,
        totalRelapses: 0,
        savedAmount: 0,
        gamification: {
          totalXP: 0,
          levelNum: 1,
          levelTitle: 'Decidido a Cambiar 🌱',
          levelProgress: 0
        },
        triggerStats: {},
        last14Days: []
      };
    }

    // Calcular racha actual desde el último reinicio
    const cleanTime = Recovery.getCleanTime();
    const currentStreak = cleanTime ? cleanTime.days : 0;

    // Calcular total de tentaciones resistidas en el historial
    let totalCravingsResisted = 0;
    let totalRelapses = 0;
    const triggerCounts = {};

    if (data.eventsLog) {
      data.eventsLog.forEach(ev => {
        if (ev.type === 'craving_resisted') {
          totalCravingsResisted++;
          const trig = ev.trigger || 'Otros';
          triggerCounts[trig] = (triggerCounts[trig] || 0) + 1;
        } else if (ev.type === 'relapse') {
          totalRelapses++;
        }
      });
    }

    // Historial de los últimos 14 días
    const last14Days = [];
    for (let i = 13; i >= 0; i--) {
      const d = addDays(today, -i);
      const dStr = formatDate(d);
      const summary = this.getDaySummary(dStr);
      last14Days.push({
        dateStr: dStr,
        dayLabel: d.toLocaleDateString('es-ES', { weekday: 'short' }),
        clean: summary.clean,
        cravings: summary.cravingsResisted,
        repRate: summary.replacementRate
      });
    }

    // Gamificación: cálculo de XP y Nivel de Fortaleza Mental
    const totalXP = data.xp || 100;

    let levelTitle = 'Decidido a Cambiar 🌱';
    let levelNum = 1;
    let nextXP = 200;
    let prevXP = 0;

    if (totalXP >= 1500) {
      levelTitle = 'Maestro de la Libertad 👑';
      levelNum = 5;
      nextXP = 3000;
      prevXP = 1500;
    } else if (totalXP >= 700) {
      levelTitle = 'Mente Inquebrantable ⚡';
      levelNum = 4;
      nextXP = 1500;
      prevXP = 700;
    } else if (totalXP >= 350) {
      levelTitle = 'Guerrero Firme 🛡️';
      levelNum = 3;
      nextXP = 700;
      prevXP = 350;
    } else if (totalXP >= 150) {
      levelTitle = 'Iniciado Valiente 🚀';
      levelNum = 2;
      nextXP = 350;
      prevXP = 150;
    }

    const levelProgress = Math.min(100, Math.round(((totalXP - prevXP) / (nextXP - prevXP)) * 100));

    return {
      currentStreak,
      bestStreak: Math.max(currentStreak, 6),
      totalCravingsResisted,
      totalRelapses,
      savedAmount: cleanTime ? cleanTime.savedAmount : 0,
      unitType: plan.unitType,
      impactLabel: plan.impactLabel,
      gamification: {
        totalXP,
        levelNum,
        levelTitle,
        levelProgress,
        nextXP
      },
      triggerCounts,
      last14Days
    };
  }
};
