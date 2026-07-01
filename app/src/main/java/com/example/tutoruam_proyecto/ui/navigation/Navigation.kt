package com.example.tutoruam_proyecto.ui.navigation

import kotlinx.serialization.Serializable

// Rutas del flujo principal.
@Serializable
object LoginDestination

@Serializable
object RegisterDestination

@Serializable
object HomeDestination

// Rutas de la barra inferior dentro de Home.
@Serializable
object TutoriasDestination

@Serializable
object ChatDestination

@Serializable
object PerfilDestination

@Serializable
object MyPostsDestination

@Serializable
object MyTutoriasDestination

@Serializable
object MyClassesDestination

@Serializable
data class ChatDetailDestination(val chatId: Long, val otherUserName: String)

@Serializable
data class ClassDetailDestination(val classId: Long, val role: com.example.tutoruam_proyecto.ui.model.UserRole)

