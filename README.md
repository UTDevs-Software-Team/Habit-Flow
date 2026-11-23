# 🌱 HabitFlow: Especificación Funcional y Programa de Hábitos de Estudio (21 Días)

**Plataforma Web Interactiva SPA construida con React, Tailwind CSS y Metodología Científica de Micro-Hábitos**

---

## 🎯 1. Propósito y Hábito Activo

HabitFlow está diseñado específicamente para erradicar la resistencia psicológica inicial al estudiar mediante el método de **micro-pasos progresivos**, temporizadores de foco sostenido y refuerzo biológico virtual.

- **Hábito Activo:** *"Vencer la procrastinación e iniciar sesiones de estudio efectivas"*
- **Duración del Método:** 21 Días estructurados secuencialmente en 3 Etapas.
- **Público Objetivo:** Estudiantes y profesionales que buscan eliminar la fricción al iniciar tareas complejas.

---

## 🏛️ 2. Arquitectura de la Aplicación (SPA)

La aplicación es una Single Page Application (SPA) con navegación superior en tiempo real y persistencia total en `localStorage` de HTML5:

1. **Vista 1: Dashboard y Calendario General**
   - Banner del hábito activo e indicador de la etapa actual.
   - Barra de progreso global y resumen estadístico.
   - **Planta Virtual** botánica con evolución interactiva en 5 fases según los días completados.
   - **Tarjeta Destacada: "Mi Próximo Paso"** (generada a partir del Día 9) para reanudar el estudio sin fricción.
   - **Calendario Interactivo de 21 Días:** Organizado en las 3 etapas con candados visuales, estados de progreso y acceso a días finalizados.

2. **Vista 2: Pantalla de Actividad Diaria (Checklist y Temporizador)**
   - Barra superior con navegación entre días y botón de retorno al calendario.
   - **Modo Solo Lectura:** Al ingresar a cualquier día previamente finalizado, los campos se protegen para consulta sin riesgo de sobreescritura.
   - **Porcentaje Dinámico Diario:** Calculado en tiempo real según las acciones requeridas del día.
   - **Formularios e Inputs Específicos** para cada uno de los 21 días.
   - **Temporizador Nativo en React:** Implementado con `setInterval` y limpieza estricta en el ciclo de vida, soporte para secuencias multi-bloque (10-3-5 y 10-3-10) y timbre sintetizado mediante la Web Audio API.
   - **Botón "Finalizar Día":** Bloqueado al 100% hasta completar todas las acciones del día; al guardarse, dispara confeti visual, actualiza las métricas y desbloquea el Día $N+1$.

3. **Vista 3: Reporte de Progreso y Métricas Objetivas**
   - 5 indicadores verificables en tiempo real:
     1. **Actividades Completadas:** Días finalizados sobre 21 ($P\%$).
     2. **Tiempo de Enfoque Acumulado:** Minutos reales de temporizadores que llegaron a 00:00.
     3. **Sesiones Iniciadas vs. Terminadas:** Tasa porcentual de finalización de sesiones.
     4. **Tareas Registradas y Pasos Completados:** Conteo objetivo de acciones desglosadas.
     5. **Porcentaje de Puntualidad de Inicio:** Cálculo con tolerancia de $\le 15$ minutos respecto a horas programadas.
   - **Nota Aclaratoria Oficial:**
     > *"La aplicación mide acciones verificables dentro del sistema (registros guardados, temporizadores finalizados y marcas de tiempo), reportando sesiones registradas y actividades confirmadas."*
   - Historial de auditoría completo con tabla cronológica de cada día.
   - Herramientas de exportación de reporte en JSON y reinicio seguro del ciclo.

---

## 🔒 3. Reglas de Negocio y Lógica de Bloqueo

1. **Desbloqueo Secuencial Estricto:**
   - El Día $N+1$ **permanece bloqueado con un icono de candado** hasta que el Día $N$ alcance el 100% de cumplimiento y sea guardado mediante el botón "Finalizar Día".
2. **Consulta en Modo Solo Lectura:**
   - Los días completados quedan accesibles con un distintivo verde (✅) y banner protector de solo lectura para consultar notas, respuestas y tiempos previos.
3. **Cálculo del Porcentaje Diario:**
   $$\text{Porcentaje Diario} = \left(\frac{\text{Acciones Completadas del Día}}{\text{Total de Acciones Requeridas del Día}}\right) \times 100$$
   *El botón "Finalizar Día" está deshabilitado mientras este porcentaje sea menor al 100%.*
4. **Cálculo del Porcentaje General:**
   $$\text{Porcentaje General} = \left(\frac{\text{Días Completados}}{21}\right) \times 100$$

---

## 🪴 4. Evolución Botánica: La Planta Virtual

La planta visualiza tu consistencia biológica y evoluciona en 5 fases ilustradas con SVG interactivo:

| Fase | Rango de Días | Nombre de la Fase | Representación Visual |
|:---:|:---:|:---|:---|
| **1** | **Días 1 a 3** | **Semilla Latente / Germinando** | Maceta de terracota, tierra fértil, semilla dorada brillante y primeros destellos de energía. |
| **2** | **Días 4 a 7** | **Brote Emergente** | Tierno tallo verde esmeralda abriéndose con dos hojas cotiledóneas y rocío matutino. |
| **3** | **Días 8 a 14** | **Tallo Firme con Hojas** | Tallo erguido con 6 a 8 hojas vivaces, ramificaciones y raíces visibles en la base. |
| **4** | **Días 15 a 20** | **Floración Radiante** | Follaje frondoso con capullos y flores índigo/violeta abiertas, halo luminoso y mariposas. |
| **5** | **Día 21** | **Árbol Maduro de Estudio** | Árbol majestuoso de copa densa, flores, frutos dorados de logro, estrella en la cúspide y celebración. |

