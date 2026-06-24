package com.example.tutoruam_proyecto.ui.model

data class Chat(
    val id: Long,
    val tutorId: Long,
    val tutorName: String,
    val studentId: Long,
    val studentName: String,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int
)
