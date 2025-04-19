package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.enums.NotificationType

/**
 * Service interface for handling Firebase Cloud Messaging (FCM) notifications.
 */
interface FcmService {
    /**
     * Sends a notification to a specific user
     *
     * @param userId The ID of the user to receive the notification
     * @param title The notification title
     * @param body The notification body text
     * @param data Optional key-value pairs to include with the notification
     * @return True if notification was sent successfully, false otherwise
     */
    suspend fun sendNotification(
        userId: String, 
        title: String, 
        body: String, 
        data: Map<String, String> = emptyMap()
    ): Boolean

    /**
     * Sends a notification to a user about a like/comment action
     *
     * @param recipientUserId The ID of the user who should receive the notification
     * @param senderUserId The ID of the user who liked/commented
     * @param notificationType The type of notification (POST_LIKE, POST_COMMENT, etc.)
     * @param itemId The ID of the post or comment that was interacted with
     * @return True if notification was sent successfully, false otherwise
     */
    suspend fun sendInteractionNotification(
        recipientUserId: String,
        senderUserId: String,
        notificationType: NotificationType,
        itemId: String
    ): Boolean
} 