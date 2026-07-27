package com.emilionavarro.prueba.dispositivos.data



import com.emilionavarro.prueba.dispositivos.network.*

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

class DeviceRepository(
    private val api: DeviceApiService = ApiClient.deviceApi
) {
    // ── Lista / detalle ──────────────────────────────────────────────────
    suspend fun getDevices(userId: String?): ApiResult<List<DeviceResponse>> = safeCall {
        api.getDevices(userId)
    }

    suspend fun getDeviceById(id: String): ApiResult<DeviceResponse> = safeCall {
        api.getDeviceById(id)
    }

    // ── Encender / apagar ────────────────────────────────────────────────
    suspend fun toggleDeviceState(id: String, isOn: Boolean): ApiResult<MessageResponse> = safeCall {
        api.toggleDeviceState(id, DeviceStateDto(isOn))
    }

    // ── Editar / eliminar (menú "más opciones" del detalle) ─────────────
    suspend fun updateDevice(id: String, displayName: String, room: String, icon: String): ApiResult<MessageResponse> =
        safeCall { api.updateDevice(id, UpdateDeviceDto(displayName, room, icon)) }

    suspend fun deleteDevice(id: String): ApiResult<MessageResponse> = safeCall {
        api.deleteDevice(id)
    }

    // ── Flujo de escaneo / vinculación (pantalla "encontrado") ──────────
    suspend fun startScan(userId: String): ApiResult<ScanStartResponse> = safeCall {
        api.startDeviceScan(ScanRequestDto(userId))
    }

    suspend fun getScanResults(scanId: String): ApiResult<ScanResultResponse> = safeCall {
        api.getScanResults(scanId)
    }

    suspend fun linkDevices(devices: List<MdnsDeviceDto>): ApiResult<MessageResponse> = safeCall {
        api.syncMdnsDevices(devices)
    }

    // ── Helper ───────────────────────────────────────────────────────────
    private suspend fun <T> safeCall(block: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        return try {
            val response = block()
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error ${response.code()}: ${response.errorBody()?.string() ?: "sin detalle"}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de red desconocido")
        }
    }
}