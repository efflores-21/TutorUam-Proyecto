package com.example.tutoruam_proyecto.ui.service

import com.example.tutoruam_proyecto.ui.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    // Clases y Solicitudes
    @GET("classes")
    suspend fun getClasses(@Query("subject") subject: String? = null): Response<List<ClassSessionResponse>>

    @POST("classes")
    suspend fun createClass(@Body request: CreateClassRequest): Response<ClassSessionResponse>

    @POST("classes/{id}/join")
    suspend fun joinClass(@Path("id") classId: Long): Response<Unit>

    @GET("requests")
    suspend fun getRequests(@Query("subject") subject: String? = null): Response<List<HelpRequestResponse>>

    @POST("requests")
    suspend fun createRequest(@Body request: CreateClassRequest): Response<HelpRequestResponse>

    // Chats
    @GET("chats")
    suspend fun getChats(): Response<List<Chat>>

    @GET("chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: Long,
        @Query("since") since: String? = null
    ): Response<List<Message>>

    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: Long,
        @Body request: SendMessageRequest
    ): Response<Message>

    @POST("users/me/role")
    suspend fun changeRole(@Query("newRole") newRole: String): Response<Unit>
}
