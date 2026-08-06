package com.emilionavarro.prueba.dispositivos

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.dispositivos.data.DeviceRepository
import com.emilionavarro.prueba.dispositivos.data.isWifiConnected
import com.emilionavarro.prueba.dispositivos.network.SpotifyAuthRetrofitClient
import com.emilionavarro.prueba.dispositivos.network.SpotifyTokenDto
import com.emilionavarro.prueba.dispositivos.data.spotify.PkceGenerator
import com.emilionavarro.prueba.dispositivos.data.spotify.SpotifyAuthBridge
import com.emilionavarro.prueba.dispositivos.data.spotify.SpotifyConfig
import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import java.time.Instant

data class DevicesUiState(
    val isLoading: Boolean = false,
    val rooms: List<RoomSection> = emptyList(),
    val errorMessage: String? = null,
    // ── Spotify ──────────────────────────────────────────────────────────
    val isSpotifyLinked: Boolean = false,
    val isLinkingSpotify: Boolean = false,
    val spotifyError: String? = null
)

class DevicesViewModel(
    private val repository: DeviceRepository = DeviceRepository(),
    private val session: SessionManager? = null,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState(isLoading = true))
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    private val deviceIdByKey = mutableMapOf<String, String>()

    init {
        loadDevices()
        _uiState.value = _uiState.value.copy(isSpotifyLinked = session?.isSpotifyLinked() ?: false)

        // Escucha el resultado del deep link de Spotify (llega desde MainActivity)
        viewModelScope.launch {
            SpotifyAuthBridge.authCode.collect { code ->
                if (code != null) completeSpotifyLink(code)
            }
        }
        viewModelScope.launch {
            SpotifyAuthBridge.authError.collect { err ->
                if (err != null) {
                    _uiState.value = _uiState.value.copy(isLinkingSpotify = false, spotifyError = "Autorización cancelada o rechazada.")
                    SpotifyAuthBridge.consume()
                }
            }
        }
    }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = repository.getDevices(userId)) {
                is ApiResult.Success -> {
                    val rooms = groupByRoom(result.data)
                    _uiState.value = _uiState.value.copy(isLoading = false, rooms = rooms)
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
        updateLocalState(roomName, device.name, isOn)
        viewModelScope.launch {
            when (val result = repository.toggleDeviceState(deviceId, isOn)) {
                is ApiResult.Error -> {
                    updateLocalState(roomName, device.name, !isOn)
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)
                }
                else -> Unit
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // SPOTIFY — OAuth Authorization Code + PKCE
    // ══════════════════════════════════════════════════════════════════════

    fun startSpotifyLink(context: Context) {
        _uiState.value = _uiState.value.copy(spotifyError = null)
        val verifier = SpotifyAuthBridge.beginAuth()
        val challenge = PkceGenerator.generateCodeChallenge(verifier)

        val authUri = Uri.parse("https://accounts.spotify.com/authorize").buildUpon()
            .appendQueryParameter("client_id", SpotifyConfig.CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", SpotifyConfig.REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("scope", SpotifyConfig.SCOPES.joinToString(" "))
            .build()

        CustomTabsIntent.Builder().build().launchUrl(context, authUri)
    }

    private fun completeSpotifyLink(code: String) {
        val verifier = SpotifyAuthBridge.pendingCodeVerifier
        SpotifyAuthBridge.consume()
        if (verifier == null) {
            _uiState.value = _uiState.value.copy(spotifyError = "Vuelve a intentar la vinculación.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLinkingSpotify = true, spotifyError = null)
            try {
                val tokenResponse = SpotifyAuthRetrofitClient.api.exchangeCode(
                    code = code,
                    redirectUri = SpotifyConfig.REDIRECT_URI,
                    clientId = SpotifyConfig.CLIENT_ID,
                    codeVerifier = verifier
                )
                val token = tokenResponse.body()
                if (!tokenResponse.isSuccessful || token == null) {
                    _uiState.value = _uiState.value.copy(isLinkingSpotify = false, spotifyError = "Spotify rechazó la autorización.")
                    return@launch
                }

                val expiresAt = Instant.now().plusSeconds(token.expires_in.toLong()).toString()
                val dto = SpotifyTokenDto(
                    userId = userId,
                    accessToken = token.access_token,
                    refreshToken = token.refresh_token ?: "",
                    expiresAt = expiresAt
                )

                when (val result = repository.linkSpotify(dto)) {
                    is ApiResult.Success -> {
                        session?.saveSpotifyLinked(true)
                        _uiState.value = _uiState.value.copy(isLinkingSpotify = false, isSpotifyLinked = true)
                    }
                    is ApiResult.Error -> {
                        _uiState.value = _uiState.value.copy(isLinkingSpotify = false, spotifyError = result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLinkingSpotify = false, spotifyError = e.message ?: "No se pudo vincular Spotify.")
            }
        }
    }

    private fun updateLocalState(roomName: String, deviceName: String, isOn: Boolean) {
        val rooms = _uiState.value.rooms.map { room ->
            if (room.name != roomName) room
            else room.copy(devices = room.devices.map { d -> if (d.name == deviceName) d.copy(isOn = isOn) else d })
        }
        _uiState.value = _uiState.value.copy(rooms = rooms)
    }

    private fun groupByRoom(devices: List<DeviceResponse>): List<RoomSection> {
        deviceIdByKey.clear()
        return devices
            .groupBy { it.room ?: "Sin cuarto" }
            .map { (roomName, devicesInRoom) ->
                val items = devicesInRoom.map { dev ->
                    val displayName = dev.displayName ?: dev.deviceType ?: "Dispositivo"
                    val key = "$roomName:$displayName"
                    deviceIdByKey[key] = dev.id
                    val connectionLabel = when {
                        !dev.isOnline -> "Desconectado"
                        dev.isWifiConnected() -> "WiFi"
                        else -> "Bluetooth"
                    }
                    DeviceItem(
                        name = displayName,
                        subtitle = "${dev.deviceType ?: "Dispositivo"} · $connectionLabel",
                        icon = iconForIconLabel(dev.icon),
                        isOn = dev.isOn
                    )
                }
                RoomSection(name = roomName, deviceCount = items.size, kwh = 0.0, devices = items)
            }
    }

    /**
     * El ícono ya se guarda explícitamente al configurar el dispositivo
     * (ConfigureDeviceScreen -> updateDevice(..., icon = ...)), así que se
     * resuelve por esa etiqueta en vez de adivinar a partir de deviceType
     * (que ahora es el nombre real del producto, no una categoría fija).
     */
    private fun iconForIconLabel(icon: String?): ImageVector = when (icon) {
        "lámpara" -> Icons.Outlined.LightMode
        "mesa"    -> Icons.Outlined.Tungsten
        "tv"      -> Icons.Outlined.Tv
        "control" -> Icons.Outlined.SettingsRemote
        "enchufe" -> Icons.Outlined.Power
        else      -> Icons.Outlined.Memory
    }
}