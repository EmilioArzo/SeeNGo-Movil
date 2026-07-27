package com.emilionavarro.prueba.perfil.data.network


import retrofit2.Response
import retrofit2.http.*

// ── DTOs ─────────────────────────────────────────────────────────────────────

data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val createdAt: String
)

data class UpdateProfileDto(
    val name: String,
    val email: String? = null,
    val phone: String? = null
)

// ── API interface ─────────────────────────────────────────────────────────────

interface UserApiService {

    @GET("api/users/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UserProfileDto>

    @PUT("api/users/{id}")
    suspend fun updateUserProfile(
        @Path("id") id: String,
        @Body body: UpdateProfileDto
    ): Response<Unit>
}