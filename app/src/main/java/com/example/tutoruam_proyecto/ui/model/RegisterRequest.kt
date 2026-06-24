package com.example.tutoruam_proyecto.ui.model

data class RegisterRequest(
    val name: String,
    val cif: String,
    val email: String,
    val password: String,
    val role: String // "TUTOR" o "ESTUDIANTE"
)
