package com.example.tutoruam_proyecto.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.model.*
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
    private val authRepository: com.example.tutoruam_proyecto.ui.repository.AuthRepository,
    private val role: UserRole
) : ViewModel() {

    private val _itemsState = MutableStateFlow<ApiResult<List<TutorClass>>>(ApiResult.Loading)
    val itemsState: StateFlow<ApiResult<List<TutorClass>>> = _itemsState.asStateFlow()

    private val _myTutoriasState = MutableStateFlow<ApiResult<List<TutorClass>>>(ApiResult.Loading)
    val myTutoriasState: StateFlow<ApiResult<List<TutorClass>>> = _myTutoriasState.asStateFlow()

    private val _myClassesState = MutableStateFlow<ApiResult<List<TutorClass>>>(ApiResult.Loading)
    val myClassesState: StateFlow<ApiResult<List<TutorClass>>> = _myClassesState.asStateFlow()

    private val _actionState = MutableStateFlow<ApiResult<Any>?>(null)
    val actionState: StateFlow<ApiResult<Any>?> = _actionState.asStateFlow()

    private val _classStudentsState = MutableStateFlow<ApiResult<List<UserProfile>>>(ApiResult.Loading)
    val classStudentsState: StateFlow<ApiResult<List<UserProfile>>> = _classStudentsState.asStateFlow()

    private val _classDetailState = MutableStateFlow<ApiResult<TutorClass>>(ApiResult.Loading)
    val classDetailState: StateFlow<ApiResult<TutorClass>> = _classDetailState.asStateFlow()

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

    fun loadMyPosts() {
        viewModelScope.launch {
            _itemsState.value = ApiResult.Loading
            val userId = TokenManager.getUserId()
            _itemsState.value = if (role == UserRole.TUTOR) {
                repository.getClasses(tutorId = userId)
            } else {
                repository.getRequests(studentId = userId)
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

    fun assignRequest(requestId: Long) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = repository.assignRequest(requestId)
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadData()
            }
        }
    }

    fun findOrCreateChatWithUser(recipientId: Long, onResult: (Chat) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.createChat(recipientId)
            if (result is ApiResult.Success) {
                onResult(result.data)
            }
        }
    }

    fun loadClassStudents(classId: Long) {
        viewModelScope.launch {
            _classStudentsState.value = ApiResult.Loading
            _classStudentsState.value = repository.getClassStudents(classId)
        }
    }

    fun expelStudent(classId: Long, studentId: Long) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = repository.removeStudent(classId, studentId)
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadClassStudents(classId) // recargar lista
                loadData() // recargar lista principal
            }
        }
    }

    fun createGroupChatForClass(classId: Long, onSuccess: (Chat) -> Unit) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = repository.createGroupChat(classId)
            if (result is ApiResult.Success) {
                onSuccess(result.data)
                _actionState.value = result as ApiResult<Any>
                // Recargar chats para que aparezca en la lista
                chatRepository.getChats() 
            } else if (result is ApiResult.Error && result.code == 409) {
                // Si ya existe, obtenerlo de la lista actual de chats
                val chatsResult = chatRepository.getChats()
                if (chatsResult is ApiResult.Success) {
                    val existingChat = chatsResult.data.firstOrNull { it.isGroup && it.classSessionId == classId }
                    if (existingChat != null) {
                        onSuccess(existingChat)
                        _actionState.value = ApiResult.Success(existingChat)
                    } else {
                        // Si no está en la lista, recargar y buscar de nuevo
                        val refreshed = chatRepository.getChats()
                        if (refreshed is ApiResult.Success) {
                            val chat = refreshed.data.firstOrNull { it.isGroup && it.classSessionId == classId }
                            chat?.let { 
                                onSuccess(it) 
                                _actionState.value = ApiResult.Success(it)
                            }
                        }
                    }
                }
            } else {
                _actionState.value = result as ApiResult<Any>
            }
        }
    }

    fun updatePost(item: TutorClass, subject: String, time: String, description: String, maxStudents: Int) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = if (item.type == "CLASS") {
                val request = CreateClassRequest(subject, time, description, maxStudents)
                repository.updateClass(item.id, request)
            } else {
                val request = CreateHelpRequest(subject, description, time) // Reusamos el campo 'time' para timeLimit
                repository.updateRequest(item.id, request)
            }
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadMyPosts() // Recargar mis publicaciones
            }
        }
    }

    fun deletePost(item: TutorClass) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val result = if (item.type == "CLASS") {
                repository.deleteClass(item.id)
            } else {
                repository.deleteRequest(item.id)
            }
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadMyPosts() // Recargar mis publicaciones
            }
        }
    }

    fun clearActionState() {
        _actionState.value = null
    }

    fun loadMyTutorias() {
        viewModelScope.launch {
            _myTutoriasState.value = ApiResult.Loading
            _myTutoriasState.value = repository.getMyTutoriasAsTutor()
        }
    }

    fun loadMyClasses() {
        viewModelScope.launch {
            _myClassesState.value = ApiResult.Loading
            _myClassesState.value = repository.getMyClassesAsStudent()
        }
    }

    fun loadClassDetail(classId: Long) {
        viewModelScope.launch {
            _classDetailState.value = ApiResult.Loading
            _classDetailState.value = repository.getClassById(classId)
        }
    }

    fun toggleClassStatus(classId: Long, currentStatus: String) {
        viewModelScope.launch {
            _actionState.value = ApiResult.Loading
            val newStatus = if (currentStatus == "OPEN") "CLOSED" else "OPEN"
            val result = repository.updateClassStatus(classId, newStatus)
            _actionState.value = result as ApiResult<Any>
            if (result is ApiResult.Success) {
                loadData()
                loadClassDetail(classId)
            }
        }
    }

    fun getGroupChatForClass(classId: Long, onSuccess: (Chat) -> Unit) {
        viewModelScope.launch {
            val chatsResult = chatRepository.getChats()
            if (chatsResult is ApiResult.Success) {
                // Buscar por classSessionId y isGroup = true
                val chat = chatsResult.data.firstOrNull { it.isGroup && it.classSessionId == classId }
                if (chat != null) {
                    onSuccess(chat)
                } else {
                    // Si no existe, crearlo
                    createGroupChatForClass(classId, onSuccess)
                }
            }
        }
    }

    fun getChatWithCreator(classId: Long, creatorId: Long, onSuccess: (Chat) -> Unit) {
        viewModelScope.launch {
            val chatsResult = chatRepository.getChats()
            if (chatsResult is ApiResult.Success) {
                // Buscar un chat 1 a 1 con el creador (tutor)
                val chat = chatsResult.data.firstOrNull { 
                    !it.isGroup && (it.tutorId == creatorId || it.studentId == creatorId)
                }
                if (chat != null) {
                    onSuccess(chat)
                } else {
                    // Si no existe, crear uno
                    val result = authRepository.createChat(creatorId)
                    if (result is ApiResult.Success) {
                        onSuccess(result.data)
                    }
                }
            }
        }
    }
}

class TutoriasViewModelFactory(
    private val repository: TutoriasRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: com.example.tutoruam_proyecto.ui.repository.AuthRepository,
    private val role: UserRole
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TutoriasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TutoriasViewModel(repository, chatRepository, authRepository, role) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
