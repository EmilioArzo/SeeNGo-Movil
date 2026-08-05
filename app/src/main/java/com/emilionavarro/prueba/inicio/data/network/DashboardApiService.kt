package com.emilionavarro.prueba.inicio.data.network

import com.emilionavarro.prueba.dispositivos.network.DeviceResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// ── DTOs ─────────────────────────────────────────────────────────────────────
// Los nombres van en camelCase porque ASP.NET Core serializa así por defecto
// (igual que en Deviceapi.kt).

data class RoutineActionDto(
    val deviceId: String,
    val action: String,
    val value: String
)

data class RoutineDto(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val triggerType: String,
    val triggerValue: String,
    val actions: List<RoutineActionDto> = emptyList(),
    val isActive: Boolean,
    val createdAt: String? = null
)

data class SuggestionDto(
    val id: String,
    val userId: String,
    val assignedCluster: String,
    val recommendationText: String,
    val projectedKwhSaving: Double,
    val isViewed: Boolean,
    val createdAt: String? = null
)

// Respuesta de GET /api/client/dashboard/{userId}
data class ClientDashboardResponse(
    val totalDevices: Int,
    val devicesOn: Int,
    val devicesOnline: Int,
    val activeRoutines: Int,
    val unreadSuggestions: Int,
    val devices: List<DeviceResponse> = emptyList(),
    val routines: List<RoutineDto> = emptyList(),
    val suggestions: List<SuggestionDto> = emptyList()
)

data class UpdateRoutineDto(
    val name: String,
    val description: String,
    val isActive: Boolean
)

data class MessageResponse(val message: String)

// ── API interface ─────────────────────────────────────────────────────────────

interface DashboardApiService {

    // GET /api/client/dashboard/{userId} → HomeScreen (carga todo el inicio de un jalón)
    @GET("api/client/dashboard/{userId}")
    suspend fun getDashboard(@Path("userId") userId: String): Response<ClientDashboardResponse>

    // PUT /api/routines/{id} → usado para desactivar una rutina desde el inicio
    // (el backend reemplaza name/description/isActive juntos, por eso mandamos los 3)
    @PUT("api/routines/{id}")
    suspend fun updateRoutine(@Path("id") id: String, @Body dto: UpdateRoutineDto): Response<MessageResponse>

    // POST /api/routines/{id}/execute → botón "probar rutina ahora"
    @POST("api/routines/{id}/execute")
    suspend fun executeRoutine(@Path("id") id: String): Response<MessageResponse>

    // PUT /api/suggestions/{id}/viewed → descartar sugerencia desde el inicio
    @PUT("api/suggestions/{id}/viewed")
    suspend fun markSuggestionViewed(@Path("id") id: String): Response<MessageResponse>
}

// ── Cliente Retrofit (mismo patrón que autenticacion/perfil/dispositivos) ────
object DashboardRetrofitClient {
    private const val BASE_URL = "https://seengo-backend-production.up.railway.app"

    val api: DashboardApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DashboardApiService::class.java)
    }
}