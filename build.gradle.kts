// Codigo anadido por Felipe Denis
// Archivo build.gradle.kts raiz. Declara plugins sin aplicarlos directamente.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}
