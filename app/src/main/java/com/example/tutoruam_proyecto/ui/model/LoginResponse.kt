package com.example.tutoruam_proyecto.ui.model

data class LoginResponse(
    val token: String? = null,
    val userId: Long? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null // "TUTOR" o "ESTUDIANTE"
)
