package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.TutorResponse
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface TutorRepository {
    suspend fun getTutors(subject: String? = null): ApiResult<List<TutorResponse>>
    suspend fun getTutorById(tutorId: Int): ApiResult<TutorResponse>
}
