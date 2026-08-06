package com.emilionavarro.prueba.dispositivos.bluetooth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.network.MdnsDeviceDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BleDevicesUiState(
    val isScanning: Boolean = false,
    val devices: List<BleFoundDevice> = emptyList(),
    val isLinking: Boolean = false,
    val linkSuccess: Boolean = false,
    val newlyLinkedDeviceId: String? = null,
    val errorMessage: String? = null
)

class BluetoothDevicesViewModel(
    private val userId: String,
    private val repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BleDevicesUiState())
    val uiState: StateFlow<BleDevicesUiState> = _uiState.asStateFlow()

    private var scanJob: Job? = null

    fun startScan(context: Context) {
        val scanner = BleDeviceScanner(context)
        if (!scanner.isBluetoothEnabled()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Activa el Bluetooth para buscar dispositivos.")
            return
        }

        scanJob?.cancel()
        _uiState.value = _uiState.value.copy(isScanning = true, errorMessage = null, devices = emptyList())

        scanJob = viewModelScope.launch {
            try {
                scanner.scan().collect { found ->
                    val current = _uiState.value.devices
                    if (current.none { it.macAddress == found.macAddress }) {
                        _uiState.value = _uiState.value.copy(devices = current + found)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isScanning = false, errorMessage = e.message ?: "Error al escanear.")
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    /** Reusa POST /api/devices/sync-mdns: el backend solo necesita macAddress/userId,
     *  así que no hace falta un endpoint nuevo para BLE. */
    fun linkDevices(selected: List<BleFoundDevice>) {
        if (selected.isEmpty()) return
        stopScan()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLinking = true, errorMessage = null)

            val dtos = selected.map { device ->
                MdnsDeviceDto(macAddress = device.macAddress, localIp = "bluetooth", deviceType = "ble", userId = userId)
            }

            when (val result = repository.linkDevices(dtos)) {
                is ApiResult.Success -> {
                    val resolvedId = when (val list = repository.getDevices(userId)) {
                        is ApiResult.Success -> list.data.firstOrNull { it.macAddress == selected.first().macAddress }?.id
                        is ApiResult.Error -> null
                    }
                    _uiState.value = _uiState.value.copy(
                        isLinking = false, linkSuccess = true, newlyLinkedDeviceId = resolvedId
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLinking = false, errorMessage = result.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
    }
}