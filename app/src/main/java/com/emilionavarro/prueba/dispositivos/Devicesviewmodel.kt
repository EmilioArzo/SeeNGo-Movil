package com.emilionavarro.prueba.dispositivos


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DevicesUiState(
    val isLoading: Boolean = false,
    val rooms: List<RoomSection> = emptyList(),
    val errorMessage: String? = null
)

class DevicesViewModel(
    private val repository: DeviceRepository = DeviceRepository(),
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState(isLoading = true))
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    // Guardamos el id real del backend por cada DeviceItem para poder togglear
    private val deviceIdByKey = mutableMapOf<String, String>()

    init {
        loadDevices()
    }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getDevices(userId)) {
                is ApiResult.Success -> {
                    val rooms = groupByRoom(result.data)
                    _uiState.value = DevicesUiState(isLoading = false, rooms = rooms)
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun toggleDevice(roomName: String, device: DeviceItem, isOn: Boolean) {
        val key = "$roomName:${device.name}"
        val deviceId = deviceIdByKey[key] ?: return

        // Actualización optimista en UI
        updateLocalState(roomName, device.name, isOn)

        viewModelScope.launch {
            when (val result = repository.toggleDeviceState(deviceId, isOn)) {
                is ApiResult.Error -> {
                    // Revertimos si falla
                    updateLocalState(roomName, device.name, !isOn)
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)
                }
                else -> Unit
            }
        }
    }

    private fun updateLocalState(roomName: String, deviceName: String, isOn: Boolean) {
        val rooms = _uiState.value.rooms.map { room ->
            if (room.name != roomName) room
            else room.copy(devices = room.devices.map { d ->
                if (d.name == deviceName) d.copy(isOn = isOn) else d
            })
        }
        _uiState.value = _uiState.value.copy(rooms = rooms)
    }

    private fun groupByRoom(devices: List<DeviceResponse>): List<RoomSection> {
        deviceIdByKey.clear()
        return devices
            .groupBy { it.room ?: "Sin cuarto" }
            .map { (roomName, devicesInRoom) ->
                val items = devicesInRoom.map { dev ->
                    val key = "$roomName:${dev.displayName ?: dev.deviceType ?: "Dispositivo"}"
                    deviceIdByKey[key] = dev.id
                    DeviceItem(
                        name = dev.displayName ?: dev.deviceType ?: "Dispositivo",
                        subtitle = "${dev.deviceType ?: "Shelly"} · ${if (dev.isOnline) "En línea" else "Desconectado"}",
                        icon = iconForDeviceType(dev.deviceType),
                        isOn = dev.isOn
                    )
                }
                RoomSection(
                    name = roomName,
                    deviceCount = items.size,
                    // El backend no expone consumo aún; se deja en 0.0 hasta tener ese endpoint
                    kwh = 0.0,
                    devices = items
                )
            }
    }

    private fun iconForDeviceType(deviceType: String?): ImageVector {
        return when (deviceType?.lowercase()) {
            "shellyplug", "plug" -> Icons.Outlined.Tungsten
            "shelly1pm", "switch" -> Icons.Outlined.LightMode
            "ac", "aire" -> Icons.Outlined.AcUnit
            "tv" -> Icons.Outlined.Tv
            "speaker", "echo" -> Icons.Outlined.GraphicEq
            else -> Icons.Outlined.Memory
        }
    }
}