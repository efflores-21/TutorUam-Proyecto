package com.example.tutoruam_proyecto.ui.model

data class TutorClass(
    val id: Long,
    val creatorId: Long,
    val creatorName: String,
    val subject: String,
    val time: String,
    val description: String,
    val maxStudents: Int,
    val currentStudents: Int,
    val role: String // "TUTOR" or "ESTUDIANTE"
)

data class CreateClassRequest(
    val subject: String,
    val time: String,
    val description: String,
    val maxStudents: Int
)
