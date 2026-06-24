package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.LoginRequest
import com.example.tutoruam_proyecto.ui.model.LoginResponse
import com.example.tutoruam_proyecto.ui.model.RegisterRequest
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

    private suspend fun executeAuthRequest(
        request: suspend () -> Response<LoginResponse>
    ): ApiResult<LoginResponse> {
        return try {
            val response = request()
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
            ApiResult.Error(message = "Sin conexión a internet", exception = e)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error inesperado", exception = e)
        }
    }
}
