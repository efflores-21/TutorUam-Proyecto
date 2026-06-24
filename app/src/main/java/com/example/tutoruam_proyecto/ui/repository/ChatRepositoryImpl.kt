package com.example.tutoruam_proyecto.ui.repository

import com.example.tutoruam_proyecto.ui.model.SendMessageRequest
import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.Message
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ApiService
import java.io.IOException

class ChatRepositoryImpl(
    private val apiService: ApiService
) : ChatRepository {

    override suspend fun getChats(): ApiResult<List<Chat>> {
        return executeRequest { apiService.getChats() }
    }

    override suspend fun getMessages(chatId: Long, since: String?): ApiResult<List<Message>> {
        return executeRequest { apiService.getMessages(chatId, since) }
    }

    override suspend fun sendMessage(chatId: Long, content: String): ApiResult<Message> {
        return executeRequest { apiService.sendMessage(chatId, SendMessageRequest(content)) }
    }

    private suspend fun <T> executeRequest(
        request: suspend () -> retrofit2.Response<T>
    ): ApiResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) ApiResult.Success(body)
                else ApiResult.Error(message = "Respuesta vacía")
            } else {
                ApiResult.Error(
                    code = response.code(),
                    message = response.errorBody()?.string() ?: "Error"
                )
            }
        } catch (e: IOException) {
            ApiResult.Error(message = "Sin conexión", exception = e)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Error", exception = e)
        }
    }
}
