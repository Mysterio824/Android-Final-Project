package com.androidfinalproject.hacktok.service

import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface ApiService {
    @POST("change-password")
    suspend fun sendChangePasswordRequest(
        @Query("email") email: String,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String
    ): Response

    @POST("send-notification")
    suspend fun sendNotificationRequest(
        @Query("token") token: String,
        @Query("title") title: String,
        @Query("body") body: String,
        @Body data: Map<String, String>
    ): Response

    @POST("signup")
    suspend fun sendSignUpRequest(
        @Query("email") email: String,
        @Query("password") password: String
    ): Response

    @POST("reset-password")
    suspend fun sendResetPassword(
        @Query("email") email: String
    ): Response
    
    // Verification code methods
    @POST("verify-code")
    suspend fun verifyCode(
        @Query("email") email: String,
        @Query("code") code: String
    ): Response

    @POST("resend-verification")
    suspend fun resendVerificationCode(
        @Query("email") email: String
    ): Response

    @POST("set-username")
    suspend fun setUsername(
        @Query("email") email: String,
        @Query("username") username: String
    ): Response

    @GET("encryption-key")
    suspend fun getEncryptionKey(): String
}