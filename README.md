# HabitFlow 🚀

HabitFlow es una aplicación web interactiva diseñada para ayudar a los usuarios a vencer la procrastinación e iniciar actividades pequeñas mediante un programa estructurado y guiado de **21 días**.

## 📋 Características Principales

- **Programa Progresivo de 21 Días**: Estructurado en 3 etapas semanales consecutivas (Aprender a comenzar, Crear rutina de inicio y Consolidar).
- **Asignación Dinámica por Arrastre**: Lógica inteligente donde los datos ingresados en un día (actividades, micro-pasos, prioridades) alimentan las tareas de los días posteriores.
- **Desbloqueo Secuencial Estricto**: Sistema de avance progresivo que requiere completar el 100% de las tareas diarias para desbloquear el día siguiente.
- **Temporizadores Integrados y Secuenciales**: Relojes regresivos dinámicos de 5 a 23 minutos adaptados a cada estrategia diaria (incluyendo bloques de enfoque y descansos).
- **Visualización de Planta Virtual**: Gamificación del progreso donde una planta evoluciona en 5 etapas a medida que incrementa el porcentaje global acumulado.
- **Métricas Objetivas**: Registro automático e inmutable de eventos (tiempo de enfoque acumulado, puntualidad de inicio, sesiones terminadas y actividades completadas).
- **Persistencia Local**: Todos los avances y respuestas se conservan automáticamente en el almacenamiento del navegador (`localStorage`).

## 🛠️ Tecnologías Usadas

- **Frontend**: HTML5, CSS3 / Tailwind CSS, JavaScript (ES6+) o React
- **Almacenamiento**: LocalStorage API
- **Diseño**: Interfaz responsiva y accesible para escritorio y dispositivos móviles

## 📂 Estructura del Repositorio

```
habitflow/
├── index.html            # Punto de entrada principal
├── css/
│   └── styles.css        # Estilos globales y temas de componentes
├── js/
│   ├── app.js            # Inicialización y control de vistas
│   ├── logic.js          # Lógica de asignación dinámica y reglas de 21 días
│   ├── timer.js          # Controladores de temporizadores y secuencias
│   └── storage.js        # Manejo de persistencia en localStorage
└── README.md             # Documentación del proyecto
```

## 🚀 Guía Rápida de Uso

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/usuario/habitflow.git
   cd habitflow
   ```
2. **Ejecutar la aplicación**:
   Abre el archivo `index.html` en cualquier navegador web moderno o utiliza una extensión de servidor local como *Live Server* en VS Code.
3. **Iniciar el Programa**:
   - Selecciona **"Comenzar Día 1"** en el Panel Principal.
   - Registra tu primera actividad pospuesta para dar inicio a tu Plan de 21 Días.
