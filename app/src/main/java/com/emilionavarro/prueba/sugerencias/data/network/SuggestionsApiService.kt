package com.emilionavarro.prueba.sugerencias.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SuggestionsApiService {

    // ── Predictive Suggestions ───────────────────────────────────────────────

    @GET("/api/suggestions/user/{userId}")
    suspend fun getActiveSuggestions(
        @Path("userId") userId: String
    ): Response<List<SuggestionDto>>

    @PUT("/api/suggestions/{id}/viewed")
    suspend fun markSuggestionViewed(
        @Path("id") id: String
    ): Response<MessageResponseDto>

    // ── Routines ──────────────────────────────────────────────────────────────

    @GET("/api/routines")
    suspend fun getRoutines(
        @Query("userId") userId: String?
    ): Response<List<RoutineDto>>

    @GET("/api/routines/{id}")
    suspend fun getRoutineById(
        @Path("id") id: String
    ): Response<RoutineDto>

    @POST("/api/routines")
    suspend fun createRoutine(
        @Body dto: CreateRoutineDto
    ): Response<RoutineDto>

    @PUT("/api/routines/{id}")
    suspend fun updateRoutine(
        @Path("id") id: String,
        @Body dto: UpdateRoutineDto
    ): Response<MessageResponseDto>

    @DELETE("/api/routines/{id}")
    suspend fun deleteRoutine(
        @Path("id") id: String
    ): Response<MessageResponseDto>

    @POST("/api/routines/{id}/execute")
    suspend fun executeRoutine(
        @Path("id") id: String
    ): Response<ExecuteRoutineResponseDto>
}