package com.example.tutoruam_proyecto.ui.repository

import android.util.Log
import com.example.tutoruam_proyecto.ui.model.*
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import com.example.tutoruam_proyecto.ui.service.TokenManager
import java.io.IOException
import retrofit2.Response

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return executeAuthRequest {
            apiService.login(LoginRequest(email, password))
        }
    }

    override suspend fun register(request: RegisterRequest): ApiResult<LoginResponse> {
        return executeAuthRequest {
            apiService.register(request)
        }
    }

    override suspend fun changeRole(newRole: String): ApiResult<Unit> {
        return try {
            val response = apiService.changeRole(newRole)
            if (response.isSuccessful) {
                // Actualizar el rol en el TokenManager si es necesario
                TokenManager.saveUser(
                    token = TokenManager.getToken() ?: "",
                    userId = TokenManager.getUserId(),
                    name = TokenManager.getName(),
                    email = TokenManager.getEmail(),
                    role = newRole
                )
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Error al cambiar rol"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error inesperado", exception = e)
        }
    }

    override suspend fun createChat(recipientId: Long): ApiResult<Chat> {
        return try {
            val response = apiService.createChat(recipientId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error(message = "Respuesta vacía")
            } else {
                ApiResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Error al crear chat"
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error inesperado", exception = e)
        }
    }

    private suspend fun executeAuthRequest(
        request: suspend () -> Response<LoginResponse>
    ): ApiResult<LoginResponse> {
        return try {
            Log.d("AuthRepo", "Enviando petición...")
            val response = request()
            Log.d("AuthRepo", "Código: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    TokenManager.saveUser(
                        token = body.token,
                        userId = body.userId,
                        name = body.name,
                        email = body.email,
                        role = body.role
                    )
                    ApiResult.Success(body)
                } else {
                    ApiResult.Error(message = "Respuesta vacía del servidor")
                }
            } else {
                ApiResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Error desconocido"
                )
            }
        } catch (e: IOException) {
            Log.e("AuthRepo", "Error de red: ${e.message}", e)
            ApiResult.Error(message = "Sin conexión a internet", exception = e)
        } catch (e: Exception) {
            Log.e("AuthRepo", "Error: ${e.message}", e)
            ApiResult.Error(message = e.message ?: "Error inesperado", exception = e)
        }
    }
}
