package com.example.tutoruam_proyecto.ui.model

data class TutorResponse(
    val id: Int,
    val name: String,
    val subject: String? = null,
    val rating: Double? = null
)
