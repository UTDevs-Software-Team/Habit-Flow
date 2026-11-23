# 📘 HabitFlow: Dossier Técnico y Guía de Exposición Académica

**Proyecto:** Plataforma Web Interactiva SPA para la Creación de Hábitos Calibrados (21 Días)  
**Autor:** Estudiante / Desarrollador del Proyecto  
**Tecnologías:** React 18, Tailwind CSS, HTML5 LocalStorage, Web Audio API, SVG Interactivo  
**Novedad Implementada:** Calibración dinámica de 3 Calendarios según la Magnitud del Hábito (*Chico, Mediano, Grande*)

---

## 🎯 1. Propósito Fundamental de HabitFlow y Justificación de las 3 Magnitudes

### ¿Por qué no todos los hábitos pueden durar lo mismo?
Un error común en las aplicaciones de productividad es tratar todas las metas con la misma vara: exigir 25 minutos desde el inicio a una persona que quiere tender su cama o recoger su cuarto es absurdo, mientras que dedicar 2 minutos a resolver problemas de cálculo diferencial es insuficiente para generar inmersión cognitiva.

HabitFlow introduce la **Calibración por Magnitud y Complejidad**:
1. 🟢 **Hábito Chico (Baja complejidad / Físico rápido):**
   - *Ejemplos:* Ordenar el cuarto, tender la cama, hidratación, recoger el escritorio, 5 estiramientos.
   - *Lógica conductual:* La barrera no es el esfuerzo mental, sino la inercia de levantarse. Los lapsos van de **2 a 5 minutos** al inicio y se consolidan en rutinas ágiles de **5 a 10 minutos** en etapas avanzadas (Total planeado: 87 min).
2. 🔵 **Hábito Mediano (Complejidad moderada / Rutinas diarias sostenidas):**
   - *Ejemplos:* Lectura de libros, repasar apuntes del día, practicar idioma (Duolingo), diario reflexivo, ejercicio ligero.
   - *Lógica conductual:* Progresión equilibrada de **5 a 10 minutos** en Etapas 1 y 2, alcanzando **10 a 20 minutos** en la etapa de autonomía (Total planeado: 182 min).
3. 🟣 **Hábito Grande (Alta demanda cognitiva / Estudio universitario y proyectos):**
   - *Ejemplos:* Estudiar matemáticas/carrera, redactar tesis o ensayos, programar software, resolver guías analíticas.
   - *Lógica conductual:* Inicia con **5-8 minutos** para romper la intimidación psicológica (fricción cero) y escala hacia bloques profundos de **15 a 30 minutos** con técnica Pomodoro guiada (Total planeado: 257 min).

---

## 📅 2. Definición Comparativa de los 3 Calendarios (21 Días)

| Día | Actividad Central | 🟢 Chico (Orden / Físico) | 🔵 Mediano (Lectura / Rutina) | 🟣 Grande (Estudio / Análisis) |
|:---:|:---|:---:|:---:|:---:|
| **1** | Primera acción mínima | **2 min** | **5 min** | **5 min** |
| **2** | 3 pasos ordenados | **3 min** | **5 min** | **7 min** |
| **3** | Ejecución de Paso 1 | **3 min** | **5 min** | **8 min** |
| **4** | Espacio y materiales | **3 min** | **5 min** | **6 min** |
| **5** | Control de distracción | **3 min** | **5 min** | **8 min** |
| **6** | Prioridad #1 | **4 min** | **7 min** | **10 min** |
| **7** | Evaluación Semana 1 | **2 min** | **3 min** | **5 min** |
| **8** | Hora fija y puntualidad | **5 min** | **8 min** | **12 min** |
| **9** | Próximo paso destacado | **3 min** | **5 min** | **8 min** |
| **10** | Enfoque sostenido | **5 min** | **10 min** | **15 min** |
| **11** | Tracción en pendiente | **5 min** | **8 min** | **12 min** |
| **12** | Principio de Premack | **5 min** | **10 min** | **15 min** |
| **13** | Temporizador secuencial | **8 min** (4m ➔ 1m ➔ 3m) | **18 min** (10m ➔ 3m ➔ 5m) | **25 min** (15m ➔ 3m ➔ 7m) |
| **14** | Evaluación Semana 2 | **3 min** | **5 min** | **7 min** |
| **15** | Priorización 1 a 3 | **5 min** | **10 min** | **15 min** |
| **16** | Confrontar tarea evitada | **4 min** | **5 min** | **10 min** |
| **17** | Preparación para mañana | **3 min** | **5 min** | **8 min** |
| **18** | Doble bloque profundo | **10 min** (4m ➔ 2m ➔ 4m) | **23 min** (10m ➔ 3m ➔ 10m) | **30 min** (12m ➔ 4m ➔ 14m) |
| **19** | Checklist 5 puntos | **5 min** | **10 min** | **15 min** |
| **20** | Plan personal sostenible | **3 min** | **5 min** | **8 min** |
| **21** | Evaluación final y graduación | **3 min** | **5 min** | **8 min** |

