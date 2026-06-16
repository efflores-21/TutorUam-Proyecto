package com.example.tutoruam_proyecto.ui.service

object TokenManager {
    private var token: String? = null

    fun saveToken(value: String) {
        token = value
    }

    fun getToken(): String? = token

    fun clear() {
        token = null
    }
}
