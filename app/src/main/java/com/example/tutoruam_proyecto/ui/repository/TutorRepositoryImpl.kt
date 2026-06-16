package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.TutorResponse
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import java.io.IOException
import retrofit2.Response

class TutorRepositoryImpl(
    private val apiService: ApiService
) : TutorRepository {

    override suspend fun getTutors(subject: String?): ApiResult<List<TutorResponse>> {
        return executeRequest { apiService.getTutors(subject) }
    }

    override suspend fun getTutorById(tutorId: Int): ApiResult<TutorResponse> {
        return executeRequest { apiService.getTutorById(tutorId) }
    }

    private suspend fun <T> executeRequest(
        request: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
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
