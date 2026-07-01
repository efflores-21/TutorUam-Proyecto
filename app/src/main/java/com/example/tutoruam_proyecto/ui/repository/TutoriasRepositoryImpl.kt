package com.example.tutoruam_proyecto.ui.repository

import android.util.Log
import com.example.tutoruam_proyecto.ui.model.*
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import java.io.IOException
import retrofit2.Response

class TutoriasRepositoryImpl(
    private val apiService: ApiService
) : TutoriasRepository {

    override suspend fun getClasses(subject: String?, tutorId: Long?, studentId: Long?): ApiResult<List<TutorClass>> {
        return try {
            val response = apiService.getClasses(subject, tutorId, studentId)
            if (response.isSuccessful) {
                val classes = response.body()?.map { it.toDomain() } ?: emptyList()
                ApiResult.Success(classes)
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun createClass(request: CreateClassRequest): ApiResult<TutorClass> {
        Log.d("TutoriasRepo", "Creando clase: $request")
        return try {
            val response = apiService.createClass(request)
            Log.d("TutoriasRepo", "Código: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body.toDomain())
                else ApiResult.Error(message = "Respuesta vacía")
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            Log.e("TutoriasRepo", "Error: ${e.message}", e)
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun joinClass(classId: Long): ApiResult<Unit> {
        Log.d("TutoriasRepo", "Uniéndose a clase: $classId")
        return executeRequest { apiService.joinClass(classId) }
    }

    override suspend fun getRequests(subject: String?, studentId: Long?, tutorAssignedId: Long?): ApiResult<List<TutorClass>> {
        return try {
            val response = apiService.getRequests(subject, studentId, tutorAssignedId)
            if (response.isSuccessful) {
                val requests = response.body()?.map { it.toDomain() } ?: emptyList()
                ApiResult.Success(requests)
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun createRequest(request: CreateClassRequest): ApiResult<TutorClass> {
        Log.d("TutoriasRepo", "Creando solicitud: $request")
        return try {
            val response = apiService.createRequest(request)
            Log.d("TutoriasRepo", "Código: ${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body.toDomain())
                else ApiResult.Error(message = "Respuesta vacía")
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            Log.e("TutoriasRepo", "Error: ${e.message}", e)
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun getClassStudents(classId: Long): ApiResult<List<UserProfile>> {
        return executeRequest { apiService.getClassStudents(classId) }
    }

    override suspend fun removeStudent(classId: Long, studentId: Long): ApiResult<Unit> {
        return executeRequest { apiService.removeStudent(classId, studentId) }
    }

    override suspend fun createGroupChat(classId: Long): ApiResult<Chat> {
        return executeRequest { apiService.createGroupChat(classId) }
    }

    override suspend fun updateClass(classId: Long, request: CreateClassRequest): ApiResult<TutorClass> {
        return try {
            val response = apiService.updateClass(classId, request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!.toDomain())
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun deleteClass(classId: Long): ApiResult<Unit> {
        return executeRequest { apiService.deleteClass(classId) }
    }

    override suspend fun updateRequest(requestId: Long, request: CreateHelpRequest): ApiResult<TutorClass> {
        return try {
            val response = apiService.updateRequest(requestId, request)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!.toDomain())
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun deleteRequest(requestId: Long): ApiResult<Unit> {
        return executeRequest { apiService.deleteRequest(requestId) }
    }

    override suspend fun assignRequest(requestId: Long): ApiResult<Unit> {
        return executeRequest { apiService.assignRequest(requestId) }
    }

    override suspend fun updateClassStatus(classId: Long, status: String): ApiResult<Unit> {
        return executeRequest { apiService.updateClassStatus(classId, UpdateClassStatusRequest(status)) }
    }

    override suspend fun getClassById(classId: Long): ApiResult<TutorClass> {
        return try {
            val response = apiService.getClassById(classId)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!.toDomain())
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string() ?: "Error")
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }

    override suspend fun getMyTutoriasAsTutor(): ApiResult<List<TutorClass>> {
        val userId = com.example.tutoruam_proyecto.ui.service.TokenManager.getUserId() ?: return ApiResult.Error(message = "Usuario no autenticado")
        return try {
            val response = apiService.getRequests(tutorAssignedId = userId)
            if (response.isSuccessful) {
                val list = response.body()?.map { it.toDomain() } ?: emptyList()
                ApiResult.Success(list)
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message)
        }
    }

    override suspend fun getMyClassesAsStudent(): ApiResult<List<TutorClass>> {
        val userId = com.example.tutoruam_proyecto.ui.service.TokenManager.getUserId() ?: return ApiResult.Error(message = "Usuario no autenticado")
        return try {
            val response = apiService.getClasses(studentId = userId)
            if (response.isSuccessful) {
                val list = response.body()?.map { it.toDomain() } ?: emptyList()
                ApiResult.Success(list)
            } else {
                ApiResult.Error(code = response.code(), message = response.errorBody()?.string())
            }
        } catch (e: Exception) {
            ApiResult.Error(message = e.message)
        }
    }

    private fun com.example.tutoruam_proyecto.ui.model.ClassSessionResponse.toDomain() = TutorClass(
        id = id,
        creatorId = tutorId,
        creatorName = tutorName,
        subject = subject,
        scheduledTime = scheduledTime,
        description = description,
        maxStudents = maxStudents,
        currentStudents = currentStudents,
        status = status,
        type = "CLASS"
    )

    private fun com.example.tutoruam_proyecto.ui.model.HelpRequestResponse.toDomain() = TutorClass(
        id = id,
        creatorId = studentId,
        creatorName = studentName,
        subject = subject,
        description = description,
        timeLimit = timeLimit,
        type = "REQUEST"
    )

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
                    @Suppress("UNCHECKED_CAST")
                    ApiResult.Success(Unit as T)
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
