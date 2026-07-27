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

sealed class NewPasswordUiState {
    object Idle    : NewPasswordUiState()
    object Loading : NewPasswordUiState()
    object Success : NewPasswordUiState()
    data class Error(val message: String) : NewPasswordUiState()
}

class NewPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewPasswordUiState>(NewPasswordUiState.Idle)
    val uiState: StateFlow<NewPasswordUiState> = _uiState

    fun resetPassword(resetCode: String, newPassword: String, confirmPassword: String) {
        when {
            resetCode.isBlank() ->
            { _uiState.value = NewPasswordUiState.Error("Ingresa el código de restablecimiento."); return }
            newPassword.length < 10 ->
            { _uiState.value = NewPasswordUiState.Error("La contraseña debe tener al menos 10 caracteres."); return }
            newPassword != confirmPassword ->
            { _uiState.value = NewPasswordUiState.Error("Las contraseñas no coinciden."); return }
        }

        viewModelScope.launch {
            _uiState.value = NewPasswordUiState.Loading
            _uiState.value = when (val result = repository.resetPassword(resetCode, newPassword)) {
                is AuthResult.Success -> NewPasswordUiState.Success
                is AuthResult.Error   -> NewPasswordUiState.Error(result.message)
                else -> NewPasswordUiState.Error("Error desconocido.")
            }
        }
    }

    fun resetState() { _uiState.value = NewPasswordUiState.Idle }
}

class NewPasswordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return NewPasswordViewModel(
            repository = AuthRepository(RetrofitClient.authApi)
        ) as T
    }
}