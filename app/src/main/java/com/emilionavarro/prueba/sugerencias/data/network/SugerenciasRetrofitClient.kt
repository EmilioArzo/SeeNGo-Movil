package com.emilionavarro.prueba.sugerencias.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SugerenciasRetrofitClient {

    // Misma URL base que el resto de los módulos (auth, perfil, dispositivos).
    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"

    val api: SuggestionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SuggestionsApiService::class.java)
    }
}