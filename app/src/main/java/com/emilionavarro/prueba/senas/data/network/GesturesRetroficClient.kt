package com.emilionavarro.prueba.senas.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GesturesRetrofitClient {

    private const val BASE_URL = "https://seengo-backend-production-38f3.up.railway.app"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 👇 registramos el adapter para el campo String::class -> intercepta "id" cuando venga como objeto
    private val gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, MongoIdAdapter())
        .create()

    val api: GesturesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GesturesApiService::class.java)
    }
}