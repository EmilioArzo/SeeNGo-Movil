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
    val newlyLinkedDeviceId: String? = null,
    val errorMessage: String? = null
)

/**
 * Unifica descubrimiento WiFi (mDNS) y Bluetooth (BLE) en una sola lista, y
 * vincula lo seleccionado con POST /api/devices/sync-mdns.
 *
 * IMPORTANTE sobre deduplicación:
 * WifiDeviceScanner no tiene acceso a la MAC real (mDNS no la expone), así
 * que deriva un identificador a partir del nombre del servicio. BleDeviceScanner
 * sí trae la MAC real de Bluetooth. Como son valores distintos para el MISMO
 * dispositivo físico, si no dedupliramos por nombre, un dispositivo que
 * responde por ambos transportes (ej. un enchufe WiFi que también anuncia
 * BLE) aparecería dos veces: una con LocalIp real (WiFi) y otra con
 * LocalIp = "bluetooth". Por eso en addOrUpdate() se prioriza siempre la
 * versión WiFi cuando el mismo nombre aparece en los dos transportes.
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
        _uiState.value = _uiState.value.copy(
            devices = emptyList(),
            linkSuccess = false,
            newlyLinkedDeviceId = null,
            errorMessage = null
        )
        startWifiScan(context)
        startBluetoothScan(context)
    }

    fun consumeLinkSuccess() {
        _uiState.value = _uiState.value.copy(linkSuccess = false)
    }

    fun stopScans() {
        wifiJob?.cancel(); wifiJob = null
        bleJob?.cancel(); bleJob = null
        _uiState.value = _uiState.value.copy(isScanningWifi = false, isScanningBluetooth = false)
    }

    /**
     * Agrega un dispositivo encontrado, o lo fusiona si ya existe (misma mac,
     * o mismo nombre visto por otro transporte). Cuando hay conflicto de
     * transporte para el mismo nombre, la versión WiFi siempre gana porque
     * trae LocalIp real; la versión Bluetooth se descarta.
     */
    private fun addOrUpdate(device: DiscoveredDevice) {
        val current = _uiState.value.devices

        // Caso 1: ya vimos exactamente esta mac (mismo transporte) -> actualiza en su lugar.
        if (current.any { it.macAddress == device.macAddress }) {
            _uiState.value = _uiState.value.copy(
                devices = current.map { if (it.macAddress == device.macAddress) device else it }
            )
            return
        }

        // Caso 2: mismo nombre de dispositivo visto por el otro transporte
        // (mac distinta porque WiFi usa un id derivado y BLE la mac real).
        val normalizedName = device.name.trim().lowercase()
        val existingByName = current.firstOrNull { it.name.trim().lowercase() == normalizedName }
        if (existingByName != null && existingByName.source != device.source) {
            val wifiVersion = if (device.source == DiscoverySource.WIFI) device else existingByName
            val isWifiVersion = wifiVersion.source == DiscoverySource.WIFI
            if (isWifiVersion) {
                _uiState.value = _uiState.value.copy(
                    devices = current.map {
                        if (it.name.trim().lowercase() == normalizedName) wifiVersion else it
                    }
                )
                return
            }
        }

        // Caso 3: dispositivo nuevo de verdad.
        _uiState.value = _uiState.value.copy(devices = current + device)
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
                    // DeviceType ahora es el nombre real anunciado por el dispositivo
                    // (service name de mDNS, o nombre BLE), no una etiqueta genérica.
                    deviceType = device.name.ifBlank {
                        if (device.source == DiscoverySource.WIFI) "Dispositivo WiFi" else "Dispositivo Bluetooth"
                    },
                    userId = userId
                )
            }

            when (val result = repository.linkDevices(dtos)) {
                is ApiResult.Success -> {
                    // sync-mdns no devuelve el id creado; lo resolvemos re-consultando
                    // por macAddress (tomamos el primero si se seleccionó más de uno).
                    val resolvedId = when (val list = repository.getDevices(userId)) {
                        is ApiResult.Success -> list.data.firstOrNull { it.macAddress == selected.first().macAddress }?.id
                        is ApiResult.Error -> null
                    }
                    _uiState.value = _uiState.value.copy(
                        isLinking = false,
                        linkSuccess = true,
                        newlyLinkedDeviceId = resolvedId
                    )
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(isLinking = false, errorMessage = result.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopScans()
    }
}