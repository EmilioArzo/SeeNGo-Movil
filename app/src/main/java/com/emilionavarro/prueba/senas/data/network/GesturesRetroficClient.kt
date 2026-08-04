package com.emilionavarro.prueba.senas.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GesturesRetrofitClient {

    // Misma URL base que el resto de los módulos.
    private const val BASE_URL = "https://seengo-backend-production.up.railway.app"

    val api: GesturesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GesturesApiService::class.java)
    }
}