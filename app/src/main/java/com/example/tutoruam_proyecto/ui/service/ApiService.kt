package com.example.tutoruam_proyecto.ui.service

import com.example.tutoruam_proyecto.ui.model.LoginRequest
import com.example.tutoruam_proyecto.ui.model.LoginResponse
import com.example.tutoruam_proyecto.ui.model.RegisterRequest
import com.example.tutoruam_proyecto.ui.model.TutorResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    @GET("tutors")
    suspend fun getTutors(
        @Query("subject") subject: String? = null
    ): Response<List<TutorResponse>>

    @GET("tutors/{id}")
    suspend fun getTutorById(
        @Path("id") tutorId: Int
    ): Response<TutorResponse>
}
