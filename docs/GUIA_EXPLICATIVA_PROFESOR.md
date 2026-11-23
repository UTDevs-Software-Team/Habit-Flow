# HabitFlow: Dossier Tecnico y Guia de Exposicion Academica

**Proyecto:** Plataforma Web Interactiva SPA para la Creacion de Habitos Calibrados (21 Dias)  
**Formato de Entrega:** Aplicacion Web Monobloque autocontenida en `index.html`  
**Tecnologias:** React 18 (CDN), Tailwind CSS (CDN), HTML5 `localStorage`, Web Audio API, SVG Parametrico  
**Enfoque Central:** Calibracion dinamica de 3 calendarios segun la magnitud del habito (*Chico, Mediano, Grande*), catalogo CSV simple de micro-pasos y modo claro/oscuro.

---

## 1. Proposito Fundamental y Justificacion de las 3 Magnitudes

### Por que no todos los habitos pueden durar lo mismo
Un error comun en aplicaciones de productividad es exigir 25 minutos fijos desde el Dia 1 a cualquier meta: pedir 25 minutos para tender la cama es desproporcionado, mientras que dedicar 2 minutos a resolver calculo diferencial no alcanza para entrar en concentracion.

HabitFlow calibra los tiempos del sprint de 21 dias en 3 niveles:

1. **Habito Chico (Baja complejidad / Fisico rapido):**
   - *Ejemplos:* Ordenar el cuarto, tender la cama, hidratacion, recoger el escritorio, 5 estiramientos.
   - *Logica conductual:* La barrera es la inercia de empezar. Lapsos de **2 a 5 minutos** en Etapa 1 y consolidacion de **5 a 10 minutos** (Total planeado: 87 min).
2. **Habito Mediano (Complejidad moderada / Rutina diaria):**
   - *Ejemplos:* Lectura de libros, repasar apuntes, practicar idioma, diario reflexivo, ejercicio ligero.
   - *Logica conductual:* Progresion equilibrada de **5 a 10 minutos** en Etapas 1 y 2, alcanzando **10 a 23 minutos** en la etapa final (Total planeado: 182 min).
3. **Habito Grande (Alta demanda cognitiva / Estudio profundo):**
   - *Ejemplos:* Estudiar calculo/fisica/programacion, redactar tesis o ensayos, resolver guias analiticas.
   - *Logica conductual:* Inicia con **5 a 8 minutos** para bajar la friccion psicologica y escala hacia bloques de **15 a 30 minutos** con pausas guiadas (Total planeado: 257 min).

---

## 2. Tabla Comparativa de los 3 Calendarios (21 Dias)

| Dia | Actividad Central | Chico | Mediano | Grande |
|:---:|:---|:---:|:---:|:---:|
| **1** | Primera accion minima | 2 min | 5 min | 5 min |
| **2** | 3 pasos ordenados | 3 min | 5 min | 7 min |
| **3** | Ejecucion de Paso 1 | 3 min | 5 min | 8 min |
| **4** | Espacio y materiales | 3 min | 5 min | 6 min |
| **5** | Control de distraccion | 3 min | 5 min | 8 min |
| **6** | Prioridad #1 | 4 min | 7 min | 10 min |
| **7** | Evaluacion Semana 1 | 2 min | 3 min | 5 min |
| **8** | Hora fija y puntualidad | 5 min | 8 min | 12 min |
| **9** | Proximo paso destacado | 3 min | 5 min | 8 min |
| **10** | Enfoque sostenido | 5 min | 10 min | 15 min |
| **11** | Traccion en pendiente | 5 min | 8 min | 12 min |
| **12** | Principio de Premack | 5 min | 10 min | 15 min |
| **13** | Temporizador secuencial | 8 min (4-1-3) | 18 min (10-3-5) | 25 min (15-3-7) |
| **14** | Evaluacion Semana 2 | 3 min | 5 min | 7 min |
| **15** | Priorizacion 1 a 3 | 5 min | 10 min | 15 min |
| **16** | Confrontar tarea evitada | 4 min | 5 min | 10 min |
| **17** | Preparacion para manana | 3 min | 5 min | 8 min |
| **18** | Doble bloque profundo | 10 min (4-2-4) | 23 min (10-3-10) | 30 min (12-4-14) |
| **19** | Checklist 5 puntos | 5 min | 10 min | 15 min |
| **20** | Plan personal sostenible | 3 min | 5 min | 8 min |
| **21** | Evaluacion final y graduacion | 3 min | 5 min | 8 min |

