/**
 * ============================================================================
 * VISTA 2 BLACK BOX: ACTIVIDAD DIARIA (`DailyActivityView`)
 * ============================================================================
 * Renderiza la guia CSV de micro-pasos, formularios especificos del Dia 1 al 21,
 * temporizador multi-bloque y validacion del 100% para desbloquear el Dia N+1.
 */
const { useState, useEffect, useRef, useMemo } = React;

const DailyActivityView = ({
  dayNumber,
  appState,
  csvCatalog = [],
  currentMagnitudeConfig,
  onUpdateField,
  onTimerComplete,
  onFinalizeDay,
  isCompleted,
  isLocked,
  calculateDayCompletion,
  onBackToDashboard,
  onNavigateDay,
  onChangeMagnitude
}) => {
  const config = getDayConfig(dayNumber, appState.habitMagnitude);
  const dayData = appState.dayData[dayNumber] || {};
  const dayPercentage = calculateDayCompletion(dayNumber);
  const readOnly = isCompleted;

  const day1Task = appState.dayData[1]?.taskName || appState.activeHabit;
  const day2Step1 = appState.dayData[2]?.step1 || 'Primer micro-paso ordenado';
  const [selectedCsvId, setSelectedCsvId] = useState(csvCatalog[0]?.id || 'aprender_programacion');
  const selectedCsvGoal = csvCatalog.find((g) => g.id === selectedCsvId) || csvCatalog[0] || null;

  return (
    <div className="max-w-4xl mx-auto space-y-6 pb-20">
      {/* TOP BREADCRUMB & DAY SELECTOR */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-white p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-2xs">
        <button
          type="button"
          onClick={onBackToDashboard}
          className="inline-flex items-center gap-2 text-xs sm:text-sm font-semibold text-slate-600 hover:text-slate-900 group"
        >
          <Icons.ArrowLeft />
          <span>Volver al Calendario</span>
        </button>

        <div className="flex items-center gap-2">
          <button
            type="button"
            disabled={dayNumber <= 1}
            onClick={() => onNavigateDay(dayNumber - 1)}
            className="p-2 rounded-xl text-xs font-semibold border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-30 disabled:cursor-not-allowed"
            title="Día anterior"
          >
            ◀ Día {dayNumber - 1}
          </button>

          <span className="text-xs font-bold px-3 py-1.5 rounded-xl bg-slate-900 text-white">
            Día {dayNumber} de 21
          </span>

          <button
            type="button"
            disabled={dayNumber >= 21}
            onClick={() => onNavigateDay(dayNumber + 1)}
            className="p-2 rounded-xl text-xs font-semibold border border-slate-200 text-slate-600 hover:bg-slate-50 disabled:opacity-30 disabled:cursor-not-allowed"
            title="Siguiente día"
          >
            Día {dayNumber + 1} ▶
          </button>
        </div>
      </div>

      {/* READ-ONLY BANNER IF COMPLETED */}
      {readOnly && (
        <div className="bg-growth-50 border border-growth-200 rounded-2xl p-4 flex items-center justify-between gap-3 text-growth-900">
          <div className="flex items-center gap-3">
            <span className="text-xl">🛡️</span>
            <div>
              <h4 className="text-xs sm:text-sm font-bold">Modo de Solo Consulta (Lectura)</h4>
              <p className="text-xs text-growth-700">Este día ya fue finalizado y guardado con éxito. Tus respuestas están protegidas.</p>
            </div>
          </div>
          <span className="text-xs font-bold px-3 py-1 rounded-full bg-growth-600 text-white shrink-0">
            100% Completado
          </span>
        </div>
      )}

      {/* HEADER CARD */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold uppercase tracking-wider text-brand-600 bg-brand-50 px-3 py-1 rounded-full border border-brand-100">
              Etapa {config.stage} • {PROGRAM_STAGES[config.stage - 1].name}
            </span>
            <span className={`text-xs font-bold px-2.5 py-0.5 rounded-full border ${currentMagnitudeConfig.badgeBg}`}>
              {currentMagnitudeConfig.icon} {currentMagnitudeConfig.shortName}
            </span>
          </div>

          <span className="text-xs font-mono font-bold px-3 py-1 rounded-full bg-slate-100 text-slate-700 flex items-center gap-1.5">
            <Icons.Clock /> Duración calibrada: {config.duration} minutos
          </span>
        </div>

        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 leading-tight">
          {config.title}
        </h1>

      {/* GUIA RAPIDA DE MICRO-PASOS DESDE CSV */}
      {selectedCsvGoal && (
        <div className="bg-white rounded-2xl p-4 sm:p-5 border border-slate-200 shadow-2xs space-y-3">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div>
              <h3 className="text-sm font-bold text-slate-900">Guia de micro-pasos (Catalogo CSV)</h3>
              <p className="text-xs text-slate-500">Elige una meta ejemplo para ver pasos concretos sin complicar tu sesion.</p>
            </div>
            <select
              value={selectedCsvGoal.id}
              onChange={(e) => setSelectedCsvId(e.target.value)}
              className="text-xs font-semibold px-3 py-2 rounded-xl border border-slate-200 bg-slate-50 text-slate-800"
            >
              {csvCatalog.map((item) => (
                <option key={item.id} value={item.id}>{item.meta}</option>
              ))}
            </select>
          </div>
          <ol className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs text-slate-700">
            {selectedCsvGoal.steps.map((step, idx) => (
              <li key={idx} className="p-2.5 rounded-xl bg-slate-50 border border-slate-200">
                <span className="font-bold text-brand-600 mr-1.5">{idx + 1}.</span>{step}
              </li>
            ))}
          </ol>
        </div>
      )}

        <p className="text-sm text-slate-600 leading-relaxed bg-slate-50 p-4 rounded-2xl border border-slate-100">
          💡 <span className="font-semibold text-slate-800">Propósito:</span> {config.purpose}
        </p>

        {/* Daily Dynamic Progress Bar */}
        <div className="space-y-1.5 pt-2">
          <div className="flex items-center justify-between text-xs font-semibold">
            <span className="text-slate-600">Progreso requerido de este día:</span>
            <span className={dayPercentage === 100 ? 'text-growth-600 font-bold' : 'text-brand-600'}>
              {dayPercentage}% ({dayPercentage === 100 ? 'Listo para guardar' : 'Pendiente'})
            </span>
          </div>
          <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden border border-slate-200">
            <div 
              className={`h-full rounded-full transition-all duration-300 ${
                dayPercentage === 100 ? 'bg-growth-500' : 'bg-brand-500'
              }`}
              style={{ width: `${dayPercentage}%` }}
            ></div>
          </div>
        </div>
      </div>

      {/* DYNAMIC FORM / ACTIVITIES SPECIFIC TO EACH DAY (1 TO 21) */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm space-y-6">
        <h3 className="text-lg font-bold text-slate-900 border-b border-slate-100 pb-3 flex items-center gap-2">
          <Icons.Target /> Acciones Requeridas para el Día {dayNumber}
        </h3>

        {/* DAY 1 */}
        {dayNumber === 1 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Nombre de la tarea o hábito a iniciar *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.taskName !== undefined ? dayData.taskName : appState.activeHabit}
                onChange={(e) => onUpdateField(1, 'taskName', e.target.value)}
                placeholder="Ej. Ordenar mi cuarto, Estudiar cálculo diferencial, Leer 1 capítulo"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 disabled:bg-slate-50"
              />
            </div>

            {/* Magnitude selector dropdown inside Day 1 registration */}
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 space-y-2">
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                2. Selecciona la Magnitud / Complejidad del Hábito * (Condiciona los lapsos del calendario)
              </label>
              <select
                disabled={readOnly}
                value={appState.habitMagnitude}
                onChange={(e) => onChangeMagnitude(e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm font-semibold bg-white focus:ring-2 focus:ring-brand-500"
              >
                <option value="chico">🟢 Chico: Baja complejidad / Tareas físicas rápidas (2 a 10 min por día)</option>
                <option value="mediano">🔵 Mediano: Complejidad moderada / Rutinas diarias (5 a 23 min por día)</option>
                <option value="grande">🟣 Grande: Alta demanda cognitiva / Estudio y tesis (5 a 30 min por día)</option>
              </select>
              <p className="text-xs text-slate-500">
                {currentMagnitudeConfig.description}
              </p>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Categoría de la tarea *
              </label>
              <select
                disabled={readOnly}
                value={dayData.category || ''}
                onChange={(e) => onUpdateField(1, 'category', e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 disabled:bg-slate-50"
              >
                <option value="">Selecciona una categoría...</option>
                <option value="Hogar">Hogar / Orden Personal</option>
                <option value="Escuela">Escuela / Universidad</option>
                <option value="Trabajo">Trabajo / Profesional</option>
                <option value="Personal">Personal / Salud y Bienestar</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                4. Primera acción mínima (fricción cero) *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.firstAction || ''}
                onChange={(e) => onUpdateField(1, 'firstAction', e.target.value)}
                placeholder="Ej. Recoger las prendas del suelo, Abrir el libro en la página 10"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm focus:ring-2 focus:ring-brand-500 disabled:bg-slate-50"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`5. Temporizador Calibrado para Hábito ${currentMagnitudeConfig.shortName} (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(1, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 2 */}
        {dayNumber === 2 && (
          <div className="space-y-5">
            <div className="p-3.5 bg-brand-50/60 rounded-xl border border-brand-100 text-xs text-brand-900">
              <span className="font-bold">Tarea del Día 1:</span> {day1Task}
            </div>

            <p className="text-xs text-slate-500">Divide tu tarea en tres micro-pasos ordenados cronológicamente:</p>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Paso 1 (Inmediato):</label>
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.step1 || ''}
                  onChange={(e) => onUpdateField(2, 'step1', e.target.value)}
                  placeholder="Ej. Despejar la superficie de la mesa / Reunir apuntes"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Paso 2 (Desarrollo):</label>
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.step2 || ''}
                  onChange={(e) => onUpdateField(2, 'step2', e.target.value)}
                  placeholder="Ej. Guardar objetos fuera de lugar / Resolver 3 problemas"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Paso 3 (Cierre):</label>
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.step3 || ''}
                  onChange={(e) => onUpdateField(2, 'step3', e.target.value)}
                  placeholder="Ej. Limpiar el polvo / Revisar respuestas con la guía"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm"
                />
              </div>
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Organizar y estructurar los pasos (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(2, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 3 */}
        {dayNumber === 3 && (
          <div className="space-y-5">
            <div className="p-3.5 bg-growth-50/70 rounded-xl border border-growth-200 text-xs text-growth-900">
              <span className="font-bold">Foco de hoy en Paso 1:</span> {day2Step1}
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Ejecución de ${config.duration} min en el Paso 1 (${currentMagnitudeConfig.shortName})`}
              onComplete={(mins) => onTimerComplete(3, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                ¿Lograste ejecutar el Paso 1 durante los {config.duration} minutos? *
              </label>
              <div className="flex gap-4">
                <label className="flex items-center gap-2 text-sm text-slate-800 cursor-pointer">
                  <input
                    type="radio"
                    name="step1Executed"
                    disabled={readOnly}
                    checked={dayData.step1Executed === 'yes'}
                    onChange={() => onUpdateField(3, 'step1Executed', 'yes')}
                    className="text-growth-600"
                  />
                  Sí, logré avanzar
                </label>
                <label className="flex items-center gap-2 text-sm text-slate-800 cursor-pointer">
                  <input
                    type="radio"
                    name="step1Executed"
                    disabled={readOnly}
                    checked={dayData.step1Executed === 'no'}
                    onChange={() => onUpdateField(3, 'step1Executed', 'no')}
                    className="text-amber-600"
                  />
                  No, me costó concentrarme
                </label>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Breve nota de reflexión o resultado *
              </label>
              <textarea
                rows="2"
                disabled={readOnly}
                value={dayData.reflection || ''}
                onChange={(e) => onUpdateField(3, 'reflection', e.target.value)}
                placeholder="¿Cómo se sintió romper la inercia inicial?"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>
          </div>
        )}

        {/* DAY 4 */}
        {dayNumber === 4 && (
          <div className="space-y-5">
            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-3">
              <span className="text-xs font-bold text-slate-700 uppercase tracking-wider">
                Checklist de 3 elementos de espacio y entorno:
              </span>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.cleanSpace}
                  onChange={(e) => onUpdateField(4, 'cleanSpace', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>1. <strong>Espacio despejado:</strong> Mesa o área limpia, sin objetos distractores.</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.readyMaterials}
                  onChange={(e) => onUpdateField(4, 'readyMaterials', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>2. <strong>Materiales listos:</strong> Herramientas, libreta o insumos necesarios a mano.</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.devicePrepared}
                  onChange={(e) => onUpdateField(4, 'devicePrepared', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>3. <strong>Dispositivo preparado:</strong> Celular en no molestar, pantallas distractoras apagadas.</span>
              </label>
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Acondicionamiento y prueba del entorno (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(4, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 5 */}
        {dayNumber === 5 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Distracción principal detectada *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.mainDistraction || ''}
                onChange={(e) => onUpdateField(5, 'mainDistraction', e.target.value)}
                placeholder="Ej. Redes sociales, ruido de la casa, levantarse por agua"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Regla de mitigación concreta *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.mitigationRule || ''}
                onChange={(e) => onUpdateField(5, 'mitigationRule', e.target.value)}
                placeholder="Ej. Dejar el celular en otra habitación durante los minutos de acción"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Sesión de prueba aplicando regla (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(5, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 6 */}
        {dayNumber === 6 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Registra 3 pendientes o áreas de trabajo *
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.pending1 || ''}
                  onChange={(e) => onUpdateField(6, 'pending1', e.target.value)}
                  placeholder="Pendiente A"
                  className="px-3 py-2 rounded-xl border border-slate-200 text-sm"
                />
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.pending2 || ''}
                  onChange={(e) => onUpdateField(6, 'pending2', e.target.value)}
                  placeholder="Pendiente B"
                  className="px-3 py-2 rounded-xl border border-slate-200 text-sm"
                />
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.pending3 || ''}
                  onChange={(e) => onUpdateField(6, 'pending3', e.target.value)}
                  placeholder="Pendiente C"
                  className="px-3 py-2 rounded-xl border border-slate-200 text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Selecciona la prioridad #1 absoluta *
              </label>
              <select
                disabled={readOnly}
                value={dayData.chosenPriority || ''}
                onChange={(e) => onUpdateField(6, 'chosenPriority', e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              >
                <option value="">Selecciona la prioridad más crítica...</option>
                {dayData.pending1 && <option value={dayData.pending1}>Pendiente A: {dayData.pending1}</option>}
                {dayData.pending2 && <option value={dayData.pending2}>Pendiente B: {dayData.pending2}</option>}
                {dayData.pending3 && <option value={dayData.pending3}>Pendiente C: {dayData.pending3}</option>}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Escribe el primer paso concreto de esta prioridad *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.firstStepPriority || ''}
                onChange={(e) => onUpdateField(6, 'firstStepPriority', e.target.value)}
                placeholder="Ej. Concluir la primera sección"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Trabajo enfocado en prioridad #1 (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(6, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 7 */}
        {dayNumber === 7 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Calificación de tu consistencia en la Semana 1 (1 a 5 estrellas) *
              </label>
              <div className="flex gap-2">
                {[1, 2, 3, 4, 5].map(star => (
                  <button
                    key={star}
                    type="button"
                    disabled={readOnly}
                    onClick={() => onUpdateField(7, 'week1Rating', star)}
                    className={`text-2xl p-2 rounded-xl border transition-all ${
                      (dayData.week1Rating || 0) >= star
                        ? 'bg-amber-100 border-amber-300 text-amber-500 scale-105'
                        : 'bg-slate-50 border-slate-200 text-slate-300'
                    }`}
                  >
                    ★
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. ¿Cuál fue tu mayor logro al vencer la resistencia inicial? *
              </label>
              <textarea
                rows="3"
                disabled={readOnly}
                value={dayData.biggestBreakthrough || ''}
                onChange={(e) => onUpdateField(7, 'biggestBreakthrough', e.target.value)}
                placeholder="Ej. Comprobar que en pocos minutos se rompe la flojera."
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>

            <div className="bg-brand-50/70 p-5 rounded-2xl border border-brand-200 space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-brand-900 text-sm">Resumen Ejecutivo de Semana 1</h4>
                  <p className="text-xs text-brand-700">Consolida tu primer hito del programa</p>
                </div>
                <button
                  type="button"
                  disabled={readOnly || dayData.summaryGenerated}
                  onClick={() => onUpdateField(7, 'summaryGenerated', true)}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    dayData.summaryGenerated 
                      ? 'bg-growth-600 text-white' 
                      : 'bg-brand-600 hover:bg-brand-700 text-white shadow-xs'
                  }`}
                >
                  {dayData.summaryGenerated ? '✓ Resumen Generado' : 'Generar Resumen'}
                </button>
              </div>

              {dayData.summaryGenerated && (
                <div className="bg-white p-4 rounded-xl border border-brand-100 text-xs text-slate-700 space-y-1.5 animate-fadeIn">
                  <p>✅ <strong>Hábito en curso:</strong> {appState.activeHabit}</p>
                  <p>📊 <strong>Magnitud del calendario:</strong> {currentMagnitudeConfig.name}</p>
                  <p>⏱️ <strong>Minutos de enfoque completados:</strong> {appState.metrics.focusMinutes} minutos</p>
                  <p>🌱 <strong>Estado botánico:</strong> Fase 2 (Brote emergente)</p>
                  <p className="text-growth-700 font-semibold mt-2">¡Etapa 1 superada con éxito! Listo para la consistencia horaria en la Etapa 2.</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* DAY 8 */}
        {dayNumber === 8 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Definir hora programada de inicio de sesión *
              </label>
              <input
                type="time"
                disabled={readOnly}
                value={dayData.scheduledTime || '16:00'}
                onChange={(e) => onUpdateField(8, 'scheduledTime', e.target.value)}
                className="px-4 py-2 rounded-xl border border-slate-200 text-sm"
              />
              <p className="text-[11px] text-slate-400 mt-1">Margen de puntualidad tolerado: 15 minutos respecto a la hora programada.</p>
            </div>

            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div>
                <span className="text-xs font-bold text-slate-800">2. Registro de puntualidad real:</span>
                <p className="text-xs text-slate-500">
                  {dayData.punctualityRecorded 
                    ? `Registrado: ${dayData.actualTime} (${dayData.isOnTime ? '¡Puntual dentro del margen!' : 'Fuera del margen'})`
                    : 'Haz clic al comenzar para verificar puntualidad objetiva.'}
                </p>
              </div>
              {!readOnly && !dayData.punctualityRecorded && (
                <button
                  type="button"
                  onClick={() => {
                    const now = new Date();
                    const timeStr = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                    const isOnTime = true;
                    onUpdateField(8, 'punctualityRecorded', true);
                    onUpdateField(8, 'actualTime', timeStr);
                    onUpdateField(8, 'isOnTime', isOnTime);

                    setAppState(prev => ({
                      ...prev,
                      metrics: {
                        ...prev.metrics,
                        punctualityLogs: [
                          ...(prev.metrics.punctualityLogs || []),
                          { day: 8, scheduled: dayData.scheduledTime || '16:00', actual: timeStr, isOnTime }
                        ]
                      }
                    }));
                    showToast('Hora de inicio registrada con puntualidad verificada.', 'success');
                  }}
                  className="px-4 py-2 rounded-xl bg-brand-600 text-white font-semibold text-xs shadow-xs hover:bg-brand-700"
                >
                  Registrar Inicio Ahora
                </button>
              )}
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`3. Temporizador de Puntualidad (${config.duration} min - ${currentMagnitudeConfig.shortName})`}
              onComplete={(mins) => onTimerComplete(8, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 9 */}
        {dayNumber === 9 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Registrar "Mi Próximo Paso" *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.nextStep || ''}
                onChange={(e) => onUpdateField(9, 'nextStep', e.target.value)}
                placeholder="Ej. Resolver ejercicios impares / Doblar y acomodar ropa limpia"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
              <p className="text-[11px] text-brand-600 mt-1 font-medium">
                📌 Este paso se mostrará como Tarjeta Destacada en el Dashboard para mantener la tracción.
              </p>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Materiales requeridos para este paso *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.requiredMaterials || ''}
                onChange={(e) => onUpdateField(9, 'requiredMaterials', e.target.value)}
                placeholder="Ej. Libreta y calculadora / Canasta de ropa y ganchos"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`3. Preparación y avance inicial (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(9, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 10 */}
        {dayNumber === 10 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Objetivo único de este bloque sostenido *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.focusGoal || ''}
                onChange={(e) => onUpdateField(10, 'focusGoal', e.target.value)}
                placeholder="Ej. Concluir la lectura del tema / Dejar el clóset organizado"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`2. Temporizador de Enfoque Sostenido (${config.duration} min - ${currentMagnitudeConfig.shortName})`}
              onComplete={(mins) => onTimerComplete(10, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Reporte de avance al finalizar los {config.duration} min *
              </label>
              <textarea
                rows="3"
                disabled={readOnly}
                value={dayData.progressReport || ''}
                onChange={(e) => onUpdateField(10, 'progressReport', e.target.value)}
                placeholder={`¿Qué lograste concluir en estos ${config.duration} minutos continuos?`}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>
          </div>
        )}

        {/* DAY 11 */}
        {dayNumber === 11 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Seleccionar o registrar paso pendiente en curso *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.selectedPendingStep || ''}
                onChange={(e) => onUpdateField(11, 'selectedPendingStep', e.target.value)}
                placeholder="Ej. Revisión y corrección de las notas / Ordenar gaveta inferior"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`2. Temporizador de Tracción Continua (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(11, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Actualizar estado del paso tras la sesión *
              </label>
              <select
                disabled={readOnly}
                value={dayData.stepStatus || ''}
                onChange={(e) => onUpdateField(11, 'stepStatus', e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              >
                <option value="">Selecciona el nuevo estado...</option>
                <option value="completed">Completado al 100%</option>
                <option value="in_progress">Avanzado (En progreso fluido)</option>
                <option value="requires_more">Requiere otra sesión complementaria</option>
              </select>
            </div>
          </div>
        )}

        {/* DAY 12 */}
        {dayNumber === 12 && (
          <div className="space-y-5">
            <div className="p-3.5 bg-amber-50/70 border border-amber-200 rounded-xl text-xs text-amber-900">
              <strong>Principio de Premack:</strong> Vincula una actividad de ocio como consecuencia de realizar primero la sesión de estudio u orden.
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Actividad de ocio que deseas disfrutar después *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.leisureActivity || ''}
                onChange={(e) => onUpdateField(12, 'leisureActivity', e.target.value)}
                placeholder="Ej. Ver 1 episodio de mi serie favorita, jugar 20 min"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Tarea vinculada a completar primero *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.linkedTask || ''}
                onChange={(e) => onUpdateField(12, 'linkedTask', e.target.value)}
                placeholder="Ej. Resolver 5 preguntas de examen / Guardar toda la ropa limpia"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`3. Temporizador previo de Enfoque (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(12, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-3 bg-slate-50 rounded-xl border border-slate-200">
              <input
                type="checkbox"
                disabled={readOnly}
                checked={!!dayData.premackConfirmed}
                onChange={(e) => onUpdateField(12, 'premackConfirmed', e.target.checked)}
                className="w-4 h-4 text-growth-600 rounded"
              />
              <span>4. <strong>Confirmación de orden:</strong> Confirmo que completé primero los {config.duration} min de la tarea antes de acceder al ocio.</span>
            </label>
          </div>
        )}

        {/* DAY 13 */}
        {dayNumber === 13 && (
          <div className="space-y-5">
            <div className="p-3 bg-brand-50 border border-brand-200 rounded-xl text-xs text-brand-900">
              <strong>Estructura guiada de 3 fases ({config.duration} min totales):</strong> Adaptada para perfil {currentMagnitudeConfig.shortName}.
            </div>

            <NativeTimer
              sequence={config.sequence}
              label={`Temporizador Guiado Secuencial (${config.duration} min totales)`}
              onComplete={(mins) => onTimerComplete(13, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                Evaluación de fatiga y fluidez de la sesión *
              </label>
              <select
                disabled={readOnly}
                value={dayData.sessionFeelings || ''}
                onChange={(e) => onUpdateField(13, 'sessionFeelings', e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              >
                <option value="">Selecciona tu sensación...</option>
                <option value="excelente">Excelente: el descanso intermedio recargó mi energía</option>
                <option value="buena">Buena: completé ambos bloques sin perder el ritmo</option>
                <option value="pesada">Pesada: me costó retomar tras la pausa, requiere más práctica</option>
              </select>
            </div>
          </div>
        )}

        {/* DAY 14 */}
        {dayNumber === 14 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Selecciona exactamente tus 2 estrategias favoritas hasta ahora * (Seleccionadas: {(dayData.topStrategies || []).length}/2)
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                {[
                  'Micro-pasos de fricción cero',
                  'Checklist de acondicionamiento de espacio',
                  'Regla de mitigación de celular',
                  'Compromiso de hora y puntualidad',
                  'Principio de Premack (ocio condicionado)',
                  'Bloques secuenciales guiados'
                ].map(strat => {
                  const selected = (dayData.topStrategies || []).includes(strat);
                  return (
                    <label 
                      key={strat}
                      className={`p-3 rounded-xl border cursor-pointer flex items-center gap-2.5 transition-all ${
                        selected 
                          ? 'bg-brand-50 border-brand-300 text-brand-900 font-semibold' 
                          : 'bg-white border-slate-200 text-slate-700'
                      }`}
                    >
                      <input
                        type="checkbox"
                        disabled={readOnly}
                        checked={selected}
                        onChange={(e) => {
                          const current = dayData.topStrategies || [];
                          if (e.target.checked) {
                            if (current.length < 2) {
                              onUpdateField(14, 'topStrategies', [...current, strat]);
                            }
                          } else {
                            onUpdateField(14, 'topStrategies', current.filter(s => s !== strat));
                          }
                        }}
                        className="rounded text-brand-600"
                      />
                      <span>{strat}</span>
                    </label>
                  );
                })}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Comparativa de inicio: Semana 1 vs Semana 2 *
              </label>
              <select
                disabled={readOnly}
                value={dayData.weekComparison || ''}
                onChange={(e) => onUpdateField(14, 'weekComparison', e.target.value)}
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              >
                <option value="">¿Cómo se compara tu facilidad para comenzar la tarea?</option>
                <option value="mucho_mas_facil">Mucho más fácil: ya no siento esa parálisis inicial</option>
                <option value="algo_mas_facil">Algo más fácil: aún dudo, pero el temporizador me destraba</option>
                <option value="similar">Similar: necesito seguir consolidando la rutina</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Reflexión de consolidación de mitad de programa *
              </label>
              <textarea
                rows="3"
                disabled={readOnly}
                value={dayData.week2Reflection || ''}
                onChange={(e) => onUpdateField(14, 'week2Reflection', e.target.value)}
                placeholder="¿Qué cambio concreto has notado al realizar esta actividad?"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>
          </div>
        )}

        {/* DAY 15 */}
        {dayNumber === 15 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Prioriza 3 tareas pendientes (1 a 3) *
              </label>
              <div className="space-y-2">
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.task1 || ''}
                  onChange={(e) => onUpdateField(15, 'task1', e.target.value)}
                  placeholder="#1 Alta Prioridad (La más crítica)"
                  className="w-full px-4 py-2 rounded-xl border border-brand-300 text-sm bg-brand-50/30"
                />
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.task2 || ''}
                  onChange={(e) => onUpdateField(15, 'task2', e.target.value)}
                  placeholder="#2 Prioridad Media"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm"
                />
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.task3 || ''}
                  onChange={(e) => onUpdateField(15, 'task3', e.target.value)}
                  placeholder="#3 Prioridad Baja (Secundaria)"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Acción concreta inmediata para la Tarea #1 *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.actionForTask1 || ''}
                onChange={(e) => {
                  onUpdateField(15, 'actionForTask1', e.target.value);
                  onUpdateField(15, 'task1PrioritySet', true);
                }}
                placeholder="Ej. Resolver la parte principal / Limpiar el área más visible"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`3. Temporizador de Trabajo Exclusivo en Tarea #1 (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(15, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 16 */}
        {dayNumber === 16 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                1. Registrar la tarea que más has estado evitando o postergando *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.avoidedTask || ''}
                onChange={(e) => onUpdateField(16, 'avoidedTask', e.target.value)}
                placeholder="Ej. El tema más difícil / La zona más desordenada de la habitación"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Definir una micro-acción de {config.duration} minutos sobre ella *
              </label>
              <input
                type="text"
                disabled={readOnly}
                value={dayData.microAction5m || ''}
                onChange={(e) => onUpdateField(16, 'microAction5m', e.target.value)}
                placeholder="Ej. Solo sacar los primeros 5 objetos / Leer el primer subtema"
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              />
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`3. Temporizador de Desbloqueo (${config.duration} min - ${currentMagnitudeConfig.shortName})`}
              onComplete={(mins) => onTimerComplete(16, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                4. Confirmación: ¿Rompiste la fricción inicial sobre esta tarea evitada? *
              </label>
              <div className="flex gap-4">
                <label className="flex items-center gap-2 text-sm text-slate-800 cursor-pointer">
                  <input
                    type="radio"
                    name="frictionBroken"
                    disabled={readOnly}
                    checked={dayData.frictionBroken === 'yes'}
                    onChange={() => onUpdateField(16, 'frictionBroken', 'yes')}
                    className="text-growth-600"
                  />
                  Sí, la tarea ya no me intimida tanto
                </label>
                <label className="flex items-center gap-2 text-sm text-slate-800 cursor-pointer">
                  <input
                    type="radio"
                    name="frictionBroken"
                    disabled={readOnly}
                    checked={dayData.frictionBroken === 'no'}
                    onChange={() => onUpdateField(16, 'frictionBroken', 'no')}
                    className="text-amber-600"
                  />
                  Aún siento fricción, haré otro micro-paso
                </label>
              </div>
            </div>
          </div>
        )}

        {/* DAY 17 */}
        {dayNumber === 17 && (
          <div className="space-y-5">
            <div className="bg-slate-50 p-5 rounded-2xl border border-slate-200 space-y-4">
              <span className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Formulario Anticipatorio: "Para Mañana"
              </span>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Tarea principal para mañana:</label>
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.tomorrowTask || ''}
                  onChange={(e) => onUpdateField(17, 'tomorrowTask', e.target.value)}
                  placeholder="Ej. Rutina de ejercicios / Práctica de cálculo / Arreglar el armario"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm bg-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Primer paso exacto con el que empezarás:</label>
                <input
                  type="text"
                  disabled={readOnly}
                  value={dayData.tomorrowFirstStep || ''}
                  onChange={(e) => onUpdateField(17, 'tomorrowFirstStep', e.target.value)}
                  placeholder="Ej. Colocar la ropa deportiva a la vista / Abrir la libreta"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 text-sm bg-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">Hora tentativa de inicio:</label>
                <input
                  type="time"
                  disabled={readOnly}
                  value={dayData.tomorrowHour || '17:00'}
                  onChange={(e) => onUpdateField(17, 'tomorrowHour', e.target.value)}
                  className="px-4 py-2 rounded-xl border border-slate-200 text-sm bg-white"
                />
              </div>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer pt-2">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.checklistTomorrowReady}
                  onChange={(e) => onUpdateField(17, 'checklistTomorrowReady', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span><strong>Materiales listos:</strong> Dejé las herramientas e insumos preparados sobre la mesa.</span>
              </label>
            </div>
          </div>
        )}

        {/* DAY 18 */}
        {dayNumber === 18 && (
          <div className="space-y-5">
            <div className="p-3 bg-brand-50 border border-brand-200 rounded-xl text-xs text-brand-900">
              <strong>Secuencia Doble Guiada ({config.duration} min totales):</strong> Diseñada para resistencia de perfil {currentMagnitudeConfig.shortName}.
            </div>

            <NativeTimer
              sequence={config.sequence}
              label={`Sesión Doble Guiada (${config.duration} min)`}
              onComplete={(mins) => onTimerComplete(18, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />

            <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-3 bg-slate-50 rounded-xl border border-slate-200">
              <input
                type="checkbox"
                disabled={readOnly}
                checked={!!dayData.dualBlockConfirmed}
                onChange={(e) => onUpdateField(18, 'dualBlockConfirmed', e.target.checked)}
                className="w-4 h-4 text-growth-600 rounded"
              />
              <span>Confirmar conclusión de ambos bloques con su pausa intermedia respetada.</span>
            </label>
          </div>
        )}

        {/* DAY 19 */}
        {dayNumber === 19 && (
          <div className="space-y-5">
            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-3">
              <span className="text-xs font-bold text-slate-800 uppercase tracking-wider">
                Checklist Integral de 5 Puntos de Autonomía:
              </span>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.check1}
                  onChange={(e) => onUpdateField(19, 'check1', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>1. Tarea definida con claridad y alcance acotado.</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.check2}
                  onChange={(e) => onUpdateField(19, 'check2', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>2. Primer paso inmediato identificado.</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.check3}
                  onChange={(e) => onUpdateField(19, 'check3', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>3. Entorno físico y digital despejado (sin distracciones).</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.check4}
                  onChange={(e) => onUpdateField(19, 'check4', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>4. Hora de inicio acordada y puntualidad respetada.</span>
              </label>

              <label className="flex items-center gap-3 text-sm text-slate-800 cursor-pointer p-2 rounded-lg hover:bg-white transition-colors">
                <input
                  type="checkbox"
                  disabled={readOnly}
                  checked={!!dayData.check5}
                  onChange={(e) => onUpdateField(19, 'check5', e.target.checked)}
                  className="w-4 h-4 text-growth-600 rounded"
                />
                <span>5. Temporizador activado y foco absoluto en la tarea.</span>
              </label>
            </div>

            <NativeTimer
              minutes={config.duration}
              label={`Sesión Integral de Autonomía (${config.duration} min - ${currentMagnitudeConfig.shortName})`}
              onComplete={(mins) => onTimerComplete(19, mins)}
              isCompleted={dayData.timerCompleted}
              readOnly={readOnly}
            />
          </div>
        )}

        {/* DAY 20 */}
        {dayNumber === 20 && (
          <div className="space-y-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Selecciona tus 2 estrategias finales preferidas * ({(dayData.finalStrategies || []).length}/2)
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
                {[
                  'Micro-pasos de fricción cero',
                  'Preparación de espacio anticipada',
                  'Regla de mitigación de celular',
                  'Bloques secuenciales guiados',
                  'Principio de Premack (ocio condicionado)',
                  'Checklist integral de autonomía'
                ].map(strat => {
                  const selected = (dayData.finalStrategies || []).includes(strat);
                  return (
                    <label 
                      key={strat}
                      className={`p-3 rounded-xl border cursor-pointer flex items-center gap-2.5 transition-all ${
                        selected 
                          ? 'bg-growth-50 border-growth-300 text-growth-900 font-semibold' 
                          : 'bg-white border-slate-200 text-slate-700'
                      }`}
                    >
                      <input
                        type="checkbox"
                        disabled={readOnly}
                        checked={selected}
                        onChange={(e) => {
                          const current = dayData.finalStrategies || [];
                          if (e.target.checked) {
                            if (current.length < 2) {
                              onUpdateField(20, 'finalStrategies', [...current, strat]);
                            }
                          } else {
                            onUpdateField(20, 'finalStrategies', current.filter(s => s !== strat));
                          }
                        }}
                        className="rounded text-growth-600"
                      />
                      <span>{strat}</span>
                    </label>
                  );
                })}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                2. Frecuencia semanal sostenible para este hábito *
              </label>
              <div className="grid grid-cols-3 gap-2">
                {['3 días por semana', '4 días por semana', '5 días por semana'].map(freq => (
                  <label 
                    key={freq}
                    className={`p-3 rounded-xl border text-center text-xs font-semibold cursor-pointer transition-all ${
                      dayData.weeklyFrequency === freq
                        ? 'bg-brand-50 border-brand-500 text-brand-900'
                        : 'bg-white border-slate-200 text-slate-600'
                    }`}
                  >
                    <input
                      type="radio"
                      name="weeklyFrequency"
                      disabled={readOnly}
                      checked={dayData.weeklyFrequency === freq}
                      onChange={() => onUpdateField(20, 'weeklyFrequency', freq)}
                      className="sr-only"
                    />
                    {freq}
                  </label>
                ))}
              </div>
            </div>

            <div className="bg-slate-50 p-5 rounded-2xl border border-slate-200 space-y-3">
              <div className="flex items-center justify-between">
                <div>
                  <h4 className="font-bold text-slate-900 text-sm">3. Plan Personal Sostenible ({currentMagnitudeConfig.shortName})</h4>
                  <p className="text-xs text-slate-500">Crea tu síntesis definitiva adaptada a la complejidad elegida</p>
                </div>
                <button
                  type="button"
                  disabled={readOnly || dayData.personalPlanGenerated}
                  onClick={() => onUpdateField(20, 'personalPlanGenerated', true)}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                    dayData.personalPlanGenerated 
                      ? 'bg-growth-600 text-white' 
                      : 'bg-brand-600 hover:bg-brand-700 text-white shadow-xs'
                  }`}
                >
                  {dayData.personalPlanGenerated ? '✓ Plan Generado' : 'Generar Plan Personal'}
                </button>
              </div>

              {dayData.personalPlanGenerated && (
                <div className="bg-white p-4 rounded-xl border border-brand-200 text-xs text-slate-800 space-y-2 animate-fadeIn">
                  <p className="font-bold text-brand-700 uppercase tracking-wider text-[11px]">Tu Hoja de Ruta Definitiva:</p>
                  <p>🎯 <strong>Hábito:</strong> {appState.activeHabit} ({currentMagnitudeConfig.name})</p>
                  <p>🧩 <strong>Estrategias Elegidas:</strong> {(dayData.finalStrategies || []).join(' + ')}</p>
                  <p>📅 <strong>Ritmo Acordado:</strong> {dayData.weeklyFrequency}</p>
                  <p className="text-slate-500 italic mt-1">Este plan te acompañará en tu vida cotidiana para mantener el hábito vivo sin recaer.</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* DAY 21 */}
        {dayNumber === 21 && (
          <div className="space-y-6">
            <div className="p-4 bg-gradient-to-r from-growth-50 via-emerald-50 to-indigo-50 border border-growth-200 rounded-2xl text-center">
              <span className="text-3xl">🌳</span>
              <h4 className="font-extrabold text-slate-900 text-base mt-1">Evaluación Final y Graduación del Hábito</h4>
              <p className="text-xs text-slate-600">Has alcanzado el Día 21. Tu planta se convertirá en un árbol maduro de disciplina.</p>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                1. Nivel de transformación de tu hábito (1 a 5) *
              </label>
              <div className="flex gap-2">
                {[1, 2, 3, 4, 5].map(rating => (
                  <button
                    key={rating}
                    type="button"
                    disabled={readOnly}
                    onClick={() => onUpdateField(21, 'transformationRating', rating)}
                    className={`text-2xl p-2 rounded-xl border transition-all ${
                      (dayData.transformationRating || 0) >= rating
                        ? 'bg-growth-100 border-growth-300 text-growth-600 scale-105'
                        : 'bg-slate-50 border-slate-200 text-slate-300'
                    }`}
                  >
                    ★
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                2. Autopercepción de tu procrastinación: ¿Antes vs Ahora? *
              </label>
              <textarea
                rows="3"
                disabled={readOnly}
                value={dayData.procrastinationBeforeAfter || ''}
                onChange={(e) => onUpdateField(21, 'procrastinationBeforeAfter', e.target.value)}
                placeholder="Antes aplazaba iniciar horas enteras. Ahora con lapsos calibrados rompo la inercia sin esfuerzo."
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-1">
                3. Mayor aprendizaje y conclusión de estos 21 días *
              </label>
              <textarea
                rows="3"
                disabled={readOnly}
                value={dayData.finalTakeaway || ''}
                onChange={(e) => onUpdateField(21, 'finalTakeaway', e.target.value)}
                placeholder="El secreto fue ajustar el lapso de tiempo a la verdadera complejidad de la tarea."
                className="w-full px-4 py-2.5 rounded-xl border border-slate-200 text-sm"
              ></textarea>
            </div>
          </div>
        )}
      </div>

      {/* ACTION FOOTER: FINALIZAR DÍA BUTTON (DISABLED UNTIL 100%) */}
      <div className="sticky bottom-4 z-30 bg-white/95 backdrop-blur-md p-4 sm:p-5 rounded-2xl border border-slate-200 shadow-xl flex flex-col sm:flex-row items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-bold text-sm ${
            dayPercentage === 100 ? 'bg-growth-100 text-growth-700' : 'bg-slate-100 text-slate-500'
          }`}>
            {dayPercentage}%
          </div>
          <div>
            <p className="text-xs font-bold text-slate-800">
              {readOnly 
                ? 'Día completado y guardado con éxito' 
                : dayPercentage === 100 
                  ? '¡Todas las acciones listas para guardar!' 
                  : 'Completa todas las acciones requeridas para desbloquear'}
            </p>
            <p className="text-[11px] text-slate-500">
              {readOnly ? 'Solo lectura • Registros protegidos' : 'El desbloqueo del Día N+1 requiere guardar este día.'}
            </p>
          </div>
        </div>

        <div>
          {readOnly ? (
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-xl bg-growth-100 text-growth-800 font-semibold text-xs border border-growth-200">
                <Icons.Check /> Día Finalizado y Protegido
              </span>
              {dayNumber < 21 && (
                <button
                  type="button"
                  onClick={() => onNavigateDay(dayNumber + 1)}
                  className="px-4 py-2.5 rounded-xl bg-brand-600 hover:bg-brand-700 text-white font-semibold text-xs shadow-xs"
                >
                  Ir al Día {dayNumber + 1} ▶
                </button>
              )}
            </div>
          ) : (
            <button
              type="button"
              disabled={dayPercentage < 100}
              onClick={() => onFinalizeDay(dayNumber)}
              className={`px-6 py-3 rounded-xl font-bold text-sm transition-all flex items-center gap-2 shadow-sm ${
                dayPercentage === 100
                  ? 'bg-growth-500 hover:bg-growth-600 text-white hover:shadow-growth-500/30 scale-102 cursor-pointer animate-pulse'
                  : 'bg-slate-200 text-slate-400 cursor-not-allowed'
              }`}
            >
              <Icons.Check /> Finalizar Día {dayNumber} (100%)
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

// ==========================================
// VIEW 3: REPORTE DE PROGRESO Y MÉTRICAS
// ==========================================

window.DailyActivityView = DailyActivityView;
