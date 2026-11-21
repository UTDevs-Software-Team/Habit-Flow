/**
 * Codigo anadido por Felipe Denis
 *
 * Contrato Black Box de autenticacion de usuarios (Correo, Google y Telefono SMS).
 */
package com.utdevs.habitflow.domain.repository

import kotlinx.coroutines.flow.Flow

data class AuthUser(
    val uid: String,
    val email: String,
    val phoneNumber: String = "",
    val displayName: String = ""
)

interface AuthRepository {
    val currentUserFlow: Flow<AuthUser?>
    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInWithGoogleIdToken(idToken: String): Result<AuthUser>
    suspend fun signInWithPhoneCode(verificationId: String, smsCode: String): Result<AuthUser>
    fun signOut()
}
