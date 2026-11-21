/**
 * Codigo anadido por Felipe Denis
 *
 * ViewModel Black Box para capturar el apodo inicial del usuario ("Como quieres que te llamemos?").
 */
package com.utdevs.habitflow.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.utdevs.habitflow.domain.repository.GoalsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NicknameUiState(
    val nickname: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class NicknameViewModel(
    private val uid: String,
    private val email: String,
    initialSuggestedName: String,
    private val goalsRepository: GoalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NicknameUiState(nickname = initialSuggestedName))
    val uiState: StateFlow<NicknameUiState> = _uiState.asStateFlow()

    fun onNicknameChange(value: String) {
        _uiState.update { it.copy(nickname = value, errorMessage = null) }
    }

    fun saveNickname() {
        val clean = _uiState.value.nickname.trim()
        if (clean.length < 2) {
            _uiState.update { it.copy(errorMessage = "Escribe un apodo de al menos 2 caracteres.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            goalsRepository.saveNickname(uid, email, clean)
                .onFailure { err ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = err.message ?: "No se pudo guardar tu apodo.")
                    }
                }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                }
        }
    }

    class Factory(
        private val uid: String,
        private val email: String,
        private val initialSuggestedName: String,
        private val goalsRepository: GoalsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NicknameViewModel(uid, email, initialSuggestedName, goalsRepository) as T
    }
}
