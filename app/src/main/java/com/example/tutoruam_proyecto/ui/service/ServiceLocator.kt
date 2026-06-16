package com.example.tutoruam_proyecto.ui.service

import com.example.tutoruam_proyecto.ui.repository.AuthRepository
import com.example.tutoruam_proyecto.ui.repository.AuthRepositoryImpl
import com.example.tutoruam_proyecto.ui.repository.TutorRepository
import com.example.tutoruam_proyecto.ui.repository.TutorRepositoryImpl

object ServiceLocator {

    private val apiService: ApiService by lazy {
        RetrofitClient.instance
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService)
    }

    val tutorRepository: TutorRepository by lazy {
        TutorRepositoryImpl(apiService)
    }
}
