package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.Message
import com.example.tutoruam_proyecto.ui.model.UserProfile
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface ChatRepository {
    suspend fun getChats(): ApiResult<List<Chat>>
    suspend fun getMessages(chatId: Long, since: String? = null): ApiResult<List<Message>>
    suspend fun sendMessage(chatId: Long, content: String): ApiResult<Message>
    
    suspend fun getChatParticipants(chatId: Long): ApiResult<List<UserProfile>>
    suspend fun addParticipant(chatId: Long, userId: Long): ApiResult<Unit>
    suspend fun removeParticipant(chatId: Long, userId: Long): ApiResult<Unit>
}
