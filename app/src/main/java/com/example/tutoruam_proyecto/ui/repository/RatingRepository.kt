package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.Rating
import com.example.tutoruam_proyecto.ui.model.RatingRequest
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface RatingRepository {
    suspend fun createRating(request: RatingRequest): ApiResult<Rating>
    suspend fun getRatingsByTutor(tutorId: Long): ApiResult<List<Rating>>
    suspend fun getAverageRating(tutorId: Long): ApiResult<Double>
}
