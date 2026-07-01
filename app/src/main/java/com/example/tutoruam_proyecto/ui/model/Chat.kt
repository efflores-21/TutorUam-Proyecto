package com.example.tutoruam_proyecto.ui.model

data class Chat(
    val id: Long,
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val classSessionId: Long? = null,
    // Para 1 a 1
    val tutorId: Long? = null,
    val tutorName: String? = null,
    val studentId: Long? = null,
    val studentName: String? = null,
    // Para grupos
    val participants: List<UserProfile>? = null,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int
)

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val rating: Double? = null
)
