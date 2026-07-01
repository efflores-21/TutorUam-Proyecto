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
    suspend fun getClasses(
        @Query("subject") subject: String? = null,
        @Query("tutorId") tutorId: Long? = null,
        @Query("studentId") studentId: Long? = null
    ): Response<List<ClassSessionResponse>>

    @POST("classes")
    suspend fun createClass(@Body request: CreateClassRequest): Response<ClassSessionResponse>

    @POST("classes/{id}/join")
    suspend fun joinClass(@Path("id") classId: Long): Response<Unit>

    @GET("requests")
    suspend fun getRequests(
        @Query("subject") subject: String? = null,
        @Query("studentId") studentId: Long? = null,
        @Query("tutorAssignedId") tutorAssignedId: Long? = null
    ): Response<List<HelpRequestResponse>>

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

    // --- NUEVOS ENDPOINTS ---

    @GET("classes/{id}/students")
    suspend fun getClassStudents(@Path("id") classId: Long): Response<List<UserProfile>>

    @DELETE("classes/{id}/students/{studentId}")
    suspend fun removeStudent(@Path("id") classId: Long, @Path("studentId") studentId: Long): Response<Unit>

    @POST("classes/{id}/create-chat")
    suspend fun createGroupChat(@Path("id") classId: Long): Response<Chat>

    @POST("chats")
    suspend fun createChat(@Query("recipientId") recipientId: Long): Response<Chat>

    @POST("chats/{chatId}/participants")
    suspend fun addParticipant(@Path("chatId") chatId: Long, @Query("userId") userId: Long): Response<Unit>

    @DELETE("chats/{chatId}/participants/{userId}")
    suspend fun removeParticipant(@Path("chatId") chatId: Long, @Path("userId") userId: Long): Response<Unit>

    @GET("chats/{chatId}/participants")
    suspend fun getChatParticipants(@Path("chatId") chatId: Long): Response<List<UserProfile>>

    // --- Endpoints para clases ---
    @PUT("classes/{id}")
    suspend fun updateClass(@Path("id") classId: Long, @Body request: CreateClassRequest): Response<ClassSessionResponse>

    @DELETE("classes/{id}")
    suspend fun deleteClass(@Path("id") classId: Long): Response<Unit>

    // --- Endpoints para solicitudes ---
    @PUT("requests/{id}")
    suspend fun updateRequest(@Path("id") requestId: Long, @Body request: CreateHelpRequest): Response<HelpRequestResponse>

    @DELETE("requests/{id}")
    suspend fun deleteRequest(@Path("id") requestId: Long): Response<Unit>

    @POST("requests/{id}/assign")
    suspend fun assignRequest(@Path("id") requestId: Long): Response<Unit>

    @PATCH("classes/{id}/status")
    suspend fun updateClassStatus(@Path("id") classId: Long, @Body request: UpdateClassStatusRequest): Response<Unit>

    @GET("classes/{id}")
    suspend fun getClassById(@Path("id") classId: Long): Response<ClassSessionResponse>

    // --- Ratings ---
    @POST("ratings")
    suspend fun createRating(@Body request: RatingRequest): Response<Rating>

    @GET("ratings/tutor/{tutorId}")
    suspend fun getRatingsByTutor(@Path("tutorId") tutorId: Long): Response<List<Rating>>

    @GET("ratings/tutor/{tutorId}/average")
    suspend fun getAverageRating(@Path("tutorId") tutorId: Long): Response<Double>
}
