/**
 * ============================================================================
 * MODULO BLACK BOX: CATALOGO CSV DE MICRO-PASOS SIMPLES
 * ============================================================================
 * Contrato:
 * - Entrada: cadena CSV (`id,meta,paso_1,paso_2,paso_3,paso_4`).
 * - Salida: `parseMicroPasosCsv(csvText)` retorna `Array<{id, meta, steps}>`.
 * - Autocontenido para funcionar incluso bajo protocolo `file://` sin CORS.
 */
const EMBEDDED_MICRO_PASOS_CSV = [
  'id,meta,paso_1,paso_2,paso_3,paso_4',
  'bajar_de_peso,Bajar de peso y ejercicio,Llenar un vaso de agua y ponerte tenis deportivos,Hacer 5 minutos de calentamiento o caminata suave,Completar 1 serie corta de sentadillas o abdominales,Anotar que comiste hoy sin juzgarte',
  'aprender_programacion,Aprender programacion,Abrir tu editor de codigo y crear un archivo de prueba,Escribir y ejecutar 1 funcion pequena o ejemplo basico,Leer 1 concepto corto (variables o ciclos) y probarlo,Guardar tu avance con un comentario de que sigue manana',
  'tocar_instrumento,Aprender a tocar un instrumento,Sacar el instrumento de su estuche y afinarlo,Practicar 3 minutos de digitacion o acordes basicos lento,Tocar 1 fragmento de 4 compases de una cancion sencilla,Dejar el instrumento visible y listo para manana',
  'aprender_ingles,Practicar ingles diario,Abrir tu app o cuaderno de ingles por 5 minutos,Escribir 3 frases cortas usando vocabulario nuevo,Leer en voz alta un parrafo corto para soltar pronunciacion,Anotar 2 palabras nuevas para repasarlas manana',
  'habito_lectura,Leer todos los dias,Abrir el libro en el separador actual sin distracciones,Leer 3 paginas o 5 minutos seguidos en silencio,Subrayar o anotar 1 idea principal del fragmento,Dejar el libro sobre tu escritorio o mesa de noche',
  'orden_hogar,Orden y limpieza diaria,Recoger 5 objetos fuera de su lugar en tu habitacion,Tender la cama o despejar la superficie de tu escritorio,Sacar basura o vasos acumulados del cuarto,Dejar lista la ropa o mochila del dia siguiente'
].join('\n');

const parseMicroPasosCsv = (csvText) => {
  const lines = (csvText || '').trim().split(/\r?\n/).filter(Boolean);
  if (lines.length <= 1) return [];
  return lines.slice(1).map((line) => {
    const cols = line.split(',').map((c) => c.trim());
    return {
      id: cols[0] || '',
      meta: cols[1] || '',
      steps: cols.slice(2).filter(Boolean)
    };
  }).filter((item) => item.id && item.meta);
};

// [SECCION 6] Selector de configuracion diaria calibrada por magnitud (Chico/Mediano/Grande)

window.EMBEDDED_MICRO_PASOS_CSV = EMBEDDED_MICRO_PASOS_CSV;
window.parseMicroPasosCsv = parseMicroPasosCsv;
