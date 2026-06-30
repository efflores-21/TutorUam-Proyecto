package com.example.tutoruam_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.repository.AuthRepository
import com.example.tutoruam_proyecto.ui.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApiResult<Unit>?>(null)
    val uiState: StateFlow<ApiResult<Unit>?> = _uiState.asStateFlow()

    fun changeRole(newRole: String) {
        viewModelScope.launch {
            _uiState.value = ApiResult.Loading
            val result = repository.changeRole(newRole)
            _uiState.value = result
        }
    }

    fun clearState() {
        _uiState.value = null
    }
}

class PerfilViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfilViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
