package com.emilionavarro.prueba.perfil.data.repository



import com.emilionavarro.prueba.perfil.data.network.UpdateProfileDto
import com.emilionavarro.prueba.autenticacion.data.repository.AuthResult
import com.emilionavarro.prueba.perfil.data.network.UserApiService
import com.emilionavarro.prueba.perfil.data.network.UserProfileDto


class UserRepository(private val api: UserApiService) {

    // GET /api/users/{id}  →  ProfileScreen (carga datos al entrar)
    suspend fun getUserProfile(userId: String): AuthResult<UserProfileDto> = safeCall {
        api.getUserProfile(userId)
    }

    // PUT /api/users/{id}  →  EditProfileScreen (botón "Guardar cambios")
    suspend fun updateUserProfile(
        userId: String,
        name: String,
        email: String?,
        phone: String?
    ): AuthResult<Unit> = safeCall {
        api.updateUserProfile(userId, UpdateProfileDto(name = name, email = email, phone = phone))
    }

    // ── Generic safe wrapper ──────────────────────────────────────────────────
    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<T>): AuthResult<T> {
        return try {
            val response = call()
            when {
                response.isSuccessful -> {
                    // PUT returns 200 with no body → treat as success
                    val body = response.body()
                    @Suppress("UNCHECKED_CAST")
                    if (body != null) AuthResult.Success(body)
                    else AuthResult.Success(Unit as T)
                }
                response.code() == 404 -> AuthResult.Error("Usuario no encontrado.")
                response.code() == 400 -> AuthResult.Error("Datos inválidos.")
                response.code() == 401 -> AuthResult.Error("Sesión expirada. Vuelve a iniciar sesión.")
                else -> AuthResult.Error("Error del servidor (${response.code()}).")
            }
        } catch (e: java.net.UnknownHostException) {
            AuthResult.Error("Sin conexión a internet.")
        } catch (e: java.net.SocketTimeoutException) {
            AuthResult.Error("El servidor tardó demasiado.")
        } catch (e: Exception) {
            AuthResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }
}