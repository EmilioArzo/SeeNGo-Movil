package com.emilionavarro.prueba.inicio.data.repository

import com.emilionavarro.prueba.dispositivos.data.ApiResult
import com.emilionavarro.prueba.inicio.data.network.*
import retrofit2.Response

class DashboardRepository(
    private val api: DashboardApiService = DashboardRetrofitClient.api
) {
    suspend fun getDashboard(userId: String): ApiResult<ClientDashboardResponse> = safeCall {
        api.getDashboard(userId)
    }

    /** PUT /api/routines/{id}: reenvía name/description y cambia isActive. */
    suspend fun setRoutineActive(routine: RoutineDto, isActive: Boolean): ApiResult<MessageResponse> = safeCall {
        api.updateRoutine(routine.id, UpdateRoutineDto(routine.name, routine.description, isActive))
    }

    suspend fun executeRoutine(id: String): ApiResult<MessageResponse> = safeCall {
        api.executeRoutine(id)
    }

    suspend fun markSuggestionViewed(id: String): ApiResult<MessageResponse> = safeCall {
        api.markSuggestionViewed(id)
    }

    private suspend fun <T> safeCall(block: suspend () -> Response<T>): ApiResult<T> {
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