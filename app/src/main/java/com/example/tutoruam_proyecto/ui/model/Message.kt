package com.example.tutoruam_proyecto.ui.model

data class Message(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val content: String,
    val sentAt: String,
    val read: Boolean
)

data class SendMessageRequest(
    val content: String
)
