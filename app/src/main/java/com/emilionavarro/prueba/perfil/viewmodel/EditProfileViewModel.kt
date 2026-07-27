package com.emilionavarro.prueba.perfil.viewmodel




import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import com.emilionavarro.prueba.perfil.data.network.RetrofitClient
import com.emilionavarro.prueba.perfil.data.repository.UserRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI states ─────────────────────────────────────────────────────────────────
sealed class EditProfileUiState {
    object Idle    : EditProfileUiState()
    object Loading : EditProfileUiState()
    object Success : EditProfileUiState()
    data class Error(val message: String) : EditProfileUiState()
}

class EditProfileViewModel(
    private val repository: UserRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
    val uiState: StateFlow<EditProfileUiState> = _uiState

    fun saveProfile(name: String, email: String, phone: String) {
        if (name.isBlank()) {
            _uiState.value = EditProfileUiState.Error("El nombre no puede estar vacío.")
            return
        }

        val userId = session.getUserId() ?: run {
            _uiState.value = EditProfileUiState.Error("Sesión no encontrada.")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditProfileUiState.Loading

            val result = repository.updateUserProfile(
                userId = userId,
                name   = name,
                email  = email.ifBlank { null },
                phone  = phone.ifBlank { null }
            )

            _uiState.value = when (result) {
                is AuthResult.Success -> {
                    // Update local session name if changed
                    session.saveSession(
                        token = session.getToken() ?: "",
                        id    = userId,
                        name  = name,
                        email = email.ifBlank { session.getUserEmail() ?: "" },
                        role  = session.getUserRole() ?: ""
                    )
                    EditProfileUiState.Success
                }
                is AuthResult.Error -> EditProfileUiState.Error(result.message)
                else -> EditProfileUiState.Error("Error desconocido.")
            }
        }
    }

    fun resetState() { _uiState.value = EditProfileUiState.Idle }
}

// ── Factory ───────────────────────────────────────────────────────────────────
class EditProfileViewModelFactory(
    private val session: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditProfileViewModel(
            repository = UserRepository(RetrofitClient.userApi),
            session    = session
        ) as T
    }
}