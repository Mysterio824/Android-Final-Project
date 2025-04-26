package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.enums.NotificationType

interface FcmService {
    fun initialize()
    suspend fun getToken(): String?
    suspend fun storeToken()
    fun showNotification(title: String, body: String, data: Map<String, String>)
    suspend fun sendNotification(recipientUserId: String, title: String, body: String, data: Map<String, String>): Boolean
    suspend fun sendInteractionNotification(recipientUserId: String, senderUserId: String, notificationType: NotificationType, itemId: String, content: String? = null): Boolean
    suspend fun removeFcmToken()
}