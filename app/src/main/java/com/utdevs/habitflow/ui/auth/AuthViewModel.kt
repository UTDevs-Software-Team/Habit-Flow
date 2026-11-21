/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para autenticacion en 2 pasos:
 * Paso 1: Elegir "Ya tienes cuenta? / Registrate" y los 3 botones de metodo (Correo, Google, Telefono).
 * Paso 2: Formulario especifico de Correo o Telefono SMS.
 */
package com.utdevs.habitflow.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.data.repository.FirebaseAuthRepository
import com.utdevs.habitflow.data.repository.GoogleCredentialHelper
import com.utdevs.habitflow.data.repository.PhoneVerificationHelper
import com.utdevs.habitflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMethod {
    NONE,
    EMAIL,
    PHONE
}

data class AuthUiState(
    val selectedMethod: AuthMethod = AuthMethod.NONE,
    val isRegisterMode: Boolean = false,
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val smsCode: String = "",
    val verificationId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun selectMethod(method: AuthMethod) {
        _uiState.update {
            it.copy(selectedMethod = method, errorMessage = null, infoMessage = null)
        }
    }

    fun backToMethodChooser() {
        _uiState.update {
            it.copy(selectedMethod = AuthMethod.NONE, errorMessage = null, infoMessage = null)
        }
    }

    fun toggleMode(registerMode: Boolean) {
        _uiState.update { it.copy(isRegisterMode = registerMode, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phoneNumber = value, errorMessage = null) }
    }

    fun onSmsCodeChange(value: String) {
        _uiState.update { it.copy(smsCode = value, errorMessage = null) }
    }

    fun submitEmail() {
        val current = _uiState.value
        if (current.email.isBlank() || current.password.length < 6) {
            _uiState.update {
                it.copy(errorMessage = "Ingresa un correo valido y una contrasena de al menos 6 caracteres.")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (current.isRegisterMode) {
                authRepository.signUpWithEmail(current.email, current.password)
            } else {
                authRepository.signInWithEmail(current.email, current.password)
            }
            result.onFailure { err ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = err.message ?: "Error de autenticacion.")
                }
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            val tokenResult = GoogleCredentialHelper.requestGoogleIdToken(activity)
            tokenResult.onFailure { err ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = err.message ?: "Error al obtener cuenta Google.")
                }
                return@launch
            }
            val token = tokenResult.getOrNull().orEmpty()
            authRepository.signInWithGoogleIdToken(token)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = err.message ?: "Error al autenticar con Google.")
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun sendPhoneCode(activity: Activity) {
        val phone = _uiState.value.phoneNumber.trim()
        if (!phone.startsWith("+") || phone.length < 10) {
            _uiState.update {
                it.copy(errorMessage = "Usa formato internacional E.164, por ejemplo +5215512345678.")
            }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        PhoneVerificationHelper.sendVerificationCode(
            activity = activity,
            phoneNumberE164 = phone,
            onCodeSent = { verId, _ ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verificationId = verId,
                        infoMessage = "Codigo SMS enviado. Ingresa los 6 digitos."
                    )
                }
            },
            onAutoVerified = { credential ->
                viewModelScope.launch {
                    val repo = authRepository as? FirebaseAuthRepository
                    if (repo != null) {
                        repo.signInWithCredential(credential)
                            .onFailure { err ->
                                _uiState.update {
                                    it.copy(isLoading = false, errorMessage = err.message ?: "Error SMS.")
                                }
                            }
                            .onSuccess {
                                _uiState.update { it.copy(isLoading = false) }
                            }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            },
            onError = { msg ->
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        )
    }

    fun verifyPhoneCode() {
        val current = _uiState.value
        val verId = current.verificationId ?: return
        if (current.smsCode.trim().length < 6) {
            _uiState.update { it.copy(errorMessage = "Ingresa el codigo SMS de 6 digitos.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.signInWithPhoneCode(verId, current.smsCode)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = err.message ?: "Codigo SMS invalido.")
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AuthViewModel(authRepository) as T
    }
}
