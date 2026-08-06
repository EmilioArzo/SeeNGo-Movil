package com.emilionavarro.prueba.dispositivos.network



import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

// ════════════════════════════════════════════════════════════════════════════
// DTOs — deben coincidir con los contratos del backend (minimal API en C#)
// ════════════════════════════════════════════════════════════════════════════

// Nota: ASP.NET Core (System.Text.Json) serializa por defecto en camelCase,
// así que los nombres de campo aquí están en camelCase. Si tu backend usa
// PascalCase, agrega @SerializedName("MacAddress") etc. a cada campo.

data class MdnsDeviceDto(
    val macAddress: String,
    val localIp: String,
    val deviceType: String,
    val userId: String
)

data class CreateDeviceDto(
    val userId: String,
    val macAddress: String,
    val localIp: String,
    val deviceType: String,
    val displayName: String,
    val room: String,
    val icon: String
)

data class UpdateDeviceDto(
    val displayName: String,
    val room: String,
    val icon: String
)

data class DeviceStateDto(
    val isOn: Boolean
)

data class ScanRequestDto(
    val userId: String
)

// Respuesta de un dispositivo tal como lo devuelve el backend (DeviceDocument)
data class DeviceResponse(
    val id: String,
    val userId: String?,
    val macAddress: String?,
    val localIp: String?,
    val deviceType: String?,
    val displayName: String?,
    val room: String?,
    val icon: String?,
    val isOnline: Boolean,
    val isOn: Boolean,
    val createdAt: String?
)

data class ScanStartResponse(
    val scanId: String,
    val message: String
)

// Respuesta de un escaneo (ScanDocument)
data class ScanResultResponse(
    val id: String,
    val userId: String?,
    val status: String, // ej: "scanning" | "completed" | "failed"
    val devicesFound: List<MdnsDeviceDto>,
    val startedAt: String?
)

data class MessageResponse(
    val message: String
)

// ════════════════════════════════════════════════════════════════════════════
// Servicio Retrofit — un método por endpoint expuesto en el backend
// ════════════════════════════════════════════════════════════════════════════

interface DeviceApiService {

    @POST("api/devices/sync-mdns")
    suspend fun syncMdnsDevices(@Body devices: List<MdnsDeviceDto>): Response<MessageResponse>

    @GET("api/devices")
    suspend fun getDevices(@Query("userId") userId: String?): Response<List<DeviceResponse>>

    @GET("api/devices/{id}")
    suspend fun getDeviceById(@Path("id") id: String): Response<DeviceResponse>

    @POST("api/devices")
    suspend fun createDevice(@Body dto: CreateDeviceDto): Response<DeviceResponse>

    @PUT("api/devices/{id}")
    suspend fun updateDevice(@Path("id") id: String, @Body dto: UpdateDeviceDto): Response<MessageResponse>

    @DELETE("api/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String): Response<MessageResponse>

    @PUT("api/devices/{id}/state")
    suspend fun toggleDeviceState(@Path("id") id: String, @Body dto: DeviceStateDto): Response<MessageResponse>

    @POST("api/devices/scan")
    suspend fun startDeviceScan(@Body dto: ScanRequestDto): Response<ScanStartResponse>

    @GET("api/devices/scan/{scanId}")
    suspend fun getScanResults(@Path("scanId") scanId: String): Response<ScanResultResponse>

    @POST("api/integrations/spotify/token")
    suspend fun storeSpotifyToken(@Body dto: SpotifyTokenDto): Response<MessageResponse>
}

// ════════════════════════════════════════════════════════════════════════════
// Cliente Retrofit — ajusta BASE_URL a la IP/host real de tu backend
// ════════════════════════════════════════════════════════════════════════════

object ApiClient {
    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 👇 nuevo: gson con el adapter para el id "roto" que manda el backend
    private val gson = com.google.gson.GsonBuilder()
        .registerTypeAdapter(String::class.java, MongoIdAdapter())
        .create()

    val deviceApi: DeviceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)) // 👈 antes: GsonConverterFactory.create()
            .build()
            .create(DeviceApiService::class.java)
    }
}

// Coincide con SpotifyTokenDto(string UserId, string AccessToken, string RefreshToken, DateTime ExpiresAt)
data class SpotifyTokenDto(
    val userId: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: String // ISO-8601, ej: Instant.now().toString()
)