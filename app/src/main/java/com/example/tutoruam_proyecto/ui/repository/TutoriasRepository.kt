package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.CreateClassRequest
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface TutoriasRepository {
    suspend fun getClasses(subject: String? = null): ApiResult<List<TutorClass>>
    suspend fun createClass(request: CreateClassRequest): ApiResult<TutorClass>
    suspend fun joinClass(classId: Long): ApiResult<Unit>
    suspend fun getRequests(subject: String? = null): ApiResult<List<TutorClass>>
    suspend fun createRequest(request: CreateClassRequest): ApiResult<TutorClass>
}
