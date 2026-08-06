package com.emilionavarro.prueba.perfil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.perfil.data.repository.UserRepository
import com.emilionavarro.prueba.perfil.data.network.RetrofitClient
import com.emilionavarro.prueba.perfil.data.network.UserProfileDto
import com.emilionavarro.prueba.senas.data.network.GesturesRetrofitClient
import com.emilionavarro.prueba.senas.data.repository.GesturesRepository
import com.emilionavarro.prueba.senas.data.repository.GesturesResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── UI states ─────────────────────────────────────────────────────────────────
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val profile: UserProfileDto,
        val gestureCount: Int = 0,
        val deviceCount: Int = 0,
        val totalKwh: Double = 0.0
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val repository: UserRepository,
    private val session: SessionManager,
    private val gesturesRepository: GesturesRepository = GesturesRepository(GesturesRetrofitClient.api),
    private val deviceRepository: DeviceRepository = DeviceRepository()
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
            when (val result = repository.getUserProfile(userId)) {
                is AuthResult.Success -> {
                    val gestureCount = when (val g = gesturesRepository.getGestures(userId)) {
                        is GesturesResult.Success -> g.data.size
                        else -> 0
                    }
                    val deviceCount = when (val d = deviceRepository.getDevices(userId)) {
                        is ApiResult.Success -> d.data.size
                        else -> 0
                    }
                    // GET /api/analytics/consumption/summary — consumo real del mes en curso
                    val totalKwh = try {
                        val resp = RetrofitClient.analyticsApi.getConsumptionSummary(userId, "month")
                        if (resp.isSuccessful) resp.body()?.totalKwh ?: 0.0 else 0.0
                    } catch (e: Exception) {
                        0.0
                    }

                    _uiState.value = ProfileUiState.Success(
                        profile = result.data,
                        gestureCount = gestureCount,
                        deviceCount = deviceCount,
                        totalKwh = totalKwh
                    )
                }
                is AuthResult.Error -> _uiState.value = ProfileUiState.Error(result.message)
                else -> _uiState.value = ProfileUiState.Error("Error desconocido.")
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