package com.emilionavarro.prueba.autenticacion.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.repository.AuthRepository
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import com.emilionavarro.prueba.autenticacion.data.network.RetrofitClient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle    : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val repository: AuthRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = RegisterUiState.Error("Completa todos los campos.")
            return
        }
        if (password.length < 10) {
            _uiState.value = RegisterUiState.Error("La contraseña debe tener al menos 10 caracteres.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            when (val result = repository.register(name, email, password)) {
                is AuthResult.Success -> {
                    // Auto-login after register: call login to get JWT
                    when (val login = repository.login(email, password)) {
                        is AuthResult.Success -> {
                            session.saveSession(
                                token = login.data.token,
                                id    = login.data.user.id,
                                name  = login.data.user.name,
                                email = login.data.user.email,
                                role  = login.data.user.role
                            )
                            _uiState.value = RegisterUiState.Success
                        }
                        is AuthResult.Error -> _uiState.value = RegisterUiState.Error(login.message)
                        else -> Unit
                    }
                }
                is AuthResult.Error -> _uiState.value = RegisterUiState.Error(result.message)
                else -> Unit
            }
        }
    }

    fun resetState() { _uiState.value = RegisterUiState.Idle }
}

class RegisterViewModelFactory(
    private val session: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RegisterViewModel(
            repository = AuthRepository(RetrofitClient.authApi),
            session    = session
        ) as T
    }
}