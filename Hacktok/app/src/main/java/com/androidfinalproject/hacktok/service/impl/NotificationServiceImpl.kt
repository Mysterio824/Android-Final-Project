package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.NotificationRepository
import com.androidfinalproject.hacktok.repository.UserRepository // To get sender details
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationServiceImpl @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val authService: AuthService
) : NotificationService {

    private val TAG = "NotificationService"

    override suspend fun createNotification(
        recipientUserId: String,
        type: NotificationType,
        senderId: String?,
        relatedItemId: String?,
        content: String?,
        actionUrl: String?,
        priority: String
    ): String? {
        return try {
            // Fetch sender details if senderId is provided
            val sender = if (senderId != null) {
                userRepository.getUserById(senderId)
            } else {
                null
            }

            // Generate default content if not provided
            val finalContent = content ?: generateDefaultContent(type, sender?.username)
            
            if (finalContent == null) {
                 Log.e(TAG, "Could not generate content for notification type $type")
                 return null
            }

            val notification = Notification(
                userId = recipientUserId,
                type = type,
                senderId = senderId,
                senderName = sender?.username,
                senderImage = sender?.profileImage,
                relatedId = relatedItemId,
                content = finalContent,
                createdAt = Date(), // Set creation timestamp
                isRead = false,
                actionUrl = actionUrl,
                priority = priority
            )
            
            notificationRepository.addNotification(notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification: ${e.message}", e)
            null
        }
    }

    private fun generateDefaultContent(type: NotificationType, senderName: String?): String? {
        val name = senderName ?: "Someone"
        return when (type) {
            NotificationType.FRIEND_REQUEST -> "$name sent you a friend request"
            NotificationType.FRIEND_ACCEPT -> "$name accepted your friend request"
            NotificationType.POST_LIKE -> "$name liked your post"
            NotificationType.POST_COMMENT -> "$name commented on your post"
            NotificationType.COMMENT_REPLY -> "$name replied to your comment"
            NotificationType.COMMENT_LIKE -> "$name liked your comment"
            NotificationType.ADMIN_NOTIFICATION -> "Important system notification" // Content should likely be provided for this type
        }
    }

    override suspend fun getMyNotifications(): List<Notification> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyList()
        return try {
            notificationRepository.getNotificationsByUser(currentUserId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting notifications for user $currentUserId: ${e.message}", e)
            emptyList()
        }
    }

    override fun observeMyNotifications(): Flow<List<Notification>> {
        // TODO: Implement observation using callbackFlow in Repository or Service
        // For now, returning a flow that emits the current list once
        return flow {
            emit(getMyNotifications())
        }
        // Alternatively, return emptyFlow() if observation is not yet supported
        // return emptyFlow()
    }

    override suspend fun markNotificationAsRead(notificationId: String): Boolean {
        // We don't necessarily need the current user ID here as the repository
        // operates directly on the notificationId which should be unique.
        // However, adding a check could prevent accidental marking if needed, but complicates the interface.
        return try {
            notificationRepository.markAsRead(notificationId)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification $notificationId as read: ${e.message}", e)
            false
        }
    }

    override suspend fun markAllMyNotificationsAsRead(): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        return try {
            val notifications = notificationRepository.getNotificationsByUser(currentUserId)
            notifications.filter { !it.isRead }.forEach { 
                 // Potential optimization: Batch Firestore writes if marking many notifications
                 notificationRepository.markAsRead(it.id!!)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking all notifications as read for user $currentUserId: ${e.message}", e)
            false
        }
    }
    
    override suspend fun getUnreadNotificationCount(): Int {
         val currentUserId = authService.getCurrentUserId() ?: return 0
         return try {
             notificationRepository.getNotificationsByUser(currentUserId).count { !it.isRead }
         } catch (e: Exception) {
             Log.e(TAG, "Error getting unread notification count for user $currentUserId: ${e.message}", e)
             0
         }
    }

    override suspend fun deleteNotification(notificationId: String): Boolean {
        // Similar to markAsRead, user ID check might be optional depending on security needs.
        return try {
            notificationRepository.deleteNotification(notificationId)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting notification $notificationId: ${e.message}", e)
            false
        }
    }
} 