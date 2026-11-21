/**
 * Codigo anadido por Felipe Denis
 *
 * Caja negra aislada para verificacion telefonica por SMS con Firebase PhoneAuthProvider.
 */
package com.utdevs.habitflow.data.repository

import android.app.Activity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

object PhoneVerificationHelper {

    fun sendVerificationCode(
        activity: Activity,
        phoneNumberE164: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null,
        onCodeSent: (verificationId: String, token: PhoneAuthProvider.ForceResendingToken) -> Unit,
        onAutoVerified: (PhoneAuthCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = Firebase.auth
        auth.useAppLanguage()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onAutoVerified(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val raw = e.localizedMessage.orEmpty()
                val msg = when {
                    raw.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                        "Activa 'Telefono' en Firebase Console > Authentication > Sign-in method y agrega tu SHA-1/SHA-256."
                    e is FirebaseAuthInvalidCredentialsException ->
                        "Numero telefonico invalido. Usa formato E.164 (ej. +5215512345678)."
                    e is FirebaseTooManyRequestsException ->
                        "Cuota de SMS excedida temporalmente. Intenta mas tarde o usa un numero de prueba."
                    e is FirebaseAuthMissingActivityForRecaptchaException ->
                        "Se requiere una Activity activa para verificar reCAPTCHA."
                    else -> raw.ifBlank { "No se pudo enviar el codigo SMS." }
                }
                onError(msg)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                onCodeSent(verificationId, token)
            }
        }

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumberE164.trim())
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }
}
