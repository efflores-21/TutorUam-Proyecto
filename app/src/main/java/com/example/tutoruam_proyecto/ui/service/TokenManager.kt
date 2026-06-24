package com.example.tutoruam_proyecto.ui.service

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "tutoruam_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"

    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(token: String?, userId: Long?, name: String?, email: String?, role: String?) {
        sharedPreferences?.edit()?.apply {
            putString(KEY_TOKEN, token)
            userId?.let { putLong(KEY_USER_ID, it) }
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_ROLE, role)
            apply()
        }
    }

    fun getToken(): String? = sharedPreferences?.getString(KEY_TOKEN, null)
    fun getUserId(): Long? = sharedPreferences?.getLong(KEY_USER_ID, -1L).takeIf { it != -1L }
    fun getName(): String = sharedPreferences?.getString(KEY_NAME, "Usuario") ?: "Usuario"
    fun getEmail(): String = sharedPreferences?.getString(KEY_EMAIL, "usuario@uam.edu.ni") ?: "usuario@uam.edu.ni"
    fun getRole(): String = sharedPreferences?.getString(KEY_ROLE, "ESTUDIANTE") ?: "ESTUDIANTE"

    fun clear() {
        sharedPreferences?.edit()?.clear()?.apply()
    }
}
