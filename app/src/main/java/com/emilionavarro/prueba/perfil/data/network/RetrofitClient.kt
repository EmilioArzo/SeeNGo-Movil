package com.emilionavarro.prueba.perfil.data.network



import com.emilionavarro.prueba.autenticacion.data.network.AuthApiService
import com.emilionavarro.prueba.dispositivos.network.DeviceApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val deviceApi: DeviceApiService by lazy { retrofit.create(DeviceApiService::class.java) }
    val userApi:   UserApiService   by lazy { retrofit.create(UserApiService::class.java) }
    val analyticsApi: AnalyticsApiService by lazy { retrofit.create(AnalyticsApiService::class.java) }
}