---

## 3. Guia de Cambios en Vivo (Si el profesor pide modificar parametros)

Todo el codigo ejecutable vive en un solo archivo: `index.html`. Para hacer cambios rapidos frente al profesor sin riesgo de romper la interfaz:

1. **Cambiar duraciones, dias totales, habito inicial o tolerancia de puntualidad:**
   - Abre `index.html` y busca `[SECCION 1]` (`HABITFLOW_BASE_PARAMS`) justo al principio de `<script type="text/babel">`.
   - Para cambiar la duracion de un dia especifico, edita la posicion `dia - 1` en `DURATIONS.chico`, `DURATIONS.mediano` o `DURATIONS.grande` (ejemplo: para cambiar el Dia 5 de `grande`, modifica el quinto numero del arreglo `grande`).
   - Para cambiar el margen de puntualidad (actualmente `15` minutos), edita `PUNCTUALITY_WINDOW_MINUTES`.
   - Para cambiar la meta inicial que aparece al abrir la app por primera vez, edita `DEFAULT_HABIT` o `DEFAULT_MAGNITUDE`.
   - Guarda `index.html` y recarga la pestana del navegador (`F5`).

2. **Agregar o editar una meta en el catalogo CSV de micro-pasos:**
   - Busca `[SECCION 5]` (`EMBEDDED_MICRO_PASOS_CSV`) dentro de `index.html`.
   - Cada fila tiene exactamente 6 columnas separadas por coma: `id,meta,paso_1,paso_2,paso_3,paso_4`.
   - Agrega una linea nueva siguiendo ese mismo formato (sin comas internas dentro de los textos), guarda y recarga (`F5`). La funcion `parseMicroPasosCsv` la carga automaticamente en el selector de Vista 2.

3. **Cambiar entre modo claro y modo oscuro:**
   - Usa el boton `Oscuro` / `Claro` en la barra superior derecha. Se guarda en `localStorage` (`habitflow_dark_mode`).

---

## 4. Reglas de Negocio y Logica de Bloqueo

1. **Desbloqueo secuencial estricto:** El Dia `N + 1` permanece bloqueado hasta completar el 100% de las acciones del Dia `N` y guardar con `Finalizar Dia`.
2. **Modo solo lectura:** Los dias ya finalizados muestran un banner protector y deshabilitan inputs para conservar el historial intacto.
3. **Porcentaje diario dinamico:** El boton `Finalizar Dia` solo se habilita cuando `Porcentaje Diario = (Acciones Completadas / Total de Acciones) * 100` llega a `100%`.
4. **Planta virtual en 5 fases:** Semilla (0-3 dias) -> Brote (4-7 dias) -> Tallo con hojas (8-14 dias) -> Floracion (15-20 dias) -> Arbol maduro (21 dias).

---

## 5. Organizacion del Repositorio para Exposicion

Para evitar confusiones durante la revision del codigo:
- **Raiz:** Solo contiene `index.html` (la aplicacion real), los lanzadores (`iniciar_habitflow.bat`, `start_server.ps1`) y `README.md`.
- **`docs/`:** Contiene `EXPLICACION_TECNICA_DEL_CODIGO.md` y esta `GUIA_EXPLICATIVA_PROFESOR.md`.
- **`_archivo_no_usado/`:** Contiene archivos legados y pruebas antiguas (`legacy_7days/`, `src/`, `css/`, `assets/`, `package.json`, `test_21days.html`, `test_habitflow.html`) que la pagina actual NO utiliza.

---

## 6. Guion Breve para Responder al Profesor

> *"Profesor, mantuvimos la arquitectura como una SPA de un solo archivo (`index.html`) para que sea facil de auditar y ejecutar sin dependencias externas. Todos los parametros base --duraciones por dia de las 3 magnitudes, margen de puntualidad y habito inicial-- estan concentrados arriba en `HABITFLOW_BASE_PARAMS`, y las guias de ejemplo se leen de un catalogo CSV embebido (`EMBEDDED_MICRO_PASOS_CSV`). Si necesita cambiar el tiempo de cualquier dia o agregar una meta de ejemplo, se edita una linea en ese bloque y se recarga la pagina."*
