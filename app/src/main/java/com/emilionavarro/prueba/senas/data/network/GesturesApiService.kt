package com.emilionavarro.prueba.senas.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface GesturesApiService {

    @GET("/api/gestures")
    suspend fun getGestures(
        @Query("userId") userId: String?
    ): Response<List<GestureDto>>

    @POST("/api/gestures")
    suspend fun createGesture(
        @Body dto: CreateGestureDto
    ): Response<GestureDto>

    @PUT("/api/gestures/{id}")
    suspend fun updateGesture(
        @Path("id") id: String,
        @Body dto: UpdateGestureDto
    ): Response<MessageResponseDto>

    @DELETE("/api/gestures/{id}")
    suspend fun deleteGesture(
        @Path("id") id: String
    ): Response<MessageResponseDto>

    @PUT("/api/gestures/{id}/link")
    suspend fun linkGesture(
        @Path("id") id: String,
        @Body dto: LinkGestureDto
    ): Response<MessageResponseDto>
}