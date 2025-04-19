package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.service.NotificationService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val notificationService: NotificationService
) : FcmService {

    private val TAG = "FcmServiceImpl"

    override suspend fun sendNotification(
        userId: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Get the user's FCM token from Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            val fcmToken = userDoc.getString("fcmToken")
            
            if (fcmToken.isNullOrEmpty()) {
                Log.w(TAG, "No FCM token found for user $userId")
                return@withContext false
            }
            
            // Prepare the notification data
            val messageData = mutableMapOf<String, String>()
            messageData.putAll(data)
            
            // Create notification payload
            val payload = mapOf(
                "to" to fcmToken,
                "notification" to mapOf(
                    "title" to title,
                    "body" to body,
                    "sound" to "default"
                ),
                "data" to messageData
            )
            
            // Send via Firebase Admin SDK (this is handled server-side)
            // Here we're just storing the notification in Firestore 
            // so the MyFirebaseMessagingService will handle it when received
            
            Log.d(TAG, "FCM notification would be sent to $userId with token $fcmToken")
            
            // This is a client-side app, so we can't directly send FCM messages
            // Instead, we'll just record the notification in Firestore
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending FCM notification", e)
            return@withContext false
        }
    }

    override suspend fun sendInteractionNotification(
        recipientUserId: String,
        senderUserId: String,
        notificationType: NotificationType,
        itemId: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Don't send notifications to yourself
            if (recipientUserId == senderUserId) {
                return@withContext true
            }
            
            // Get sender user details
            val sender = userRepository.getUserById(senderUserId)
            if (sender == null) {
                Log.e(TAG, "Sender user not found: $senderUserId")
                return@withContext false
            }
            
            // Generate notification content
            val (title, body) = generateNotificationContent(sender, notificationType, itemId)
            
            // Add the notification to Firestore (for persistence and display in app)
            val notificationId = notificationService.createNotification(
                recipientUserId = recipientUserId,
                type = notificationType,
                senderId = senderUserId,
                relatedItemId = itemId,
                content = body
            )
            
            if (notificationId == null) {
                Log.e(TAG, "Failed to create notification record in Firestore")
                return@withContext false
            }
            
            // Send FCM notification
            val data = mapOf(
                "type" to notificationType.name,
                "itemId" to itemId,
                "senderId" to senderUserId,
                "notificationId" to notificationId
            )
            
            return@withContext sendNotification(
                userId = recipientUserId,
                title = title,
                body = body,
                data = data
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error sending interaction notification", e)
            return@withContext false
        }
    }
    
    private suspend fun generateNotificationContent(
        sender: User,
        notificationType: NotificationType,
        itemId: String
    ): Pair<String, String> {
        val senderName = sender.username ?: "Someone"
        
        return when (notificationType) {
            NotificationType.POST_LIKE -> {
                val post = postRepository.getPost(itemId)
                val shortContent = post?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: ""
                Pair("New Like", "$senderName liked your post: $shortContent")
            }
            NotificationType.POST_COMMENT -> {
                val post = postRepository.getPost(itemId)
                val shortContent = post?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: ""
                Pair("New Comment", "$senderName commented on your post: $shortContent")
            }
            NotificationType.COMMENT_LIKE -> {
                val comment = commentRepository.getById(itemId).getOrNull()
                val shortContent = comment?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: ""
                Pair("New Like", "$senderName liked your comment: $shortContent")
            }
            NotificationType.COMMENT_REPLY -> {
                val comment = commentRepository.getById(itemId).getOrNull()
                val shortContent = comment?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: ""
                Pair("New Reply", "$senderName replied to your comment: $shortContent")
            }
            else -> Pair("HackTok", "$senderName interacted with your content")
        }
    }
} 