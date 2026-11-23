/**
 * ============================================================================
 * COMPONENTE BLACK BOX: PLANTA VIRTUAL SVG (`VirtualPlantVisual`)
 * ============================================================================
 * Props:
 * - `phase`: objeto retornado por `getPlantPhase(completedDaysCount)` (1..5).
 * - `size`: `'default' | 'large'`.
 */
const VirtualPlantVisual = ({ phase, size = 'default' }) => {
  const isLarge = size === 'large';
  const heightClass = isLarge ? 'h-64 sm:h-72' : 'h-48 sm:h-56';

  return (
    <div className={`w-full ${heightClass} flex items-center justify-center relative overflow-hidden bg-gradient-to-b from-indigo-50/40 via-emerald-50/30 to-amber-50/40 rounded-2xl border border-slate-100 p-3`}>
      <div className="absolute inset-0 bg-radial from-emerald-100/30 via-transparent to-transparent pointer-events-none"></div>

      <svg className="w-full h-full max-w-xs drop-shadow-sm transition-all duration-700 ease-out" viewBox="0 0 240 200" fill="none" xmlns="http://www.w3.org/2000/svg">
        <ellipse cx="120" cy="180" rx="70" ry="12" fill="#cbd5e1" opacity="0.4" />

        <path d="M75 135 L85 180 C86 183 90 185 94 185 L146 185 C150 185 154 183 155 180 L165 135 Z" fill="#d97706" />
        <path d="M85 180 L155 180 L146 185 L94 185 Z" fill="#b45309" />
        <rect x="70" y="125" width="100" height="12" rx="4" fill="#f59e0b" />
        <ellipse cx="120" cy="130" rx="45" ry="6" fill="#78350f" />
        <ellipse cx="120" cy="131" rx="42" ry="4" fill="#451a03" />

        {/* PHASE 1: Semilla */}
        {phase === 1 && (
          <g className="transition-all duration-700">
            <circle cx="120" cy="128" r="6" fill="#fef08a" />
            <path d="M120 123 C124 123 126 127 120 133 C114 127 116 123 120 123 Z" fill="#eab308" />
            <circle cx="120" cy="128" r="10" stroke="#fef08a" strokeWidth="1.5" strokeDasharray="3 3" opacity="0.7" className="animate-spin" style={{ animationDuration: '8s' }} />
            <circle cx="100" cy="118" r="2" fill="#34d399" opacity="0.6" />
            <circle cx="140" cy="115" r="2.5" fill="#34d399" opacity="0.8" />
            <circle cx="120" cy="108" r="1.5" fill="#fef08a" />
          </g>
        )}

        {/* PHASE 2: Brote */}
        {phase === 2 && (
          <g className="transition-all duration-700">
            <path d="M120 129 Q120 108 120 95" stroke="#10b981" strokeWidth="4" strokeLinecap="round" />
            <path d="M120 102 C105 102 98 88 120 95 Z" fill="#34d399" stroke="#059669" strokeWidth="1" />
            <path d="M120 98 C135 98 142 84 120 91 Z" fill="#10b981" stroke="#047857" strokeWidth="1" />
            <circle cx="107" cy="94" r="1.8" fill="#60a5fa" />
            <circle cx="128" cy="90" r="1.5" fill="#93c5fd" />
          </g>
        )}

        {/* PHASE 3: Tallo con Hojas */}
        {phase === 3 && (
          <g className="transition-all duration-700">
            <path d="M120 130 Q122 95 119 65" stroke="#059669" strokeWidth="5" strokeLinecap="round" />
            <path d="M120 115 Q100 115 90 102 C102 96 115 108 120 115 Z" fill="#10b981" />
            <path d="M120 108 Q140 108 152 95 C140 90 126 102 120 108 Z" fill="#34d399" />
            <path d="M120 90 Q98 88 88 75 C102 70 115 82 120 90 Z" fill="#059669" />
            <path d="M120 82 Q142 80 150 66 C138 62 125 74 120 82 Z" fill="#10b981" />
            <path d="M119 65 C110 50 120 40 120 40 C120 40 130 50 119 65 Z" fill="#6ee7b7" stroke="#059669" strokeWidth="1" />
            <circle cx="92" cy="78" r="2" fill="#60a5fa" />
            <circle cx="145" cy="70" r="2" fill="#93c5fd" />
          </g>
        )}

        {/* PHASE 4: Floración */}
        {phase === 4 && (
          <g className="transition-all duration-700">
            <path d="M120 130 Q118 85 120 50" stroke="#047857" strokeWidth="6" strokeLinecap="round" />
            <path d="M120 95 Q90 85 80 65" stroke="#047857" strokeWidth="4" strokeLinecap="round" />
            <path d="M120 80 Q150 75 160 55" stroke="#047857" strokeWidth="4" strokeLinecap="round" />
            <circle cx="95" cy="75" r="14" fill="#059669" opacity="0.9" />
            <circle cx="145" cy="70" r="15" fill="#10b981" opacity="0.9" />
            <circle cx="120" cy="55" r="18" fill="#34d399" opacity="0.9" />
            <circle cx="105" cy="45" r="12" fill="#6ee7b7" />
            <circle cx="135" cy="45" r="12" fill="#6ee7b7" />

            <g transform="translate(76, 60)">
              <circle cx="0" cy="-6" r="4" fill="#818cf8" />
              <circle cx="6" cy="0" r="4" fill="#818cf8" />
              <circle cx="0" cy="6" r="4" fill="#818cf8" />
              <circle cx="-6" cy="0" r="4" fill="#818cf8" />
              <circle cx="0" cy="0" r="3" fill="#facc15" />
            </g>
            <g transform="translate(162, 52)">
              <circle cx="0" cy="-6" r="4" fill="#a78bfa" />
              <circle cx="6" cy="0" r="4" fill="#a78bfa" />
              <circle cx="0" cy="6" r="4" fill="#a78bfa" />
              <circle cx="-6" cy="0" r="4" fill="#a78bfa" />
              <circle cx="0" cy="0" r="3" fill="#facc15" />
            </g>
            <g transform="translate(120, 36)">
              <circle cx="0" cy="-7" r="5" fill="#6366f1" />
              <circle cx="7" cy="0" r="5" fill="#6366f1" />
              <circle cx="0" cy="7" r="5" fill="#6366f1" />
              <circle cx="-7" cy="0" r="5" fill="#6366f1" />
              <circle cx="0" cy="0" r="4" fill="#fef08a" />
            </g>
            <g transform="translate(165, 30)">
              <ellipse cx="-4" cy="-4" rx="4" ry="6" fill="#f472b6" transform="rotate(-30)" />
              <ellipse cx="4" cy="-4" rx="4" ry="6" fill="#f472b6" transform="rotate(30)" />
              <line x1="0" y1="-8" x2="0" y2="4" stroke="#475569" strokeWidth="1" />
            </g>
          </g>
        )}

        {/* PHASE 5: Árbol Completo */}
        {phase === 5 && (
          <g className="transition-all duration-700">
            <path d="M120 130 L114 85 Q105 75 90 60 M120 85 Q135 75 150 60 M120 85 L120 55" stroke="#78350f" strokeWidth="8" strokeLinecap="round" />
            <ellipse cx="120" cy="50" rx="55" ry="36" fill="#047857" />
            <ellipse cx="90" cy="55" rx="35" ry="28" fill="#059669" />
            <ellipse cx="150" cy="55" rx="35" ry="28" fill="#059669" />
            <ellipse cx="120" cy="40" rx="42" ry="28" fill="#10b981" />
            <ellipse cx="105" cy="32" rx="25" ry="18" fill="#34d399" />
            <ellipse cx="135" cy="32" rx="25" ry="18" fill="#6ee7b7" />

            <circle cx="85" cy="52" r="5" fill="#f59e0b" stroke="#fef08a" strokeWidth="1" />
            <circle cx="105" cy="62" r="5" fill="#f59e0b" stroke="#fef08a" strokeWidth="1" />
            <circle cx="122" cy="35" r="5.5" fill="#eab308" stroke="#fef08a" strokeWidth="1" />
            <circle cx="140" cy="50" r="5" fill="#f59e0b" stroke="#fef08a" strokeWidth="1" />
            <circle cx="155" cy="65" r="4.5" fill="#f59e0b" stroke="#fef08a" strokeWidth="1" />

            <g transform="translate(120, 10)">
              <polygon points="0,-8 2.5,-2.5 8,0 2.5,2.5 0,8 -2.5,2.5 -8,0 -2.5,-2.5" fill="#fbbf24" stroke="#f59e0b" strokeWidth="1" />
            </g>
            <g transform="translate(65, 35)">
              <ellipse cx="-3" cy="-3" rx="3" ry="5" fill="#ec4899" transform="rotate(-25)" />
              <ellipse cx="3" cy="-3" rx="3" ry="5" fill="#ec4899" transform="rotate(25)" />
            </g>
            <g transform="translate(175, 40)">
              <ellipse cx="-3" cy="-3" rx="3" ry="5" fill="#818cf8" transform="rotate(-25)" />
              <ellipse cx="3" cy="-3" rx="3" ry="5" fill="#818cf8" transform="rotate(25)" />
            </g>
          </g>
        )}
      </svg>
    </div>
  );
};

// --- NATIVE REACT TIMER COMPONENT ---

window.VirtualPlantVisual = VirtualPlantVisual;
