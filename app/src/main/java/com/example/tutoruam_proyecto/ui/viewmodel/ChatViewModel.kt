package com.example.tutoruam_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.Message
import com.example.tutoruam_proyecto.ui.repository.ChatRepository
import com.example.tutoruam_proyecto.ui.service.ApiResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatsState = MutableStateFlow<ApiResult<List<Chat>>?>(null)
    val chatsState: StateFlow<ApiResult<List<Chat>>?> = _chatsState.asStateFlow()

    private val _messagesState = MutableStateFlow<ApiResult<List<Message>>?>(null)
    val messagesState: StateFlow<ApiResult<List<Message>>?> = _messagesState.asStateFlow()

    private val _sendMessageState = MutableStateFlow<ApiResult<Message>?>(null)
    val sendMessageState: StateFlow<ApiResult<Message>?> = _sendMessageState.asStateFlow()

    fun loadChats() {
        viewModelScope.launch {
            _chatsState.value = ApiResult.Loading
            _chatsState.value = chatRepository.getChats()
        }
    }

    fun loadMessages(chatId: Long, since: String? = null) {
        viewModelScope.launch {
            _messagesState.value = ApiResult.Loading
            _messagesState.value = chatRepository.getMessages(chatId, since)
        }
    }

    fun sendMessage(chatId: Long, content: String) {
        viewModelScope.launch {
            _sendMessageState.value = ApiResult.Loading
            val result = chatRepository.sendMessage(chatId, content)
            _sendMessageState.value = result
            if (result is ApiResult.Success) {
                loadMessages(chatId)
            }
        }
    }

    fun clearMessagesState() {
        _messagesState.value = null
    }

    fun clearSendState() {
        _sendMessageState.value = null
    }

    fun startPolling(chatId: Long): Job {
        return viewModelScope.launch {
            while (true) {
                val result = chatRepository.getMessages(chatId)
                if (result is ApiResult.Success) {
                    _messagesState.value = result
                }
                delay(3000)
            }
        }
    }
}
