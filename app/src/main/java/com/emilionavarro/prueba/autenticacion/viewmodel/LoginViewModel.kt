package com.emilionavarro.prueba.autenticacion.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.network.RetrofitClient
import com.emilionavarro.prueba.autenticacion.data.repository.AuthRepository
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI state ──────────────────────────────────────────────────────────────────
sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(RetrofitClient.authApi),
    private val session: SessionManager? = null   // inyectado desde la Activity/NavHost
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Completa todos los campos.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> {
                    val data = result.data
                    session?.saveSession(
                        token = data.token,
                        id    = data.user.id,
                        name  = data.user.name,
                        email = data.user.email,
                        role  = data.user.role
                    )
                    _uiState.value = LoginUiState.Success
                }
                is AuthResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
                else -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}