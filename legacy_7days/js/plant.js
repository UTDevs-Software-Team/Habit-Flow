/**
 * HABITFLOW - plant.js
 * Sistema de motivación visual: Planta Virtual (Sección 7 del documento oficial)
 * 
 * Implementa las 5 etapas de crecimiento calculadas con la fórmula:
 * Porcentaje de progreso = (Actividades completadas / 7) * 100
 * 
 * Etapas:
 * 1. Semilla: 0 % a 19 %
 * 2. Brote: 20 % a 39 %
 * 3. Planta pequeña: 40 % a 59 %
 * 4. Planta desarrollada: 60 % a 79 %
 * 5. Planta florecida: 80 % a 100 %
 * 
 * Regla de crecimiento: La planta no perderá su crecimiento si el usuario
 * deja una actividad pendiente.
 */

const VirtualPlant = (() => {
  // Generador de audio sintetizado con Web Audio API (sin archivos externos)
  const SoundFX = {
    audioCtx: null,

    init() {
      if (!this.audioCtx && (window.AudioContext || window.webkitAudioContext)) {
        const AudioContextClass = window.AudioContext || window.webkitAudioContext;
        this.audioCtx = new AudioContextClass();
      }
    },

    playWaterSound() {
      try {
        this.init();
        if (!this.audioCtx) return;
        if (this.audioCtx.state === 'suspended') this.audioCtx.resume();

        // Sonido de gotas de agua suaves
        const now = this.audioCtx.currentTime;
        for (let i = 0; i < 3; i++) {
          const osc = this.audioCtx.createOscillator();
          const gain = this.audioCtx.createGain();
          osc.type = 'sine';
          const startTime = now + i * 0.12;
          osc.frequency.setValueAtTime(600 + i * 150, startTime);
          osc.frequency.exponentialRampToValueAtTime(1200 + i * 200, startTime + 0.08);

          gain.gain.setValueAtTime(0.15, startTime);
          gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.1);

          osc.connect(gain);
          gain.connect(this.audioCtx.destination);
          osc.start(startTime);
          osc.stop(startTime + 0.1);
        }
      } catch (e) {
        // Silencioso si el navegador bloquea audio
      }
    },

    playGrowthChime() {
      try {
        this.init();
        if (!this.audioCtx) return;
        if (this.audioCtx.state === 'suspended') this.audioCtx.resume();

        const notes = [523.25, 659.25, 783.99, 1046.50]; // Do, Mi, Sol, Do alto
        const now = this.audioCtx.currentTime;
        notes.forEach((freq, idx) => {
          const osc = this.audioCtx.createOscillator();
          const gain = this.audioCtx.createGain();
          osc.type = 'triangle';
          const startTime = now + idx * 0.09;
          osc.frequency.setValueAtTime(freq, startTime);

          gain.gain.setValueAtTime(0.18, startTime);
          gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.35);

          osc.connect(gain);
          gain.connect(this.audioCtx.destination);
          osc.start(startTime);
          osc.stop(startTime + 0.35);
        });
      } catch (e) {
        // Silencioso
      }
    },

    playBloomCelebration() {
      try {
        this.init();
        if (!this.audioCtx) return;
        if (this.audioCtx.state === 'suspended') this.audioCtx.resume();

        const notes = [440, 554.37, 659.25, 880, 1108.73, 1318.51];
        const now = this.audioCtx.currentTime;
        notes.forEach((freq, idx) => {
          const osc = this.audioCtx.createOscillator();
          const gain = this.audioCtx.createGain();
          osc.type = 'sine';
          const startTime = now + idx * 0.08;
          osc.frequency.setValueAtTime(freq, startTime);

          gain.gain.setValueAtTime(0.2, startTime);
          gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.45);

          osc.connect(gain);
          gain.connect(this.audioCtx.destination);
          osc.start(startTime);
          osc.stop(startTime + 0.45);
        });
      } catch (e) {
        // Silencioso
      }
    }
  };

  /**
   * Calcula el porcentaje y la etapa de la planta de acuerdo al documento:
   * Porcentaje = (completadas / 7) * 100
   * Etapa 1: 0 - 19.99% (Semilla)
   * Etapa 2: 20 - 39.99% (Brote)
   * Etapa 3: 40 - 59.99% (Planta pequeña)
   * Etapa 4: 60 - 79.99% (Planta desarrollada)
   * Etapa 5: 80 - 100% (Planta florecida)
   * 
   * Además aplica la regla de que la planta NO pierde su crecimiento máximo.
   */
  function calculatePlantState(completedDaysCount, maxPercentAchieved = 0) {
    const rawPercent = (completedDaysCount / 7) * 100;
    // La planta no pierde su nivel máximo alcanzado
    const effectivePercent = Math.max(rawPercent, maxPercentAchieved || 0);
    const roundedPercent = Math.round(effectivePercent * 10) / 10;

    let stageNumber = 1;
    if (effectivePercent >= 80) {
      stageNumber = 5;
    } else if (effectivePercent >= 60) {
      stageNumber = 4;
    } else if (effectivePercent >= 40) {
      stageNumber = 3;
    } else if (effectivePercent >= 20) {
      stageNumber = 2;
    } else {
      stageNumber = 1;
    }

    const stageData = HABITFLOW_DATA.plantStages.find(s => s.stage === stageNumber) || HABITFLOW_DATA.plantStages[0];

    return {
      completedCount: completedDaysCount,
      rawPercent: Math.round(rawPercent * 10) / 10,
      effectivePercent: roundedPercent,
      stageNumber,
      stageData
    };
  }

  /**
   * Genera el SVG ilustrado dinámico para cada etapa de la planta
   */
  function renderPlantSVG(stageNumber, animated = true) {
    const animClass = animated ? 'plant-gentle-sway' : '';

    // Maceta y tierra base (común y hermosa en todas las etapas)
    const potBase = `
      <!-- Sombra base -->
      <ellipse cx="150" cy="275" rx="75" ry="14" fill="#0f172a" opacity="0.12" />
      
      <!-- Maceta de terracota moderna -->
      <path d="M95 210 L110 270 Q150 280 190 270 L205 210 Z" fill="url(#potGradient)" filter="url(#dropShadow)" />
      <!-- Borde superior de maceta -->
      <path d="M88 200 C88 196 92 194 96 194 L204 194 C208 194 212 196 212 200 L210 212 C210 216 206 218 202 218 L98 218 C94 218 90 216 90 212 Z" fill="url(#potRimGradient)" />
      
      <!-- Tierra fértil con textura -->
      <ellipse cx="150" cy="204" rx="52" ry="11" fill="#451a03" />
      <ellipse cx="150" cy="204" rx="46" ry="8" fill="#5c2605" />
      <!-- Piedritas y brillo de tierra húmeda -->
      <circle cx="135" cy="204" r="2.5" fill="#78350f" opacity="0.8" />
      <circle cx="165" cy="205" r="2" fill="#78350f" opacity="0.8" />
      <circle cx="148" cy="206" r="1.5" fill="#a16207" opacity="0.6" />
    `;

    // Gráficos específicos por etapa
    let plantElements = '';

    switch (stageNumber) {
      case 1:
        // ETAPA 1: SEMILLA (0% - 19%)
        plantElements = `
          <!-- Rayos solares tenues -->
          <g opacity="0.6" class="sunbeam-anim">
            <line x1="150" y1="40" x2="150" y2="85" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-dasharray="4 4" />
            <line x1="110" y1="50" x2="130" y2="90" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-dasharray="4 4" />
            <line x1="190" y1="50" x2="170" y2="90" stroke="#f59e0b" stroke-width="2" stroke-linecap="round" stroke-dasharray="4 4" />
          </g>

          <!-- Pequeño montículo de tierra con la semilla -->
          <ellipse cx="150" cy="198" rx="14" ry="5" fill="#381804" />
          
          <!-- Semilla de hábito con brillo dorado -->
          <g class="pulse-soft">
            <ellipse cx="150" cy="197" rx="6" ry="9" fill="url(#seedGradient)" transform="rotate(15 150 197)" />
            <path d="M149 190 Q152 195 151 204" stroke="#fef08a" stroke-width="1.5" stroke-linecap="round" opacity="0.7" />
            <!-- Destello de vida naciente -->
            <circle cx="152" cy="194" r="2" fill="#fef08a" opacity="0.9" />
          </g>
          
          <!-- Texto insignia -->
          <rect x="105" y="125" width="90" height="26" rx="13" fill="#ffffff" fill-opacity="0.9" stroke="#e2e8f0" stroke-width="1" />
          <text x="150" y="142" font-size="11" font-weight="700" text-anchor="middle" fill="#475569">Semilla lista 🌱</text>
        `;
        break;

      case 2:
        // ETAPA 2: BROTE (20% - 39%)
        plantElements = `
          <!-- Tallo tierno emergiendo -->
          <g class="${animClass}">
            <!-- Tallo verde tierno -->
            <path d="M150 200 Q150 175 147 160 Q145 150 148 140" fill="none" stroke="url(#stemLightGradient)" stroke-width="4.5" stroke-linecap="round" />
            
            <!-- Hoja izquierda (cotiledón) -->
            <path d="M147 150 Q120 140 122 155 Q135 162 147 154" fill="url(#leafLightGradient)" filter="url(#softShadow)" />
            <line x1="147" y1="152" x2="127" y2="150" stroke="#86efac" stroke-width="1" stroke-linecap="round" />
            
            <!-- Hoja derecha (cotiledón) -->
            <path d="M148 142 Q175 130 174 148 Q160 156 148 145" fill="url(#leafGradient)" filter="url(#softShadow)" />
            <line x1="148" y1="144" x2="168" y2="141" stroke="#86efac" stroke-width="1" stroke-linecap="round" />

            <!-- Gotita de rocío matutino -->
            <ellipse cx="127" cy="148" rx="2" ry="2.5" fill="#e0f2fe" opacity="0.9" />
            <circle cx="126.5" cy="147.5" r="0.8" fill="#ffffff" />
          </g>

          <rect x="100" y="100" width="100" height="26" rx="13" fill="#ffffff" fill-opacity="0.9" stroke="#bbf7d0" stroke-width="1" />
          <text x="150" y="117" font-size="11" font-weight="700" text-anchor="middle" fill="#15803d">¡Brote fuerte! 🌿</text>
        `;
        break;

      case 3:
        // ETAPA 3: PLANTA PEQUEÑA (40% - 59%)
        plantElements = `
          <g class="${animClass}">
            <!-- Tallo central consolidado -->
            <path d="M150 200 Q148 165 153 130 Q155 105 150 85" fill="none" stroke="url(#stemGradient)" stroke-width="6" stroke-linecap="round" />
            
            <!-- Hoja inferior izquierda -->
            <path d="M150 170 Q105 160 110 182 Q130 188 149 173" fill="url(#leafDarkGradient)" filter="url(#softShadow)" />
            
            <!-- Hoja inferior derecha -->
            <path d="M151 155 Q195 145 190 170 Q170 175 152 158" fill="url(#leafGradient)" filter="url(#softShadow)" />

            <!-- Hoja media izquierda más grande -->
            <path d="M152 135 Q105 110 112 135 Q135 145 152 138" fill="url(#leafLightGradient)" filter="url(#softShadow)" />
            <line x1="152" y1="136" x2="116" y2="124" stroke="#bbf7d0" stroke-width="1.2" stroke-linecap="round" />
            
            <!-- Hoja media derecha -->
            <path d="M153 115 Q198 95 194 120 Q175 130 154 118" fill="url(#leafGradient)" filter="url(#softShadow)" />
            <line x1="153" y1="117" x2="188" y2="108" stroke="#86efac" stroke-width="1.2" stroke-linecap="round" />

            <!-- Hojitas superiores nuevas en crecimiento -->
            <path d="M150 86 Q140 70 148 60 Q156 70 151 86" fill="url(#leafLightGradient)" />
            <path d="M150 86 Q165 72 160 62 Q150 72 150 86" fill="url(#leafLightGradient)" />
          </g>

          <rect x="95" y="42" width="110" height="26" rx="13" fill="#ffffff" fill-opacity="0.9" stroke="#86efac" stroke-width="1" />
          <text x="150" y="59" font-size="11" font-weight="700" text-anchor="middle" fill="#166534">Planta pequeña 🪴</text>
        `;
        break;

      case 4:
        // ETAPA 4: PLANTA DESARROLLADA (60% - 79%)
        plantElements = `
          <g class="${animClass}">
            <!-- Tallo robusto y curvado con gracia -->
            <path d="M150 200 Q146 160 154 120 Q156 80 150 55" fill="none" stroke="url(#stemGradient)" stroke-width="7.5" stroke-linecap="round" />
            
            <!-- Ramas laterales -->
            <path d="M149 160 Q120 150 95 165" fill="none" stroke="url(#stemGradient)" stroke-width="4.5" stroke-linecap="round" />
            <path d="M152 140 Q180 130 205 142" fill="none" stroke="url(#stemGradient)" stroke-width="4.5" stroke-linecap="round" />
            
            <!-- Follaje denso y frondoso -->
            <path d="M95 165 Q70 150 75 178 Q100 188 98 165" fill="url(#leafDarkGradient)" filter="url(#softShadow)" />
            <path d="M125 155 Q95 125 105 152 Q120 162 127 157" fill="url(#leafGradient)" filter="url(#softShadow)" />
            
            <path d="M205 142 Q230 128 225 155 Q200 165 203 142" fill="url(#leafDarkGradient)" filter="url(#softShadow)" />
            <path d="M175 135 Q205 108 195 135 Q180 145 173 137" fill="url(#leafGradient)" filter="url(#softShadow)" />

            <path d="M152 110 Q105 85 115 112 Q138 122 153 113" fill="url(#leafLightGradient)" filter="url(#softShadow)" />
            <path d="M154 90 Q198 68 190 95 Q170 105 153 93" fill="url(#leafGradient)" filter="url(#softShadow)" />
            
            <!-- Hojas de la corona -->
            <path d="M150 65 Q125 40 135 60 Q145 70 150 65" fill="url(#leafLightGradient)" />
            <path d="M150 65 Q175 40 165 60 Q155 70 150 65" fill="url(#leafLightGradient)" />

            <!-- Capullos florales a punto de abrir (promesa de flor) -->
            <g class="pulse-soft">
              <ellipse cx="140" cy="50" rx="6" ry="8" fill="url(#budGradient)" transform="rotate(-20 140 50)" />
              <path d="M136 54 Q140 44 144 54" stroke="#fb7185" stroke-width="1.5" fill="none" />
              
              <ellipse cx="160" cy="48" rx="6" ry="8" fill="url(#budGradient)" transform="rotate(20 160 48)" />
              <path d="M156 52 Q160 42 164 52" stroke="#fb7185" stroke-width="1.5" fill="none" />
            </g>
          </g>

          <rect x="85" y="15" width="130" height="26" rx="13" fill="#ffffff" fill-opacity="0.9" stroke="#4ade80" stroke-width="1" />
          <text x="150" y="32" font-size="11" font-weight="700" text-anchor="middle" fill="#15803d">Planta desarrollada 🌳</text>
        `;
        break;

      case 5:
        // ETAPA 5: PLANTA FLORECIDA (80% - 100%)
        plantElements = `
          <!-- Resplandor áureo y partículas festivas -->
          <circle cx="150" cy="80" r="75" fill="url(#goldenHalo)" opacity="0.65" />
          
          <!-- Mariposas mágicas revoloteando -->
          <g class="butterfly-flight-1">
            <path d="M75 75 Q70 65 80 68 Q75 75 75 75" fill="#f43f5e" />
            <path d="M75 75 Q70 85 80 82 Q75 75 75 75" fill="#fb7185" />
          </g>
          <g class="butterfly-flight-2">
            <path d="M225 65 Q230 55 220 58 Q225 65 225 65" fill="#8b5cf6" />
            <path d="M225 65 Q230 75 220 72 Q225 65 225 65" fill="#a78bfa" />
          </g>

          <!-- Planta completa y frondosa -->
          <g class="${animClass}">
            <!-- Tallo majestuoso -->
            <path d="M150 200 Q146 160 153 120 Q155 85 150 65" fill="none" stroke="url(#stemGradient)" stroke-width="8" stroke-linecap="round" />
            
            <!-- Ramas laterales cargadas -->
            <path d="M149 160 Q115 145 85 160" fill="none" stroke="url(#stemGradient)" stroke-width="5" stroke-linecap="round" />
            <path d="M152 140 Q185 125 215 138" fill="none" stroke="url(#stemGradient)" stroke-width="5" stroke-linecap="round" />
            
            <!-- Hojas de fondo y relieve -->
            <path d="M85 160 Q55 145 62 175 Q90 185 88 160" fill="url(#leafDarkGradient)" filter="url(#softShadow)" />
            <path d="M120 152 Q85 120 100 150 Q115 160 122 154" fill="url(#leafGradient)" filter="url(#softShadow)" />
            
            <path d="M215 138 Q245 122 238 152 Q210 162 213 138" fill="url(#leafDarkGradient)" filter="url(#softShadow)" />
            <path d="M180 132 Q215 105 200 132 Q185 142 178 134" fill="url(#leafGradient)" filter="url(#softShadow)" />

            <path d="M151 115 Q100 85 110 115 Q135 128 152 118" fill="url(#leafLightGradient)" />
            <path d="M153 95 Q200 70 190 100 Q170 112 152 98" fill="url(#leafLightGradient)" />

            <!-- FLOR CENTRAL FLORECIDA Y RADIANTE -->
            <g transform="translate(150, 60)" class="bloom-pulse">
              <!-- Pétalos externos -->
              <circle cx="0" cy="-22" r="13" fill="url(#petalGradient)" />
              <circle cx="21" cy="-7" r="13" fill="url(#petalGradient)" />
              <circle cx="13" cy="18" r="13" fill="url(#petalGradient)" />
              <circle cx="-13" cy="18" r="13" fill="url(#petalGradient)" />
              <circle cx="-21" cy="-7" r="13" fill="url(#petalGradient)" />

              <!-- Pétalos internos luminosos -->
              <circle cx="0" cy="-14" r="9" fill="#fda4af" />
              <circle cx="13" cy="-4" r="9" fill="#fda4af" />
              <circle cx="8" cy="12" r="9" fill="#fda4af" />
              <circle cx="-8" cy="12" r="9" fill="#fda4af" />
              <circle cx="-13" cy="-4" r="9" fill="#fda4af" />

              <!-- Centro dorado radiante -->
              <circle cx="0" cy="0" r="10" fill="url(#goldCenterGradient)" />
              <circle cx="0" cy="0" r="6" fill="#fef08a" />
              <!-- Detalles de polen -->
              <circle cx="-3" cy="-2" r="1.5" fill="#ca8a04" />
              <circle cx="3" cy="-1" r="1.5" fill="#ca8a04" />
              <circle cx="0" cy="3" r="1.5" fill="#ca8a04" />
            </g>

            <!-- Flor secundaria izquierda -->
            <g transform="translate(85, 145) scale(0.65)">
              <circle cx="0" cy="-15" r="10" fill="#f43f5e" />
              <circle cx="14" cy="-5" r="10" fill="#f43f5e" />
              <circle cx="9" cy="12" r="10" fill="#f43f5e" />
              <circle cx="-9" cy="12" r="10" fill="#f43f5e" />
              <circle cx="-14" cy="-5" r="10" fill="#f43f5e" />
              <circle cx="0" cy="0" r="7" fill="#facc15" />
            </g>

            <!-- Flor secundaria derecha -->
            <g transform="translate(215, 125) scale(0.65)">
              <circle cx="0" cy="-15" r="10" fill="#f43f5e" />
              <circle cx="14" cy="-5" r="10" fill="#f43f5e" />
              <circle cx="9" cy="12" r="10" fill="#f43f5e" />
              <circle cx="-9" cy="12" r="10" fill="#f43f5e" />
              <circle cx="-14" cy="-5" r="10" fill="#f43f5e" />
              <circle cx="0" cy="0" r="7" fill="#facc15" />
            </g>
          </g>

          <!-- Corona de logro -->
          <rect x="85" y="8" width="130" height="26" rx="13" fill="#ffffff" fill-opacity="0.95" stroke="#f59e0b" stroke-width="1.5" />
          <text x="150" y="25" font-size="11" font-weight="800" text-anchor="middle" fill="#b45309">¡Planta Florecida! 🌸👑</text>
        `;
        break;
    }

    return `
      <svg viewBox="0 0 300 300" class="w-full h-full max-h-[320px] select-none mx-auto drop-shadow-md" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <!-- Gradientes de Maceta -->
          <linearGradient id="potGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#ea580c" />
            <stop offset="50%" stop-color="#c2410c" />
            <stop offset="100%" stop-color="#9a3412" />
          </linearGradient>
          <linearGradient id="potRimGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stop-color="#fb923c" />
            <stop offset="100%" stop-color="#c2410c" />
          </linearGradient>

          <!-- Gradientes de Hojas y Tallo -->
          <linearGradient id="stemGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stop-color="#15803d" />
            <stop offset="50%" stop-color="#16a34a" />
            <stop offset="100%" stop-color="#15803d" />
          </linearGradient>
          <linearGradient id="stemLightGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stop-color="#4ade80" />
            <stop offset="100%" stop-color="#22c55e" />
          </linearGradient>
          <linearGradient id="leafGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#4ade80" />
            <stop offset="100%" stop-color="#15803d" />
          </linearGradient>
          <linearGradient id="leafLightGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#86efac" />
            <stop offset="100%" stop-color="#22c55e" />
          </linearGradient>
          <linearGradient id="leafDarkGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#16a34a" />
            <stop offset="100%" stop-color="#14532d" />
          </linearGradient>

          <!-- Semilla y Flores -->
          <linearGradient id="seedGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#d97706" />
            <stop offset="100%" stop-color="#78350f" />
          </linearGradient>
          <linearGradient id="budGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#f43f5e" />
            <stop offset="100%" stop-color="#be123c" />
          </linearGradient>
          <linearGradient id="petalGradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stop-color="#fb7185" />
            <stop offset="60%" stop-color="#e11d48" />
            <stop offset="100%" stop-color="#be123c" />
          </linearGradient>
          <radialGradient id="goldCenterGradient" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="#fef08a" />
            <stop offset="60%" stop-color="#facc15" />
            <stop offset="100%" stop-color="#ca8a04" />
          </radialGradient>
          <radialGradient id="goldenHalo" cx="50%" cy="50%" r="50%">
            <stop offset="0%" stop-color="#fde047" stop-opacity="0.4" />
            <stop offset="60%" stop-color="#facc15" stop-opacity="0.15" />
            <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
          </radialGradient>

          <!-- Filtros de Sombra -->
          <filter id="dropShadow" x="-10%" y="-10%" width="130%" height="130%">
            <feDropShadow dx="0" dy="4" stdDeviation="4" flood-opacity="0.15" />
          </filter>
          <filter id="softShadow" x="-10%" y="-10%" width="120%" height="120%">
            <feDropShadow dx="0" dy="2" stdDeviation="2" flood-opacity="0.1" />
          </filter>
        </defs>

        <!-- Base común de la maceta y tierra -->
        ${potBase}

        <!-- Elementos botánicos de la etapa -->
        ${plantElements}
      </svg>
    `;
  }

  /**
   * Efecto interactivo de regar la planta
   */
  function waterPlant(containerId, onWateredCallback) {
    SoundFX.playWaterSound();

    const container = document.getElementById(containerId);
    if (!container) return;

    // Crear gotas animadas sobre la planta
    for (let i = 0; i < 7; i++) {
      const drop = document.createElement('div');
      drop.className = 'water-droplet-anim';
      drop.style.left = `${35 + Math.random() * 30}%`;
      drop.style.top = `${20 + Math.random() * 15}%`;
      drop.style.animationDelay = `${i * 0.08}s`;
      container.appendChild(drop);

      setTimeout(() => {
        drop.remove();
      }, 900);
    }

    // Efecto de rebote alegre en el contenedor de la planta
    const svgWrapper = container.querySelector('.plant-svg-wrapper');
    if (svgWrapper) {
      svgWrapper.classList.remove('plant-water-bounce');
      void svgWrapper.offsetWidth; // trigger reflow
      svgWrapper.classList.add('plant-water-bounce');
    }

    if (typeof onWateredCallback === 'function') {
      onWateredCallback();
    }
  }

  return {
    calculatePlantState,
    renderPlantSVG,
    waterPlant,
    SoundFX
  };
})();

// Exportar en window
if (typeof window !== 'undefined') {
  window.VirtualPlant = VirtualPlant;
}
