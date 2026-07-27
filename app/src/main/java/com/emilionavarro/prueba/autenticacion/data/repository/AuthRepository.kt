package com.emilionavarro.prueba.autenticacion.data.repository



import com.emilionavarro.prueba.autenticacion.data.network.*
import com.emilionavarro.prueba.autenticacion.data.network.LoginResponseDto
import com.emilionavarro.prueba.autenticacion.data.network.RegisterResponseDto

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

class AuthRepository(private val api: AuthApiService) {

    // ── Login ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): AuthResult<LoginResponseDto> =
        safeCall(
            call       = { api.login(LoginRequestDto(email, password)) },
            errorMap   = mapOf(401 to "Correo o contraseña incorrectos.")
        )

    // ── Register ──────────────────────────────────────────────────────────────
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): AuthResult<RegisterResponseDto> =
        safeCall(
            call     = { api.register(RegisterRequestDto(name, email, password)) },
            errorMap = mapOf(409 to "El correo ya está registrado.")
        )

    // ── Forgot password ───────────────────────────────────────────────────────
    // Backend always returns 200 (doesn't reveal if email exists)
    suspend fun forgotPassword(email: String): AuthResult<MessageResponseDto> =
        safeCall(
            call     = { api.forgotPassword(ForgotPasswordDto(email)) },
            errorMap = emptyMap()
        )

    // ── Reset password ────────────────────────────────────────────────────────
    suspend fun resetPassword(
        resetCode: String,
        newPassword: String
    ): AuthResult<MessageResponseDto> =
        safeCall(
            call     = { api.resetPassword(ResetPasswordDto(resetCode, newPassword)) },
            errorMap = mapOf(400 to "Código de restablecimiento inválido.")
        )

    // ── Generic safe call ─────────────────────────────────────────────────────
    private suspend fun <T> safeCall(
        call: suspend () -> retrofit2.Response<T>,
        errorMap: Map<Int, String>
    ): AuthResult<T> {
        return try {
            val response = call()
            when {
                response.isSuccessful && response.body() != null ->
                    AuthResult.Success(response.body()!!)
                errorMap.containsKey(response.code()) ->
                    AuthResult.Error(errorMap[response.code()]!!)
                else ->
                    AuthResult.Error("Error del servidor (${response.code()}). Intenta de nuevo.")
            }
        } catch (e: java.net.UnknownHostException) {
            AuthResult.Error("Sin conexión a internet.")
        } catch (e: java.net.SocketTimeoutException) {
            AuthResult.Error("El servidor tardó demasiado. Intenta de nuevo.")
        } catch (e: Exception) {
            AuthResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}