package com.emilionavarro.prueba.perfil.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import com.emilionavarro.prueba.perfil.data.repository.UserRepository
import com.emilionavarro.prueba.perfil.data.network.RetrofitClient
import com.emilionavarro.prueba.perfil.data.network.UserProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI states ─────────────────────────────────────────────────────────────────
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfileDto) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val repository: UserRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    fun loadProfile() {
        val userId = session.getUserId() ?: return
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            _uiState.value = when (val result = repository.getUserProfile(userId)) {
                is AuthResult.Success -> ProfileUiState.Success(result.data)
                is AuthResult.Error   -> ProfileUiState.Error(result.message)
                else -> ProfileUiState.Error("Error desconocido.")
            }
        }
    }
}

// ── Factory ───────────────────────────────────────────────────────────────────
class ProfileViewModelFactory(
    private val session: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProfileViewModel(
            repository = UserRepository(RetrofitClient.userApi),
            session    = session
        ) as T
    }
}