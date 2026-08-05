package com.emilionavarro.prueba.dispositivos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.dispositivos.bluetooth.BleDeviceScanner
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.data.DiscoveredDevice
import com.emilionavarro.prueba.dispositivos.data.DiscoverySource
import com.emilionavarro.prueba.dispositivos.network.MdnsDeviceDto
import com.emilionavarro.prueba.dispositivos.wifi.WifiDeviceScanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiscoveryUiState(
    val isScanningWifi: Boolean = false,
    val isScanningBluetooth: Boolean = false,
    val devices: List<DiscoveredDevice> = emptyList(),
    val isLinking: Boolean = false,
    val linkSuccess: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Unifica descubrimiento WiFi (mDNS) y Bluetooth (BLE) en una sola lista, y
 * vincula lo seleccionado con POST /api/devices/sync-mdns (el backend indexa
 * por macAddress sin importar el transporte, así que un solo endpoint sirve
 * para ambos casos).
 */
class DeviceDiscoveryViewModel(
    private val userId: String,
    private val repository: DeviceRepository = DeviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    private var wifiJob: Job? = null
    private var bleJob: Job? = null

    fun startWifiScan(context: Context) {
        wifiJob?.cancel()
        _uiState.value = _uiState.value.copy(isScanningWifi = true, errorMessage = null)
        wifiJob = viewModelScope.launch {
            try {
                WifiDeviceScanner(context).scan().collect { found ->
                    addOrUpdate(
                        DiscoveredDevice(
                            macAddress = found.macAddress,
                            name = found.name,
                            source = DiscoverySource.WIFI,
                            extraInfo = found.localIp
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Error al escanear WiFi.")
            } finally {
                _uiState.value = _uiState.value.copy(isScanningWifi = false)
            }
        }
    }

    fun startBluetoothScan(context: Context) {
        val scanner = BleDeviceScanner(context)
        if (!scanner.isBluetoothEnabled()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Activa el Bluetooth para buscar dispositivos.")
            return
        }
        bleJob?.cancel()
        _uiState.value = _uiState.value.copy(isScanningBluetooth = true, errorMessage = null)
        bleJob = viewModelScope.launch {
            try {
                scanner.scan().collect { found ->
                    addOrUpdate(
                        DiscoveredDevice(
                            macAddress = found.macAddress,
                            name = found.name,
                            source = DiscoverySource.BLUETOOTH,
                            extraInfo = "${found.rssi} dBm"
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Error al escanear Bluetooth.")
            } finally {
                _uiState.value = _uiState.value.copy(isScanningBluetooth = false)
            }
        }
    }

    /** Dispara ambos escaneos a la vez; se usa desde ScanNetworkScreen y como "Volver a escanear". */
    fun scanAll(context: Context) {
        _uiState.value = _uiState.value.copy(devices = emptyList())
        startWifiScan(context)
        startBluetoothScan(context)
    }

    fun stopScans() {
        wifiJob?.cancel(); wifiJob = null
        bleJob?.cancel(); bleJob = null
        _uiState.value = _uiState.value.copy(isScanningWifi = false, isScanningBluetooth = false)
    }

    private fun addOrUpdate(device: DiscoveredDevice) {
        val current = _uiState.value.devices
        val exists = current.any { it.macAddress == device.macAddress }
        _uiState.value = _uiState.value.copy(
            devices = if (exists) current.map { if (it.macAddress == device.macAddress) device else it }
            else current + device
        )
    }

    fun linkDevices(selected: List<DiscoveredDevice>) {
        if (selected.isEmpty()) return
        stopScans()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLinking = true, errorMessage = null)

            val dtos = selected.map { device ->
                MdnsDeviceDto(
                    macAddress = device.macAddress,
                    localIp = if (device.source == DiscoverySource.WIFI) device.extraInfo else "bluetooth",
                    deviceType = if (device.source == DiscoverySource.WIFI) "shelly" else "ble",
                    userId = userId
                )
            }

            when (val result = repository.linkDevices(dtos)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(isLinking = false, linkSuccess = true)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLinking = false, errorMessage = result.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopScans()
    }
}