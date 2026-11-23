/**
 * ============================================================================
 * MODULO BLACK BOX: MENTOR DIDACTICO PARA PRINCIPIANTES (SYSTEM PROMPT + FALLBACK)
 * ============================================================================
 * Contrato:
 * - Entrada: `{ goalTitle, focusMinutes, dayNumber, dayRule, previousStep }`.
 * - Salida: `Promise<{ conceptBrief: string, steps: string[], source: string }>`.
 * - Mantiene la politica de cero secretos en repositorio: si el usuario guarda
 *   opcionalmente una clave en `localStorage ('habitflow_gemini_key')`, consulta
 *   Gemini con el System Prompt de principiantes; de lo contrario, responde de
 *   inmediato con el catalogo CSV y explicacion base sin bloquear la sesion.
 */
const BEGINNER_MENTOR_SYSTEM_PROMPT = [
  'Eres un mentor didactico y coach conductual anti-procrastinacion de HabitFlow.',
  'Tu usuario parte DESDE CERO: asume que NO sabe nada previo ni domina jerga tecnica.',
  '1. Explica brevemente en 2-3 oraciones simples el concepto minimo para hoy.',
  '2. Entrega exactamente 3 micro-metas verificables adaptadas a los minutos del bloque.',
  '3. Prohibido dar consejos genericos ("abre tu material", "elimina distracciones").',
  '4. Cero emojis. Responde UNICAMENTE JSON: {"conceptBrief":"...","steps":["...","...","..."]}.'
].join('\n');

const buildBeginnerMentorSuggestion = (ctx) => {
  const topic = (ctx && ctx.goalTitle ? ctx.goalTitle : 'tu habito actual').trim();
  const mins = (ctx && ctx.focusMinutes) ? ctx.focusMinutes : 5;
  return {
    conceptBrief:
      'Para empezar "' + topic + '" desde cero, enfocate en una sola unidad minima comprobable durante ' +
      mins + ' minutos sin buscar perfeccion.',
    steps: [
      'Define 1 concepto o accion base de "' + topic + '" en una frase simple',
      'Practica esa accion minima durante el bloque de ' + mins + ' minutos',
      'Registra el resultado exacto obtenido para retomarlo manana'
    ],
    source: 'offline-mentor'
  };
};

window.BEGINNER_MENTOR_SYSTEM_PROMPT = BEGINNER_MENTOR_SYSTEM_PROMPT;
window.buildBeginnerMentorSuggestion = buildBeginnerMentorSuggestion;