---

## 💻 3. Cómo se Condicionó en el Código de la Aplicación

1. **Estado Central en React:**
   - La propiedad `habitMagnitude` almacena `'chico'`, `'mediano'` o `'grande'` en el estado del componente principal y se persiste en `localStorage`.
2. **Función Selectora de Calendario (`getDayConfig`):**
   - Cada vez que se renderiza el calendario, una tarjeta diaria o el temporizador, el sistema consulta los lapsos mapeados específicamente para esa magnitud.
3. **Temporizadores Reactivos:**
   - El componente `NativeTimer` recibe la prop `minutes={config.duration}` y adapta sus intervalos y secuencias de fases (Día 13 y Día 18) de forma reactiva sin desincronizaciones.
4. **Métricas Calibradas:**
   - La Vista 3 compara los minutos de enfoque reales contra la meta planeada de esa magnitud ($87$, $182$ o $257$ minutos).

---

## 🔒 4. Reglas de Negocio y Lógica de Bloqueo

1. **Desbloqueo Secuencial Estricto (Candado 🔒):**
   - El Día $N+1$ **permanece bloqueado** hasta que el Día $N$ alcance el 100% y se guarde.
2. **Modo Consulta / Solo Lectura:**
   - Días finalizados quedan protegidos con banner verde y campos deshabilitados para auditoría.
3. **Porcentaje Diario Dinámico:**
   $$\text{Porcentaje Diario} = \left(\frac{A_{\text{completadas}}}{A_{\text{total}}}\right) \times 100$$
   *El botón "Finalizar Día" está deshabilitado hasta alcanzar el 100%.*
4. **Planta Virtual en 5 Fases:**
   - Semilla (1-3) ➔ Brote (4-7) ➔ Tallo (8-14) ➔ Floración (15-20) ➔ Árbol Maduro (21).

---

## 🎙️ 5. Guion para Explicar las 3 Magnitudes al Profesor

> *"Profesor, agregamos una mejora metodológica fundamental: **la calibración del calendario según la magnitud del hábito**.*
>
> *No es lo mismo vencer la procrastinación para ordenar un cuarto que para estudiar cálculo diferencial. Para ordenar un cuarto, pedir 10 o 25 minutos es desproporcionado: la barrera es física y se supera con micro-acciones ágiles de 2 a 5 minutos. En cambio, en el estudio universitario complejo, se requiere romper la intimidación con 5 minutos iniciales pero escalar de manera guiada hacia bloques Pomodoro de 15, 20 y 30 minutos.*
>
> *Por eso, el usuario puede seleccionar en el registro si su hábito es **Chico**, **Mediano** o **Grande**. El código condiciona al instante los 21 días del calendario, ajustando la duración de cada temporizador, las secuencias de descanso y las métricas acumuladas."*
