package com.example.tutoruam_proyecto.ui.service

import com.example.tutoruam_proyecto.ui.repository.*

object ServiceLocator {

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService)
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(apiService)
    }

    val tutoriasRepository: TutoriasRepository by lazy {
        TutoriasRepositoryImpl(apiService)
    }
}
