package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.model.enums.NotificationType
import kotlinx.coroutines.flow.Flow

/**
 * Service interface for managing user notifications.
 */
interface NotificationService {

    /**
     * Creates a new notification for a specific user.
     *
     * @param recipientUserId The ID of the user who should receive the notification.
     * @param type The type of the notification (e.g., FRIEND_REQUEST, POST_LIKE).
     * @param senderId The ID of the user who triggered the notification (optional, e.g., for system notifications).
     * @param relatedItemId The ID of the related entity (e.g., post ID, comment ID, sender user ID for friend requests).
     * @param content A brief description of the notification content.
     * @param actionUrl An optional URL to navigate to when the notification is clicked.
     * @param priority The priority of the notification ("normal" or "high").
     * @return The ID of the created notification, or null if creation failed.
     */
    suspend fun createNotification(
        recipientUserId: String,
        type: NotificationType,
        senderId: String?,
        relatedItemId: String?,
        content: String? = null, // Optional, can be generated based on type/sender/item if null
        actionUrl: String? = null,
        priority: String = "normal"
    ): String?

    /**
     * Retrieves all notifications for the currently logged-in user.
     *
     * @return A list of Notification objects.
     */
    suspend fun getMyNotifications(): List<Notification>
    
    /**
     * Observes changes to the notifications for the currently logged-in user.
     *
     * @return A Flow emitting the list of notifications whenever changes occur.
     */
    suspend fun observeMyNotifications(): Flow<List<Notification>>

    /**
     * Marks a specific notification as read for the currently logged-in user.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun markNotificationAsRead(notificationId: String): Boolean

    /**
     * Marks all notifications as read for the currently logged-in user.
     *
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun markAllMyNotificationsAsRead(): Boolean
    
    /**
     * Gets the count of unread notifications for the currently logged-in user.
     *
     * @return The number of unread notifications.
     */
    suspend fun getUnreadNotificationCount(): Int

    /**
     * Deletes a specific notification for the currently logged-in user.
     *
     * @param notificationId The ID of the notification to delete.
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun deleteNotification(notificationId: String): Boolean
} 