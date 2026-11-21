# HabitFlow Android

Codigo anadido por Felipe Denis

Aplicacion nativa Android en **100% Kotlin** con **Jetpack Compose (Material 3)** y backend en **Firebase (`habit-flow-fb095`)** para ejecutar el Sprint de Condicionamiento Progresivo contra la Procrastinacion de **21 Dias** (785 minutos / 13.08 horas de enfoque acumulado).

## Arquitectura y Filosofia

- **Data-Driven:** El plan de 21 dias se carga desde `app/src/main/assets/plans/anti_procrastination_21d.json` a traves del contrato `PlanDataSource`.
- **Black Box y Modularidad Absoluta:**
  - `domain/` (modelos, casos de uso puros e interfaces sin dependencias de Android o Firebase).
  - `data/` (mappers, `AssetPlanDataSource`, `FirebaseAuthRepository`, `FirestoreSprintRepository`).
  - `service/` (`FocusTimerService` con `SystemClock.elapsedRealtime()` y `TimerStateHolder`).
  - `ui/` (nodos visuales en Jetpack Compose desacoplados que invocan exclusivamente ViewModels).
- **Aislamiento en Cloud Firestore:** Todos los datos se almacenan bajo `users/{uid}/` (`dailyLogs`, `maintenanceSessions`, `sprintHistory`), protegidos por `firestore.rules`.

## Requisitos de Configuracion

1. Abrir el proyecto en Android Studio.
2. Colocar el archivo `google-services.json` del proyecto Firebase `habit-flow-fb095` (package `com.utdevs.habitflow`) en `app/google-services.json`.
3. Habilitar **Authentication (Correo/Contrasena)** y **Cloud Firestore** en la consola de Firebase.
4. Desplegar las reglas de seguridad de `firestore.rules` antes de pasar a produccion.
5. Compilar y ejecutar pruebas unitarias:
   - `./gradlew testDebugUnitTest`
   - `./gradlew assembleDebug`
