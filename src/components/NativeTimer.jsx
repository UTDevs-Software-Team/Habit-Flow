/**
 * ============================================================================
 * COMPONENTE BLACK BOX: TEMPORIZADOR NATIVO REACT (`NativeTimer`)
 * ============================================================================
 * Soporta bloques simples y secuencias multi-fase (Dias 13 y 18), con limpieza
 * estricta de `setInterval` al desmontar y timbre Web Audio (`playChime`).
 */
const { useState, useEffect, useRef, useMemo } = React;

const NativeTimer = ({ 
  minutes = 5, 
  label = 'Temporizador de Enfoque', 
  onComplete, 
  isCompleted = false, 
  readOnly = false,
  sequence = null 
}) => {
  const phases = useMemo(() => {
    if (sequence && Array.isArray(sequence) && sequence.length > 0) {
      return sequence.map(p => ({
        name: p.name,
        duration: p.minutes * 60,
        type: p.type || (p.name.toLowerCase().includes('descanso') || p.name.toLowerCase().includes('pausa') ? 'break' : 'work')
      }));
    }
    return [{
      name: 'Enfoque',
      duration: minutes * 60,
      type: 'work'
    }];
  }, [sequence, minutes]);

  const [phaseIndex, setPhaseIndex] = useState(0);
  const [timeLeft, setTimeLeft] = useState(phases[0].duration);
  const [isRunning, setIsRunning] = useState(false);
  const [timerDone, setTimerDone] = useState(isCompleted);
  const [soundEnabled, setSoundEnabled] = useState(true);
  const timerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, []);

  // If minutes or sequence change dynamically (e.g. user toggles magnitude)
  useEffect(() => {
    if (!isCompleted && !isRunning) {
      setPhaseIndex(0);
      setTimeLeft(phases[0].duration);
    }
  }, [phases, minutes, isCompleted]);

  useEffect(() => {
    if (isCompleted) {
      setTimerDone(true);
      setIsRunning(false);
    }
  }, [isCompleted]);

  useEffect(() => {
    if (isRunning) {
      timerRef.current = setInterval(() => {
        setTimeLeft(prev => {
          if (prev <= 1) {
            if (soundEnabled) playChime();

            if (phaseIndex + 1 < phases.length) {
              const nextIdx = phaseIndex + 1;
              setPhaseIndex(nextIdx);
              return phases[nextIdx].duration;
            } else {
              clearInterval(timerRef.current);
              setIsRunning(false);
              setTimerDone(true);
              if (onComplete) {
                const totalMins = phases.reduce((acc, p) => acc + (p.duration / 60), 0);
                onComplete(totalMins);
              }
              return 0;
            }
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [isRunning, phaseIndex, phases, soundEnabled, onComplete]);

  const formatTime = (secs) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  };

  const handleStart = () => {
    if (readOnly || timerDone) return;
    setIsRunning(true);
  };

  const handlePause = () => {
    setIsRunning(false);
  };

  const handleReset = () => {
    if (readOnly) return;
    setIsRunning(false);
    setPhaseIndex(0);
    setTimeLeft(phases[0].duration);
    setTimerDone(false);
  };

  const handleFastForward = () => {
    if (readOnly || timerDone) return;
    setTimeLeft(2);
  };

  const currentPhase = phases[phaseIndex] || phases[0];
  const progressPercent = Math.min(
    100, 
    Math.max(0, ((currentPhase.duration - timeLeft) / currentPhase.duration) * 100)
  );

  return (
    <div className={`p-4 sm:p-5 rounded-2xl border transition-all ${
      timerDone 
        ? 'bg-growth-50/70 border-growth-200' 
        : currentPhase.type === 'break'
          ? 'bg-amber-50/70 border-amber-200'
          : 'bg-white border-slate-200 shadow-sm'
    }`}>
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className={`p-2 rounded-xl ${
            timerDone ? 'bg-growth-100 text-growth-700' : 'bg-brand-50 text-brand-600'
          }`}>
            <Icons.Clock />
          </span>
          <div>
            <h4 className="font-semibold text-slate-800 text-sm sm:text-base leading-tight">{label}</h4>
            {phases.length > 1 && (
              <p className="text-xs text-slate-500 font-medium">
                Fase {phaseIndex + 1} de {phases.length}: <span className="font-semibold text-brand-700">{currentPhase.name}</span>
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => setSoundEnabled(!soundEnabled)}
            title={soundEnabled ? 'Sonido activado' : 'Silenciado'}
            className="text-xs text-slate-400 hover:text-slate-600 p-1.5 rounded-lg border border-slate-200"
          >
            {soundEnabled ? '🔔' : '🔕'}
          </button>
          {timerDone && (
            <span className="inline-flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full bg-growth-100 text-growth-800 border border-growth-200">
              <Icons.Check /> Completado
            </span>
          )}
        </div>
      </div>

      {phases.length > 1 && (
        <div className="grid grid-cols-3 gap-2 mb-3">
          {phases.map((p, idx) => (
            <div 
              key={idx}
              className={`text-center py-1.5 px-2 rounded-lg text-xs font-medium border transition-all ${
                idx < phaseIndex 
                  ? 'bg-growth-100 text-growth-800 border-growth-300'
                  : idx === phaseIndex
                    ? 'bg-brand-50 text-brand-800 border-brand-300 font-bold shadow-xs'
                    : 'bg-slate-50 text-slate-400 border-slate-200'
              }`}
            >
              {p.name} ({Math.round(p.duration / 60)}m)
            </div>
          ))}
        </div>
      )}

      <div className="flex flex-col items-center justify-center my-3">
        <div className="font-mono text-4xl sm:text-5xl font-bold tracking-tight text-slate-900 mb-2">
          {formatTime(timeLeft)}
        </div>

        <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden border border-slate-200/80">
          <div 
            className={`h-full transition-all duration-300 rounded-full ${
              timerDone 
                ? 'bg-growth-500' 
                : currentPhase.type === 'break' 
                  ? 'bg-amber-500' 
                  : 'bg-brand-500'
            }`}
            style={{ width: `${timerDone ? 100 : progressPercent}%` }}
          ></div>
        </div>
      </div>

      {!readOnly && (
        <div className="flex items-center justify-center gap-2 mt-4 pt-3 border-t border-slate-100">
          {!isRunning ? (
            <button
              type="button"
              onClick={handleStart}
              disabled={timerDone}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-semibold text-white shadow-sm transition-all ${
                timerDone 
                  ? 'bg-slate-300 cursor-not-allowed'
                  : 'bg-brand-600 hover:bg-brand-700 active:scale-95'
              }`}
            >
              <Icons.Play /> Iniciar Temporizador
            </button>
          ) : (
            <button
              type="button"
              onClick={handlePause}
              className="flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm font-semibold bg-amber-500 hover:bg-amber-600 text-white shadow-sm active:scale-95 transition-all"
            >
              <Icons.Pause /> Pausar
            </button>
          )}

          <button
            type="button"
            onClick={handleReset}
            title="Reiniciar a inicio"
            className="p-2.5 rounded-xl text-slate-500 hover:text-slate-800 hover:bg-slate-100 border border-slate-200 active:scale-95 transition-all"
          >
            <Icons.Reset />
          </button>

          {!timerDone && (
            <button
              type="button"
              onClick={handleFastForward}
              title="Modo prueba: simular últimos 2 segundos"
              className="text-xs text-slate-400 hover:text-brand-600 px-2 py-1 rounded border border-dashed border-slate-200 hover:border-brand-300"
            >
              ⚡ Probar final
            </button>
          )}
        </div>
      )}

      {readOnly && (
        <p className="text-center text-xs text-slate-400 italic mt-2">
          Sesión cronometrada registrada y finalizada con éxito.
        </p>
      )}
    </div>
  );
};

// --- MAIN APP ROOT COMPONENT ---

window.NativeTimer = NativeTimer;
