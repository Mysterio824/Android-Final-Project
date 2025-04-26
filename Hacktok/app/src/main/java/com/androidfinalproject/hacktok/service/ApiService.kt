package com.androidfinalproject.hacktok.service

import okhttp3.Response

interface ApiService {
    suspend fun sendChangePasswordRequest(email: String, oldPassword: String, newPassword: String) : Response
    suspend fun sendNotificationRequest(token: String, title: String, body: String, data: Map<String, String>) : Response
    suspend fun sendSignUpRequest(email: String, password: String) : Response
    suspend fun sendResetPassword(email: String): Response
    
    // Verification code methods
    suspend fun verifyCode(email: String, code: String): Response
    suspend fun resendVerificationCode(email: String): Response
    suspend fun setUsername(email: String, username: String): Response
}