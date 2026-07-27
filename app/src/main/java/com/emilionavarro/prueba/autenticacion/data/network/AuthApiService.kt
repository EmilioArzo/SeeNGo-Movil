package com.emilionavarro.prueba.autenticacion.data.network



import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// ── DTOs ─────────────────────────────────────────────────────────────────────

// Login
data class LoginRequestDto(val email: String, val password: String)
data class LoginUserDto(val id: String, val name: String, val email: String, val role: String)
data class LoginResponseDto(val token: String, val user: LoginUserDto)

// Register
data class RegisterRequestDto(val name: String, val email: String, val password: String)
data class RegisterResponseDto(val id: String, val name: String, val email: String, val role: String)

// Forgot password
data class ForgotPasswordDto(val email: String)

// Reset password
data class ResetPasswordDto(val resetCode: String, val newPassword: String)

// Generic message response
data class MessageResponseDto(val message: String)

// ── API interface ─────────────────────────────────────────────────────────────

interface AuthApiService {

    // POST /api/auth/login
    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<LoginResponseDto>

    // POST /api/auth/register
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequestDto): Response<RegisterResponseDto>

    // POST /api/auth/forgot-password
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordDto): Response<MessageResponseDto>

    // POST /api/auth/reset-password
    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordDto): Response<MessageResponseDto>
}