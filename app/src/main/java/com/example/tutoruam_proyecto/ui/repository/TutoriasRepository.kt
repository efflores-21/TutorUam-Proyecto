package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.CreateClassRequest
import com.example.tutoruam_proyecto.ui.model.CreateHelpRequest
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.model.UserProfile
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface TutoriasRepository {
    suspend fun getClasses(subject: String? = null, tutorId: Long? = null, studentId: Long? = null): ApiResult<List<TutorClass>>
    suspend fun createClass(request: CreateClassRequest): ApiResult<TutorClass>
    suspend fun joinClass(classId: Long): ApiResult<Unit>
    suspend fun getRequests(subject: String? = null, studentId: Long? = null, tutorAssignedId: Long? = null): ApiResult<List<TutorClass>>
    suspend fun createRequest(request: CreateClassRequest): ApiResult<TutorClass>
    
    suspend fun getClassStudents(classId: Long): ApiResult<List<UserProfile>>
    suspend fun removeStudent(classId: Long, studentId: Long): ApiResult<Unit>
    suspend fun createGroupChat(classId: Long): ApiResult<Chat>

    // Nuevos
    suspend fun updateClass(classId: Long, request: CreateClassRequest): ApiResult<TutorClass>
    suspend fun deleteClass(classId: Long): ApiResult<Unit>
    suspend fun updateRequest(requestId: Long, request: CreateHelpRequest): ApiResult<TutorClass>
    suspend fun deleteRequest(requestId: Long): ApiResult<Unit>
    suspend fun assignRequest(requestId: Long): ApiResult<Unit>
    suspend fun updateClassStatus(classId: Long, status: String): ApiResult<Unit>
    suspend fun getClassById(classId: Long): ApiResult<TutorClass>

    suspend fun getMyTutoriasAsTutor(): ApiResult<List<TutorClass>>
    suspend fun getMyClassesAsStudent(): ApiResult<List<TutorClass>>
}
