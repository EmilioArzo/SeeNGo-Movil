package com.emilionavarro.prueba.sugerencias.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SugerenciasRetrofitClient {
    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"

    private val gson = com.google.gson.GsonBuilder()
        .registerTypeAdapter(String::class.java, com.emilionavarro.prueba.dispositivos.network.MongoIdAdapter())
        .create()

    val api: SuggestionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SuggestionsApiService::class.java)
    }
}