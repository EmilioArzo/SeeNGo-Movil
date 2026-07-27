package com.emilionavarro.prueba.dispositivos



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DeviceDetailUiState(
    val isLoading: Boolean = true,
    val device: DeviceResponse? = null,
    val isOn: Boolean = false,
    val wasDeleted: Boolean = false,
    val errorMessage: String? = null
)

class DeviceDetailViewModel(
    private val deviceId: String,
    private val repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDevice()
    }

    fun loadDevice() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getDeviceById(deviceId)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        device = result.data,
                        isOn = result.data.isOn
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    /** Switch principal de encendido/apagado (PUT /api/devices/{id}/state) */
    fun setOn(isOn: Boolean) {
        val previous = _uiState.value.isOn
        _uiState.value = _uiState.value.copy(isOn = isOn) // optimista

        viewModelScope.launch {
            when (val result = repository.toggleDeviceState(deviceId, isOn)) {
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isOn = previous, errorMessage = result.message)
                }
                else -> Unit
            }
        }
    }

    /** Renombrar / mover de cuarto / cambiar ícono, típicamente desde el menú "más opciones" */
    fun updateDevice(displayName: String, room: String, icon: String) {
        viewModelScope.launch {
            when (val result = repository.updateDevice(deviceId, displayName, room, icon)) {
                is ApiResult.Success -> loadDevice()
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }

    /** Eliminar dispositivo, típicamente desde el menú "más opciones" */
    fun deleteDevice() {
        viewModelScope.launch {
            when (val result = repository.deleteDevice(deviceId)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(wasDeleted = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
            }
        }
    }
}