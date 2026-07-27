package com.emilionavarro.prueba.autenticacion.data.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // ⚠️ Cambia esta URL por la de tu servidor.
    // Emulador Android → usa 10.0.2.2 en lugar de localhost
    // Dispositivo físico → usa la IP de tu máquina en la red local, p.ej. 192.168.1.X
    private const val BASE_URL = "https://seengo-backend-production.up.railway.app"

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}