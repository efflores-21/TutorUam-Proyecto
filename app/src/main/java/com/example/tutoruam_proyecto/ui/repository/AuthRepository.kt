package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.LoginResponse
import com.example.tutoruam_proyecto.ui.model.RegisterRequest
import com.example.tutoruam_proyecto.ui.service.ApiResult

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<LoginResponse>
    suspend fun register(request: RegisterRequest): ApiResult<LoginResponse>
    suspend fun changeRole(newRole: String): ApiResult<Unit>
    suspend fun createChat(recipientId: Long): ApiResult<Chat>
}
