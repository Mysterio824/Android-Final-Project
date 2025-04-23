package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.model.enums.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationService {

    suspend fun createNotification(
        recipientUserId: String,
        type: NotificationType,
        senderId: String?,
        relatedItemId: String?,
        actionUrl: String? = null,
        priority: String = "normal"
    ): String?

    suspend fun getMyNotifications(): List<Notification>

    suspend fun observeMyNotifications(): Flow<List<Notification>>

    suspend fun markNotificationAsRead(notificationId: String): Boolean

    suspend fun markAllMyNotificationsAsRead(): Boolean
    
    suspend fun getUnreadNotificationCount(): Int

    suspend fun deleteNotification(notificationId: String): Boolean
} 