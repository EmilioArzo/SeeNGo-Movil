package com.emilionavarro.prueba.inicio.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import com.emilionavarro.prueba.inicio.data.network.RoutineDto
import com.emilionavarro.prueba.inicio.data.network.SuggestionDto
import com.emilionavarro.prueba.inicio.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI models ─────────────────────────────────────────────────────────────────
data class QuickDeviceUi(
    val id: String,
    val name: String,
    val subtitle: String,
    val icon: ImageVector,
    val isOn: Boolean
)

data class RoutineUi(
    val id: String,
    val name: String,
    val triggerType: String,
    val triggerValue: String
)

data class SuggestionUi(
    val id: String,
    val text: String,
    val kwhSaving: Double,
    val cluster: String
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val quickDevices: List<QuickDeviceUi> = emptyList(),
    val totalDevices: Int = 0,
    val devicesOn: Int = 0,
    val devicesOnline: Int = 0,
    val routines: List<RoutineUi> = emptyList(),
    val suggestions: List<SuggestionUi> = emptyList(),
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val userId: String,
    private val dashboardRepository: DashboardRepository = DashboardRepository(),
    private val deviceRepository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Guardamos las rutinas completas (name/description) para poder hacer el PUT
    private val routineById = mutableMapOf<String, RoutineDto>()

    init { loadDashboard() }

    fun loadDashboard() {
        if (userId.isBlank()) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Sesión no encontrada.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = dashboardRepository.getDashboard(userId)) {
                is ApiResult.Success -> {
                    val data = result.data
                    routineById.clear()
                    data.routines.forEach { routineById[it.id] = it }

                    _uiState.value = DashboardUiState(
                        isLoading     = false,
                        quickDevices  = data.devices.take(4).map { it.toQuickDeviceUi() },
                        totalDevices  = data.totalDevices,
                        devicesOn     = data.devicesOn,
                        devicesOnline = data.devicesOnline,
                        routines      = data.routines.map { it.toRoutineUi() },
                        suggestions   = data.suggestions.map { it.toSuggestionUi() }
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    /** Enciende/apaga desde los controles rápidos (PUT /api/devices/{id}/state, vía DeviceRepository). */
    fun toggleDevice(deviceId: String, isOn: Boolean) {
        val previous = _uiState.value.quickDevices
        _uiState.value = _uiState.value.copy(
            quickDevices = previous.map { if (it.id == deviceId) it.copy(isOn = isOn) else it }
        )
        viewModelScope.launch {
            when (val result = deviceRepository.toggleDeviceState(deviceId, isOn)) {
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(quickDevices = previous, errorMessage = result.message)
                else -> Unit
            }
        }
    }

    /**
     * El dashboard del backend solo devuelve rutinas activas, así que "desactivar"
     * aquí significa: PUT con isActive=false y sacarla de la lista si el backend confirma.
     */
    fun deactivateRoutine(routineId: String) {
        val routine = routineById[routineId] ?: return
        val previous = _uiState.value.routines
        _uiState.value = _uiState.value.copy(routines = previous.filterNot { it.id == routineId })

        viewModelScope.launch {
            when (val result = dashboardRepository.setRoutineActive(routine, isActive = false)) {
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(routines = previous, errorMessage = result.message)
                else -> routineById.remove(routineId)
            }
        }
    }

    /** POST /api/routines/{id}/execute — "probar rutina ahora". */
    fun executeRoutine(routineId: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            when (val result = dashboardRepository.executeRoutine(routineId)) {
                is ApiResult.Success -> onResult(true, result.data.message)
                is ApiResult.Error   -> onResult(false, result.message)
            }
        }
    }

    /** PUT /api/suggestions/{id}/viewed — descarta la sugerencia del inicio. */
    fun dismissSuggestion(suggestionId: String) {
        val previous = _uiState.value.suggestions
        _uiState.value = _uiState.value.copy(suggestions = previous.filterNot { it.id == suggestionId })
        viewModelScope.launch {
            when (val result = dashboardRepository.markSuggestionViewed(suggestionId)) {
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(suggestions = previous, errorMessage = result.message)
                else -> Unit
            }
        }
    }

    private fun DeviceResponse.toQuickDeviceUi() = QuickDeviceUi(
        id       = id,
        name     = displayName ?: deviceType ?: "Dispositivo",
        subtitle = if (isOn) "Encendido" else if (isOnline) "Apagado" else "Desconectado",
        icon     = iconForDeviceType(deviceType),
        isOn     = isOn
    )

    private fun RoutineDto.toRoutineUi() = RoutineUi(
        id = id, name = name, triggerType = triggerType, triggerValue = triggerValue
    )

    private fun SuggestionDto.toSuggestionUi() = SuggestionUi(
        id = id, text = recommendationText, kwhSaving = projectedKwhSaving, cluster = assignedCluster
    )

    private fun iconForDeviceType(deviceType: String?): ImageVector = when (deviceType?.lowercase()) {
        "shellyplug", "plug"  -> Icons.Outlined.Tungsten
        "shelly1pm", "switch" -> Icons.Outlined.LightMode
        "ac", "aire"          -> Icons.Outlined.AcUnit
        "tv"                  -> Icons.Outlined.Tv
        "speaker", "echo"     -> Icons.Outlined.GraphicEq
        else                  -> Icons.Outlined.Memory
    }
}