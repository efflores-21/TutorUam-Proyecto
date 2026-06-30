package com.example.tutoruam_proyecto.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.model.CreateClassRequest
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.repository.ChatRepository
import com.example.tutoruam_proyecto.ui.repository.TutoriasRepository
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TutoriasViewModel(
    private val repository: TutoriasRepository,
    private val chatRepository: ChatRepository,
    private val role: UserRole
) : ViewModel() {

    private val _itemsState = MutableStateFlow<ApiResult<List<TutorClass>>>(ApiResult.Loading)
    val itemsState: StateFlow<ApiResult<List<TutorClass>>> = _itemsState.asStateFlow()

    private val _actionState = MutableStateFlow<ApiResult<Any>?>(null)
    val actionState: StateFlow<ApiResult<Any>?> = _actionState.asStateFlow()

    init {
        loadData()
    }

    fun loadData(subject: String? = null) {
        viewModelScope.launch {
            _itemsState.value = ApiResult.Loading
            _itemsState.value = if (role == UserRole.TUTOR) {
                repository.getRequests(subject)
            } else {
                repository.getClasses(subject)
            }
        }
    }

    fun createPost(subject: String, time: String, description: String, maxStudents: Int) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val request = CreateClassRequest(
                subject = subject,
                scheduledTime = time,
                description = description,
                maxStudents = maxStudents
            )
            val result = if (role == UserRole.TUTOR) {
                repository.createClass(request)
            } else {
                repository.createRequest(request)
            }
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadData()
            }
        }
    }

    fun joinClass(classId: Long, creatorName: String, creatorId: Long) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = repository.joinClass(classId)
            if (result is ApiResult.Success) {
                // Ahora obtener el chat entre el usuario actual y el creador (tutor o estudiante)
                val chatsResult = chatRepository.getChats()
                if (chatsResult is ApiResult.Success) {
                    val lastChat = chatsResult.data.lastOrNull { it.tutorId == creatorId || it.studentId == creatorId }
                    if (lastChat != null) {
                        _actionState.value = ApiResult.Success(Pair(lastChat.id, creatorName))
                    } else {
                        _actionState.value = ApiResult.Success(Unit)
                    }
                } else {
                    _actionState.value = ApiResult.Success(Unit)
                }
                loadData()
            } else {
                _actionState.value = result as ApiResult<Any>
            }
        }
    }

    fun clearActionState() {
        _actionState.value = null
    }
}

class TutoriasViewModelFactory(
    private val repository: TutoriasRepository,
    private val chatRepository: ChatRepository,
    private val role: UserRole
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TutoriasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TutoriasViewModel(repository, chatRepository, role) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
