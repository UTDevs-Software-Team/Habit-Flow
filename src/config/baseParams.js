/**
 * ============================================================================
 * MODULO BLACK BOX: PARAMETROS BASE CENTRALIZADOS
 * ============================================================================
 * Contrato:
 * - Expone `window.HABITFLOW_BASE_PARAMS` de forma inmutable/global antes de
 *   cargar el dominio y los componentes React.
 * - Modifica aqui dias totales, meta inicial, margen de puntualidad o las
 *   duraciones (indices 0..20 = Dia 1..21) sin tocar la logica de UI.
 */
const HABITFLOW_BASE_PARAMS = {
  TOTAL_DAYS: 21,
  DEFAULT_HABIT: 'Vencer la procrastinacion e iniciar sesiones de estudio efectivas',
  DEFAULT_MAGNITUDE: 'mediano', // 'chico' | 'mediano' | 'grande'
  PUNCTUALITY_WINDOW_MINUTES: 15,
  // Arreglos de 21 numeros: indice 0 = Dia 1 ... indice 20 = Dia 21 (en minutos)
  DURATIONS: {
    chico:   [2, 3, 3, 3, 3, 4, 2,  5, 3,  5,  5,  5,  8, 3,  5,  4, 3, 10,  5, 3, 3],
    mediano: [5, 5, 5, 5, 5, 7, 3,  8, 5, 10,  8, 10, 18, 5, 10,  5, 5, 23, 10, 5, 5],
    grande:  [5, 7, 8, 6, 8, 10, 5, 12, 8, 15, 12, 15, 25, 7, 15, 10, 8, 30, 15, 8, 8]
  }
};

// --- Web Audio Synthetic Chime Player ---

window.HABITFLOW_BASE_PARAMS = HABITFLOW_BASE_PARAMS;
