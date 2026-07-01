package com.example.tutoruam_proyecto.ui.model

data class Rating(
    val id: Long,
    val tutorId: Long,
    val tutorName: String,
    val studentId: Long,
    val studentName: String,
    val score: Int,
    val comment: String?,
    val createdAt: String
)

data class RatingRequest(
    val classSessionId: Long,
    val score: Int,
    val comment: String?
)
