package com.emilionavarro.prueba.sugerencias.data.repository

import com.emilionavarro.prueba.sugerencias.data.network.CreateRoutineDto
import com.emilionavarro.prueba.sugerencias.data.network.ExecuteRoutineResponseDto
import com.emilionavarro.prueba.sugerencias.data.network.MessageResponseDto
import com.emilionavarro.prueba.sugerencias.data.network.RoutineDto
import com.emilionavarro.prueba.sugerencias.data.network.SuggestionDto
import com.emilionavarro.prueba.sugerencias.data.network.SuggestionsApiService
import com.emilionavarro.prueba.sugerencias.data.network.UpdateRoutineDto
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class SuggestionsResult<out T> {
    data class Success<T>(val data: T) : SuggestionsResult<T>()
    data class Error(val message: String) : SuggestionsResult<Nothing>()
    object Loading : SuggestionsResult<Nothing>()
}

class SuggestionsRepository(private val api: SuggestionsApiService) {

    // ── Predictive Suggestions ───────────────────────────────────────────────

    suspend fun getActiveSuggestions(userId: String): SuggestionsResult<List<SuggestionDto>> =
        safeCall { api.getActiveSuggestions(userId) }

    suspend fun markSuggestionViewed(id: String): SuggestionsResult<MessageResponseDto> =
        safeCall { api.markSuggestionViewed(id) }

    // ── Routines ──────────────────────────────────────────────────────────────

    suspend fun getRoutines(userId: String?): SuggestionsResult<List<RoutineDto>> =
        safeCall { api.getRoutines(userId) }

    suspend fun getRoutineById(id: String): SuggestionsResult<RoutineDto> =
        safeCall { api.getRoutineById(id) }

    suspend fun createRoutine(dto: CreateRoutineDto): SuggestionsResult<RoutineDto> =
        safeCall { api.createRoutine(dto) }

    suspend fun updateRoutine(id: String, dto: UpdateRoutineDto): SuggestionsResult<MessageResponseDto> =
        safeCall { api.updateRoutine(id, dto) }

    suspend fun deleteRoutine(id: String): SuggestionsResult<MessageResponseDto> =
        safeCall { api.deleteRoutine(id) }

    suspend fun executeRoutine(id: String): SuggestionsResult<ExecuteRoutineResponseDto> =
        safeCall { api.executeRoutine(id) }

    // ── Generic safe call (mismo patrón que AuthRepository) ─────────────────────

    private suspend fun <T> safeCall(call: suspend () -> Response<T>): SuggestionsResult<T> {
        return try {
            val response = call()
            when {
                response.isSuccessful && response.body() != null ->
                    SuggestionsResult.Success(response.body()!!)
                response.code() == 404 ->
                    SuggestionsResult.Error("No se encontró el recurso solicitado.")
                else ->
                    SuggestionsResult.Error("Error del servidor (${response.code()}). Intenta de nuevo.")
            }
        } catch (e: UnknownHostException) {
            SuggestionsResult.Error("Sin conexión a internet.")
        } catch (e: SocketTimeoutException) {
            SuggestionsResult.Error("El servidor tardó demasiado. Intenta de nuevo.")
        } catch (e: Exception) {
            SuggestionsResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}