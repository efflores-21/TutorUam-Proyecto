package com.example.tutoruam_proyecto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tutoruam_proyecto.ui.model.LoginResponse
import com.example.tutoruam_proyecto.ui.repository.AuthRepository
import com.example.tutoruam_proyecto.ui.service.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<ApiResult<LoginResponse>?>(null)
    val loginState: StateFlow<ApiResult<LoginResponse>?> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            _loginState.value = authRepository.login(email, password)
        }
    }

    fun register(request: com.example.tutoruam_proyecto.ui.model.RegisterRequest) {
        viewModelScope.launch {
            _loginState.value = ApiResult.Loading
            _loginState.value = authRepository.register(request)
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}
