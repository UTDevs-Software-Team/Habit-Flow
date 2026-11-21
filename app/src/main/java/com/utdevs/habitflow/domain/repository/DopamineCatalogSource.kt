/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box para cargar reglas de XP, rangos, logros y mensajes desde assets JSON.
 */
package com.utdevs.habitflow.domain.repository

import com.utdevs.habitflow.domain.model.DopamineCatalog

interface DopamineCatalogSource {
    fun getCatalog(): DopamineCatalog
}
