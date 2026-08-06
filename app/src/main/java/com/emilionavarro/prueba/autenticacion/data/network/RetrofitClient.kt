package com.emilionavarro.prueba.autenticacion.data.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"
    private val logging = okhttp3.logging.HttpLoggingInterceptor().apply {
        level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
    }
    private val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}