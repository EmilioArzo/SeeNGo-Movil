package com.emilionavarro.prueba.senas.data.repository

import com.emilionavarro.prueba.senas.data.network.CreateGestureDto
import com.emilionavarro.prueba.senas.data.network.GestureDto
import com.emilionavarro.prueba.senas.data.network.GesturesApiService
import com.emilionavarro.prueba.senas.data.network.LinkGestureDto
import com.emilionavarro.prueba.senas.data.network.MessageResponseDto
import com.emilionavarro.prueba.senas.data.network.UpdateGestureDto
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class GesturesResult<out T> {
    data class Success<T>(val data: T) : GesturesResult<T>()
    data class Error(val message: String) : GesturesResult<Nothing>()
    object Loading : GesturesResult<Nothing>()
}

class GesturesRepository(private val api: GesturesApiService) {

    suspend fun getGestures(userId: String?): GesturesResult<List<GestureDto>> =
        safeCall { api.getGestures(userId) }

    suspend fun createGesture(dto: CreateGestureDto): GesturesResult<GestureDto> =
        safeCall { api.createGesture(dto) }

    suspend fun updateGesture(id: String, dto: UpdateGestureDto): GesturesResult<MessageResponseDto> =
        safeCall { api.updateGesture(id, dto) }

    suspend fun deleteGesture(id: String): GesturesResult<MessageResponseDto> =
        safeCall { api.deleteGesture(id) }

    suspend fun linkGesture(id: String, dto: LinkGestureDto): GesturesResult<MessageResponseDto> =
        safeCall { api.linkGesture(id, dto) }

    // ── Generic safe call (mismo patrón que AuthRepository / SuggestionsRepository) ──
    private suspend fun <T> safeCall(call: suspend () -> Response<T>): GesturesResult<T> {
        return try {
            val response = call()
            when {
                response.isSuccessful && response.body() != null ->
                    GesturesResult.Success(response.body()!!)
                response.code() == 404 ->
                    GesturesResult.Error("Gesto no encontrado.")
                response.code() == 400 ->
                    GesturesResult.Error("Datos inválidos.")
                else ->
                    GesturesResult.Error("Error del servidor (${response.code()}). Intenta de nuevo.")
            }
        } catch (e: UnknownHostException) {
            GesturesResult.Error("Sin conexión a internet.")
        } catch (e: SocketTimeoutException) {
            GesturesResult.Error("El servidor tardó demasiado. Intenta de nuevo.")
        } catch (e: Exception) {
            GesturesResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}