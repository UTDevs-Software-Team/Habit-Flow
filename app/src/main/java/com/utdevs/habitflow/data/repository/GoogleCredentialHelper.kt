/**
 * Codigo anadido por Felipe Denis
 *
 * Caja negra aislada para obtener Google ID Token mediante Android Credential Manager
 * usando GetSignInWithGoogleOption (flujo explicito de boton "Continuar con Google").
 */
package com.utdevs.habitflow.data.repository

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleCredentialHelper {

    suspend fun requestGoogleIdToken(activity: Activity): Result<String> = runCatching {
        val resId = activity.resources.getIdentifier(
            "default_web_client_id",
            "string",
            activity.packageName
        )
        if (resId == 0) {
            error(
                "Falta default_web_client_id en google-services.json. Agrega tu SHA-1 en Firebase Console, activa Google en Authentication y vuelve a descargar google-services.json."
            )
        }
        val webClientId = activity.getString(resId)
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val credentialManager = CredentialManager.create(activity)
        val result = credentialManager.getCredential(activity, request)
        val cred = result.credential
        if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            GoogleIdTokenCredential.createFrom(cred.data).idToken
        } else {
            error("Credencial de Google no reconocida.")
        }
    }.recoverCatching { err ->
        val msg = when (err) {
            is GetCredentialCancellationException -> "Inicio de sesion con Google cancelado."
            is NoCredentialException ->
                "No se encontro cuenta de Google disponible en el dispositivo. Verifica que tengas una sesion de Google activa en Ajustes del telefono."
            else -> err.localizedMessage ?: "Error al iniciar sesion con Google."
        }
        throw IllegalStateException(msg, err)
    }
}
