package com.emilionavarro.prueba.dispositivos



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.MdnsDeviceDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScannedDevice(
    val id: String,
    val name: String,
    val model: String,
    val ip: String
)

data class FoundDevicesUiState(
    val isScanning: Boolean = false,
    val devices: List<ScannedDevice> = emptyList(),
    val isLinking: Boolean = false,
    val linkSuccess: Boolean = false,
    val errorMessage: String? = null
)


class FoundDevicesViewModel(
    private val repository: DeviceRepository = DeviceRepository(),
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoundDevicesUiState())
    val uiState: StateFlow<FoundDevicesUiState> = _uiState.asStateFlow()

    init {
        rescan()
    }

    /** Inicia un escaneo con POST /api/devices/scan y hace polling a
     *  GET /api/devices/scan/{scanId} hasta que el estado ya no sea "scanning". */
    fun rescan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isScanning = true, errorMessage = null)

            when (val startResult = repository.startScan(userId)) {
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isScanning = false, errorMessage = startResult.message)
                    return@launch
                }
                is ApiResult.Success -> pollScan(startResult.data.scanId)
            }
        }
    }

    private suspend fun pollScan(scanId: String, maxAttempts: Int = 15) {
        repeat(maxAttempts) {
            when (val result = repository.getScanResults(scanId)) {
                is ApiResult.Success -> {
                    val scan = result.data
                    if (scan.status != "scanning") {
                        _uiState.value = _uiState.value.copy(
                            isScanning = false,
                            devices = scan.devicesFound.mapIndexed { index, dto ->
                                ScannedDevice(
                                    id = dto.macAddress,
                                    name = "${dto.deviceType}-${dto.macAddress.takeLast(4)}",
                                    model = dto.deviceType,
                                    ip = dto.localIp
                                )
                            }
                        )
                        return
                    }
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isScanning = false, errorMessage = result.message)
                    return
                }
            }
            delay(1000)
        }
        // Se acabaron los intentos sin terminar el escaneo
        _uiState.value = _uiState.value.copy(isScanning = false, errorMessage = "El escaneo tardó demasiado, intenta de nuevo.")
    }

    /** Vincula los dispositivos seleccionados con POST /api/devices/sync-mdns */
    fun linkDevices(selected: List<ScannedDevice>) {
        if (selected.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLinking = true, errorMessage = null)

            val dtos = selected.map { device ->
                MdnsDeviceDto(
                    macAddress = device.id,
                    localIp = device.ip,
                    deviceType = device.model,
                    userId = userId
                )
            }

            when (val result = repository.linkDevices(dtos)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLinking = false, linkSuccess = true)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLinking = false, errorMessage = result.message)
                }
            }
        }
    }
}