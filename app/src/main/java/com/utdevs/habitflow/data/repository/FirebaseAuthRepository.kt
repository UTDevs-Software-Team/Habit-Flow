/**
 * Codigo anadido por Felipe Denis
 *
 * Implementacion Black Box de AuthRepository usando FirebaseAuth (Correo, Google y Telefono SMS).
 */
package com.utdevs.habitflow.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.utdevs.habitflow.domain.repository.AuthRepository
import com.utdevs.habitflow.domain.repository.AuthUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    override val currentUserFlow: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> =
        runCatching {
            val res = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = res.user ?: error("Usuario no disponible tras iniciar sesion.")
            user.toAuthUser()
        }.recoverCatching { throw mapAuthError(it) }

    override suspend fun signUpWithEmail(email: String, password: String): Result<AuthUser> =
        runCatching {
            val res = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = res.user ?: error("Usuario no disponible tras registro.")
            user.toAuthUser()
        }.recoverCatching { throw mapAuthError(it) }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser> =
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))

    override suspend fun signInWithPhoneCode(verificationId: String, smsCode: String): Result<AuthUser> =
        signInWithCredential(PhoneAuthProvider.getCredential(verificationId, smsCode.trim()))

    suspend fun signInWithCredential(credential: AuthCredential): Result<AuthUser> =
        runCatching {
            val res = auth.signInWithCredential(credential).await()
            val user = res.user ?: error("Usuario no disponible tras autenticacion.")
            user.toAuthUser()
        }.recoverCatching { throw mapAuthError(it) }

    override fun signOut() {
        auth.signOut()
    }

    private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(
        uid = uid,
        email = email.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        displayName = displayName.orEmpty()
    )

    private fun mapAuthError(error: Throwable): IllegalStateException {
        val raw = error.localizedMessage.orEmpty()
        val message = when {
            raw.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                "Firebase Auth no esta habilitado en habit-flow-fb095. Abre Firebase Console > Authentication > Comenzar y activa los metodos de acceso."
            error is FirebaseAuthWeakPasswordException -> "La contrasena debe tener al menos 6 caracteres."
            error is FirebaseAuthInvalidCredentialsException -> "Credenciales o codigo invalidos."
            error is FirebaseAuthUserCollisionException -> "Ya existe una cuenta registrada con estos datos."
            error is FirebaseAuthInvalidUserException -> "La cuenta no existe o fue deshabilitada."
            else -> raw.ifBlank { "Error de autenticacion en Firebase." }
        }
        return IllegalStateException(message, error)
    }
}
