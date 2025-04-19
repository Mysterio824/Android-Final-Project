package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.enums.NotificationType


// Interface definition (assuming it exists)
interface FcmService {
    fun initialize()
    suspend fun getToken(): String? // Changed to suspend
    suspend fun storeToken() // Changed to suspend
    fun showNotification(title: String, body: String, data: Map<String, String>)
    suspend fun sendNotification(recipientUserId: String, title: String, body: String, data: Map<String, String>): Boolean
    suspend fun sendInteractionNotification(recipientUserId: String, senderUserId: String, notificationType: NotificationType, itemId: String): Boolean
    suspend fun removeFcmToken()
}