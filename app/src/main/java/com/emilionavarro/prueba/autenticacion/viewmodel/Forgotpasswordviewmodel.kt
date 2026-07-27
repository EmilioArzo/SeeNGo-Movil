package com.emilionavarro.prueba.autenticacion.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.network.RetrofitClient
import com.emilionavarro.prueba.autenticacion.data.repository.AuthRepository
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ForgotPasswordUiState {
    object Idle    : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()   // email enviado (siempre 200)
    data class Error(val message: String) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun sendResetEmail(email: String) {
        if (email.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Ingresa tu correo.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            // Backend always returns 200 — never reveals if email exists
            _uiState.value = when (repository.forgotPassword(email)) {
                is AuthResult.Success -> ForgotPasswordUiState.Success
                is AuthResult.Error   -> ForgotPasswordUiState.Success // show success anyway
                else -> ForgotPasswordUiState.Success
            }
        }
    }

    fun resetState() { _uiState.value = ForgotPasswordUiState.Idle }
}

class ForgotPasswordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ForgotPasswordViewModel(
            repository = AuthRepository(RetrofitClient.authApi)
        ) as T
    }
}