---

## 📋 5. Especificación Detallada del Programa de 21 Días

### 🟦 ETAPA 1: Fundamentos y Micro-pasos (Días 1 a 7)
- **Día 1 (5 min):** Registrar tarea (nombre, categoría: Escuela/Trabajo/Personal, primera acción mínima de fricción cero) + Temporizador de 5 min.
- **Día 2 (5 min):** Descomponer la tarea del Día 1 en 3 pasos ordenados cronológicamente + Temporizador de 5 min.
- **Día 3 (5 min):** Temporizador de 5 min enfocado exclusivamente en el Paso 1 + Confirmación final (Sí/No) + Breve reflexión.
- **Día 4 (5 min):** Checklist de 3 elementos de espacio (espacio despejado, materiales listos, dispositivo preparado) + Temporizador de 5 min.
- **Día 5 (5 min):** Registrar distracción principal detectada, definir regla de mitigación concreta y temporizador de prueba de 5 min.
- **Día 6 (7 min):** Registrar 3 pendientes acumulados, seleccionar la prioridad #1, definir su primer paso y temporizador de 7 min.
- **Día 7 (3 min):** Evaluación de la Semana 1 (calificación 1 a 5 estrellas, mayor logro) + Generación de resumen ejecutivo.

### 🔷 ETAPA 2: Consistencia y Ritmo (Días 8 a 14)
- **Día 8 (8 min):** Definir hora programada de inicio, botón para registrar puntualidad real (tolerancia $\le 15$ min) + Temporizador de 8 min.
- **Día 9 (5 min):** Registrar "Mi próximo paso" y materiales requeridos; se proyecta automáticamente como tarjeta destacada en el inicio + Temporizador de 5 min.
- **Día 10 (10 min):** Objetivo de foco sostenido + Temporizador de 10 min + Reporte de avance obtenido.
- **Día 11 (8 min):** Seleccionar paso pendiente, temporizador de 8 min y actualización de estado (completado, en progreso o complementario).
- **Día 12 (10 min):** Principio de Premack: registrar actividad de ocio deseada, tarea vinculada previa, temporizador de 10 min y confirmación de orden.
- **Día 13 (18 min):** Temporizador guiado secuencial multi-fase (10 min trabajo ➔ 3 min descanso ➔ 5 min trabajo) con timbre sonoro + Evaluación de fatiga.
- **Día 14 (5 min):** Evaluación de la Semana 2: selección de 2 mejores estrategias top y comparativa de facilidad de inicio vs. Semana 1.

### 🟩 ETAPA 3: Consolidación y Autonomía (Días 15 a 21)
- **Día 15 (10 min):** Priorización matricial de 3 tareas (1 a 3), definir acción de la #1 y temporizador de 10 min.
- **Día 16 (5 min):** Registrar tarea evitada, definir micro-acción de 5 min, temporizador de 5 min y confirmación de desbloqueo de inercia.
- **Día 17 (5 min):** Formulario "Para mañana" (tarea principal, primer paso exacto, hora tentativa y checklist de materiales preparados la noche anterior).
- **Día 18 (23 min):** Secuencia guiada doble de enfoque profundo (10 min trabajo ➔ 3 min descanso ➔ 10 min trabajo) + Verificación de ambos bloques.
- **Día 19 (10 min):** Checklist integral de autonomía de 5 puntos (Tarea, Paso 1, Materiales, Hora, Temporizador) + Temporizador de 10 min.
- **Día 20 (5 min):** Selección de 2 estrategias finales + frecuencia semanal (3, 4 o 5 días) ➔ Generar "Plan Personal de Estudio Sostenible".
- **Día 21 (5 min):** Cuestionario de evaluación final de transformación, despliegue de métricas globales, florecimiento del árbol maduro y opciones de descarga/reinicio.

---

## 🚀 6. Guía de Ejecución

La aplicación está diseñada para ser 100% funcional y ligera en cualquier entorno:

### Opción 1: Abrir directamente en el navegador (Sin Node.js ni instalaciones)
Haz doble clic sobre el archivo `index.html`. La aplicación cargará con React 18, Tailwind CSS y persistencia local inmediata.

### Opción 2: Usar el lanzador por lotes incluido en Windows
Haz doble clic sobre `iniciar_habitflow.bat`. Abrirá tu navegador y te ofrecerá levantar el servidor HTTP local en el puerto 8080.

### Opción 3: Servidor ligero con PowerShell
Abre PowerShell en la carpeta del proyecto y ejecuta:
```powershell
.\start_server.ps1
```
Se abrirá automáticamente en `http://localhost:8080/`.

---

## 🎨 7. Paleta y Diseño UX/UI

- **Fondo General:** `#F8FAFC` (Slate 50 claro y limpio).
- **Acento Primario:** Índigo (`#6366f1` / Slate-Indigo) para enfoque, navegación y elementos interactivos.
- **Acento de Logro:** Esmeralda (`#10b981` / Growth) para días completados, planta virtual y estados exitosos.
- **Tipografía:** *Plus Jakarta Sans* y *JetBrains Mono* para temporizadores.
- **Accesibilidad:** Contraste WCAG AA, focus rings claros, badges legibles y feedback sonoro opcional mediante Web Audio API.
