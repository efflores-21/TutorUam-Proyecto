package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.Rating
import com.example.tutoruam_proyecto.ui.model.RatingRequest
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import java.io.IOException

class RatingRepositoryImpl(
    private val apiService: ApiService
) : RatingRepository {

    override suspend fun createRating(request: RatingRequest): ApiResult<Rating> {
        return executeRequest { apiService.createRating(request) }
    }

    override suspend fun getRatingsByTutor(tutorId: Long): ApiResult<List<Rating>> {
        return executeRequest { apiService.getRatingsByTutor(tutorId) }
    }

    override suspend fun getAverageRating(tutorId: Long): ApiResult<Double> {
        return executeRequest { apiService.getAverageRating(tutorId) }
    }

    private suspend fun <T> executeRequest(
        request: suspend () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error(message = "Respuesta vacía")
            } else {
                ApiResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Error"
                )
            }
        } catch (e: IOException) {
            ApiResult.Error(message = "Sin conexión", exception = e)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }
}
