package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.CreateClassRequest
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import java.io.IOException
import retrofit2.Response

class TutoriasRepositoryImpl(
    private val apiService: ApiService
) : TutoriasRepository {

    override suspend fun getClasses(subject: String?): ApiResult<List<TutorClass>> {
        return executeRequest { apiService.getClasses(subject) }
    }

    override suspend fun createClass(request: CreateClassRequest): ApiResult<TutorClass> {
        return executeRequest { apiService.createClass(request) }
    }

    override suspend fun joinClass(classId: Long): ApiResult<Unit> {
        return executeRequest { apiService.joinClass(classId) }
    }

    override suspend fun getRequests(subject: String?): ApiResult<List<TutorClass>> {
        return executeRequest { apiService.getRequests(subject) }
    }

    override suspend fun createRequest(request: CreateClassRequest): ApiResult<TutorClass> {
        return executeRequest { apiService.createRequest(request) }
    }

    private suspend fun <T> executeRequest(
        request: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else if (response.code() == 204 || response.code() == 200) {
                     @Suppress("UNCHECKED_CAST")
                     ApiResult.Success(Unit as T)
                } else {
                    ApiResult.Error(message = "Respuesta vacía")
                }
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
