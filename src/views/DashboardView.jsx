/**
 * ============================================================================
 * VISTA 1 BLACK BOX: DASHBOARD Y CALENDARIO DE 21 DIAS (`DashboardView`)
 * ============================================================================
 * Renderiza el habito activo, selector de magnitud, Planta Virtual SVG, tarjeta
 * de proximo paso y cuadricula secuencial de 21 dias en 3 etapas.
 */
const DashboardView = ({
  appState,
  globalPercentage,
  completedDaysCount,
  plantPhase,
  currentMagnitudeConfig,
  isDayLocked,
  isDayCompleted,
  onSelectDay,
  onGoToMetrics,
  onOpenConfig,
  onChangeMagnitude
}) => {
  const currentActiveDay = Math.min(21, (Math.max(0, ...appState.completedDays) + 1));

  return (
    <div className="space-y-8">
      {/* HABIT BANNER & HERO CARD */}
      <div className="bg-gradient-to-r from-slate-900 via-brand-950 to-slate-900 text-white rounded-3xl p-6 sm:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute right-0 top-0 w-96 h-96 bg-brand-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-3 max-w-2xl">
            <div className="flex flex-wrap items-center gap-2">
              <span className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-brand-500/20 text-brand-200 border border-brand-500/30 text-xs font-semibold">
                <span className="w-2 h-2 rounded-full bg-growth-400 animate-ping"></span>
                Hábito Activo Seleccionado
              </span>

              {/* Magnitude Chip */}
              <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold border ${currentMagnitudeConfig.badgeBg}`}>
                {currentMagnitudeConfig.icon} Magnitud: {currentMagnitudeConfig.shortName}
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-white leading-tight">
              {appState.activeHabit}
            </h1>

            <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
              {currentMagnitudeConfig.description}
            </p>

            {/* Magnitude Selector Bar right on Hero */}
            <div className="pt-2 flex flex-wrap items-center gap-2">
              <span className="text-xs text-slate-400 font-semibold mr-1">Calibrar Magnitud:</span>
              {['chico', 'mediano', 'grande'].map(magKey => {
                const magObj = HABIT_MAGNITUDES[magKey];
                const isSelected = appState.habitMagnitude === magKey;
                return (
                  <button
                    key={magKey}
                    type="button"
                    onClick={() => onChangeMagnitude(magKey)}
                    className={`text-xs px-3 py-1.5 rounded-xl font-bold border transition-all flex items-center gap-1.5 ${
                      isSelected
                        ? 'bg-white text-slate-900 border-white shadow-sm'
                        : 'bg-white/10 hover:bg-white/20 text-slate-300 border-white/10'
                    }`}
                  >
                    <span>{magObj.icon}</span>
                    <span>{magObj.shortName}</span>
                    <span className="text-[10px] opacity-75 font-mono">({magObj.timeRangeLabel.split(' ')[0]}-{magObj.timeRangeLabel.split(' ')[2]}m)</span>
                  </button>
                );
              })}

              <button
                type="button"
                onClick={onOpenConfig}
                className="text-xs px-3 py-1.5 rounded-xl bg-brand-600/60 hover:bg-brand-600 text-white font-semibold flex items-center gap-1 transition-all"
                title="Editar nombre y detalles del hábito"
              >
                <Icons.Settings /> Editar Nombre
              </button>
            </div>
          </div>

          {/* Quick Action Button */}
          <div className="flex flex-col sm:flex-row md:flex-col gap-3 shrink-0">
            <button
              type="button"
              onClick={() => onSelectDay(currentActiveDay)}
              className="px-6 py-3.5 rounded-2xl bg-growth-500 hover:bg-growth-600 text-white font-bold text-sm shadow-lg hover:shadow-growth-500/25 transition-all flex items-center justify-center gap-2"
            >
              <Icons.Play /> Continuar Día {currentActiveDay}
            </button>

            <button
              type="button"
              onClick={onGoToMetrics}
              className="px-5 py-2.5 rounded-xl bg-white/10 hover:bg-white/15 text-slate-200 font-semibold text-xs transition-all text-center"
            >
              Ver Métricas Objetivas
            </button>
          </div>
        </div>
      </div>

      {/* TWO COLUMNS: VIRTUAL PLANT & GLOBAL PROGRESS */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Col: Virtual Plant Evolution (5 cols) */}
        <div className="lg:col-span-5 bg-white rounded-3xl p-6 border border-slate-200 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <span className="p-2 rounded-xl bg-growth-50 text-growth-700">
                  <Icons.Leaf />
                </span>
                <div>
                  <h3 className="font-bold text-slate-900 text-base">Planta Virtual de Hábito</h3>
                  <p className="text-xs text-slate-500">Evolución biológica por constancia</p>
                </div>
              </div>
              <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-growth-100 text-growth-800 border border-growth-200">
                Fase {plantPhase.phase} de 5
              </span>
            </div>

            {/* SVG Visual */}
            <VirtualPlantVisual phase={plantPhase.phase} />

            {/* Botanical Details */}
            <div className="mt-4 text-center">
              <h4 className="text-lg font-extrabold text-slate-800">{plantPhase.name}</h4>
              <p className="text-xs font-semibold text-growth-600 mb-2">{plantPhase.range} • {completedDaysCount} de 21 días</p>
              <p className="text-xs text-slate-600 italic bg-slate-50 rounded-xl p-3 border border-slate-100">
                {plantPhase.description}
              </p>
            </div>
          </div>

          <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500 font-medium">
            <span>Progreso: {globalPercentage}%</span>
            <span className="text-brand-600 font-semibold">{plantPhase.quote}</span>
          </div>
        </div>

        {/* Right Col: Global Progress & Featured Next Step (7 cols) */}
        <div className="lg:col-span-7 flex flex-col gap-6">
          {/* Progress Summary Card */}
          <div className="bg-white rounded-3xl p-6 border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="font-bold text-slate-900 text-base">Progreso General del Programa</h3>
                <p className="text-xs text-slate-500">
                  Calendario activo: <span className="font-semibold text-slate-800">{currentMagnitudeConfig.name}</span>
                </p>
              </div>
              <div className="text-right">
                <span className="text-2xl font-black text-brand-600">{globalPercentage}%</span>
                <p className="text-[11px] text-slate-400 font-medium">{completedDaysCount} / 21 Días</p>
              </div>
            </div>

            {/* Big Progress Bar */}
            <div className="w-full bg-slate-100 h-4 rounded-full overflow-hidden p-0.5 border border-slate-200">
              <div 
                className="h-full bg-gradient-to-r from-brand-500 via-indigo-500 to-growth-500 rounded-full transition-all duration-700 ease-out shadow-xs"
                style={{ width: `${globalPercentage}%` }}
              ></div>
            </div>

            {/* Mini Stats Grid */}
            <div className="grid grid-cols-3 gap-3 mt-6 pt-4 border-t border-slate-100 text-center">
              <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                <p className="text-xs text-slate-500 font-medium mb-1">Días Completos</p>
                <p className="text-xl font-bold text-slate-900">{completedDaysCount} <span className="text-xs text-slate-400 font-normal">/ 21</span></p>
              </div>
              <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                <p className="text-xs text-slate-500 font-medium mb-1">Minutos Foco</p>
                <p className="text-xl font-bold text-brand-600">{appState.metrics.focusMinutes || 0} <span className="text-xs text-slate-400 font-normal">min</span></p>
              </div>
              <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                <p className="text-xs text-slate-500 font-medium mb-1">Día Actual</p>
                <p className="text-xl font-bold text-growth-600">Día {currentActiveDay}</p>
              </div>
            </div>
          </div>

          {/* FEATURED CARD: "Mi Próximo Paso" (Day 9) */}
          <div className="bg-gradient-to-br from-brand-50/70 via-white to-growth-50/40 rounded-3xl p-6 border border-brand-200 shadow-sm relative overflow-hidden">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <span className="p-2 rounded-xl bg-brand-100 text-brand-700">
                  <Icons.Sparkles />
                </span>
                <div>
                  <h4 className="font-bold text-slate-900 text-sm">Tarjeta Destacada: Mi Próximo Paso</h4>
                  <p className="text-[11px] text-slate-500">Anticipación para eliminar fricción</p>
                </div>
              </div>
              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-white text-slate-600 border border-slate-200">
                Día 9
              </span>
            </div>

            {appState.featuredNextStep ? (
              <div className="space-y-3 bg-white p-4 rounded-2xl border border-brand-100 shadow-2xs">
                <div>
                  <span className="text-[11px] font-bold uppercase tracking-wider text-brand-600">Acción Inmediata:</span>
                  <p className="text-sm font-semibold text-slate-800 mt-0.5">{appState.featuredNextStep.step}</p>
                </div>
                <div>
                  <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">Materiales Requeridos:</span>
                  <p className="text-xs text-slate-600 mt-0.5">{appState.featuredNextStep.materials}</p>
                </div>
              </div>
            ) : (
              <div className="bg-white/80 p-4 rounded-2xl border border-dashed border-slate-200 text-center">
                <p className="text-xs text-slate-500 italic">
                  Completa el Día 9 para desbloquear tu tarjeta destacada con tu próximo paso inmediato y materiales listos.
                </p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* 21-DAY CALENDAR GRID CONDITIONED BY MAGNITUDE */}
      <div className="space-y-8 pt-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-200 pb-4">
          <div>
            <div className="flex items-center gap-2">
              <h2 className="text-xl sm:text-2xl font-bold text-slate-900">Calendario Condicionado de los 21 Días</h2>
              <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full border ${currentMagnitudeConfig.badgeBg}`}>
                {currentMagnitudeConfig.icon} {currentMagnitudeConfig.name}
              </span>
            </div>
            <p className="text-xs sm:text-sm text-slate-500 mt-1">
              Lapsos de tiempo adaptados a la complejidad del hábito ({currentMagnitudeConfig.timeRangeLabel}). Desbloqueo secuencial estricto.
            </p>
          </div>
          <div className="flex items-center gap-3 text-xs font-semibold">
            <span className="flex items-center gap-1.5 text-slate-400"><Icons.Lock /> Bloqueado</span>
            <span className="flex items-center gap-1.5 text-brand-600"><span className="w-2 h-2 rounded-full bg-brand-500"></span> En curso</span>
            <span className="flex items-center gap-1.5 text-growth-600"><Icons.Check /> Completado</span>
          </div>
        </div>

        {/* Stages Grid */}
        {PROGRAM_STAGES.map(stage => {
          const stageDays = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21]
            .filter(d => (d <= 7 ? 1 : d <= 14 ? 2 : 3) === stage.id)
            .map(d => getDayConfig(d, appState.habitMagnitude));

          return (
            <div key={stage.id} className="space-y-4">
              <div className="flex items-center justify-between bg-slate-100/70 px-4 py-2.5 rounded-2xl border border-slate-200">
                <span className="font-bold text-slate-800 text-sm">{stage.name}</span>
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-white text-slate-600 border border-slate-200">
                  {stage.days}
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-7 gap-3.5">
                {stageDays.map(item => {
                  const locked = isDayLocked(item.day);
                  const completed = isDayCompleted(item.day);
                  const isCurrent = item.day === currentActiveDay;

                  return (
                    <div
                      key={item.day}
                      onClick={() => onSelectDay(item.day)}
                      className={`group relative rounded-2xl p-4 border transition-all duration-200 flex flex-col justify-between cursor-pointer ${
                        locked 
                          ? 'bg-slate-50/80 border-slate-200 text-slate-400 hover:border-slate-300' 
                          : completed
                            ? 'bg-white border-growth-200 hover:border-growth-400 hover:shadow-md'
                            : isCurrent
                              ? 'bg-white border-brand-500 shadow-md ring-2 ring-brand-500/20'
                              : 'bg-white border-slate-200 hover:border-brand-300 hover:shadow-sm'
                      }`}
                    >
                      <div className="flex items-center justify-between mb-2">
                        <span className={`text-xs font-bold px-2 py-0.5 rounded-lg ${
                          completed 
                            ? 'bg-growth-100 text-growth-800' 
                            : isCurrent 
                              ? 'bg-brand-100 text-brand-800' 
                              : 'bg-slate-100 text-slate-500'
                        }`}>
                          Día {item.day}
                        </span>

                        <div className="text-slate-400">
                          {locked ? (
                            <Icons.Lock />
                          ) : completed ? (
                            <span className="text-growth-600 font-bold"><Icons.Check /></span>
                          ) : (
                            <span className="w-2.5 h-2.5 rounded-full bg-brand-500 animate-pulse"></span>
                          )}
                        </div>
                      </div>

                      <div className="my-1">
                        <h4 className={`text-xs font-semibold leading-tight line-clamp-2 ${
                          locked ? 'text-slate-400' : 'text-slate-800 group-hover:text-brand-600'
                        }`}>
                          {item.title}
                        </h4>
                      </div>

                      <div className="mt-3 pt-2 border-t border-slate-100 flex items-center justify-between text-[11px]">
                        <span className="font-mono text-slate-500 flex items-center gap-1 font-semibold">
                          <Icons.Clock /> {item.duration}m
                        </span>
                        <span className="font-medium text-slate-400 group-hover:text-slate-700">
                          {completed ? 'Ver' : locked ? 'Bloq.' : 'Iniciar'}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

// ==========================================
// VIEW 2: PANTALLA DE ACTIVIDAD DIARIA
// ==========================================

window.DashboardView = DashboardView;
