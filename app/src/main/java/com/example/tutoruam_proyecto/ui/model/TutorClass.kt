package com.example.tutoruam_proyecto.ui.model

data class TutorClass(
    val id: Long,
    val creatorId: Long,          // tutorId para clases, studentId para requests
    val creatorName: String,      // tutorName o studentName
    val subject: String,
    val scheduledTime: String? = null, // para clases
    val description: String,
    val maxStudents: Int? = null,     // para clases
    val currentStudents: Int? = null, // para clases
    val status: String? = null,       // para clases
    val timeLimit: String? = null,    // para requests
    val type: String  // "CLASS" o "REQUEST"
)

data class ClassSessionResponse(
    val id: Long,
    val tutorId: Long,
    val tutorName: String,
    val subject: String,
    val scheduledTime: String,
    val description: String,
    val maxStudents: Int,
    val currentStudents: Int,
    val status: String
)

data class HelpRequestResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val subject: String,
    val description: String,
    val timeLimit: String? = null
)

data class CreateClassRequest(
    val subject: String,
    val scheduledTime: String,  // NOTA: se llama scheduledTime, no "time"
    val description: String,
    val maxStudents: Int
)

data class CreateHelpRequest(
    val subject: String,
    val description: String,
    val timeLimit: String? = null
)
