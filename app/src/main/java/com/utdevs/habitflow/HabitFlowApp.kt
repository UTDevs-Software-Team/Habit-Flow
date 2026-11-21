/**
 * Codigo anadido por Felipe Denis
 *
 * Clase Application de HabitFlow. Configura cache persistente offline en Cloud Firestore
 * e inicializa el contenedor de dependencias.
 */
package com.utdevs.habitflow

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.utdevs.habitflow.di.AppContainer

class HabitFlowApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val settings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings { })
        }
        Firebase.firestore.firestoreSettings = settings
        container = AppContainer(applicationContext)
    }
}
