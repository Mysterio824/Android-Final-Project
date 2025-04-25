package com.androidfinalproject.hacktok.service.impl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.androidfinalproject.hacktok.MainActivity
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.service.ApiService
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.utils.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.*

@Singleton
class FcmServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val apiService: ApiService
) : FcmService {

    private val TAG = "FcmServiceImpl"
    private val CHANNEL_ID = "default_channel"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        createNotificationChannel()
        initialize()
    }

    override fun initialize() {
        serviceScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                Log.d(TAG, "User $userId logged in. Ensuring FCM token is stored.")
                storeToken() // Attempt to store token on initialization/login
            } else {
                Log.d(TAG, "No user logged in during init, FCM token not stored.")
            }
        }
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        val cachedToken = TokenManager.getTokenFromPreferences(context)
        if (cachedToken != null) {
            Log.d(TAG, "Using cached FCM token from SharedPreferences: ${cachedToken.take(10)}...")
            return@withContext cachedToken
        }

        Log.d(TAG, "No cached token, attempting to fetch new token.")
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "Fetched new FCM Token: ${token.take(10)}...")
            TokenManager.saveTokenToPreferences(context, token)
            return@withContext token
        } catch (e: Exception) {
            Log.e(TAG, "Fetching FCM token failed", e)
            return@withContext null // Return null on failure
        }
    }

    override suspend fun storeToken() = withContext(Dispatchers.IO) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Log.w(TAG, "storeToken called but no user logged in.")
            return@withContext
        }

        val token = getToken() // Call the suspend version

        if (token.isNullOrEmpty()) {
            Log.e(TAG, "Cannot store token: Fetched token is null or empty for user $currentUserId")
            return@withContext
        }

        Log.d(TAG, "Attempting to store FCM token for user $currentUserId: ${token.take(10)}...")

        try {
            val userDocRef = firestore.collection("users").document(currentUserId)
            // Check existing token *before* writing (optional but good practice)
            val document = userDocRef.get().await()
            val existingToken = document.getString("fcmToken")

            if (existingToken == token) {
                Log.d(TAG, "Token already up-to-date in Firestore for user $currentUserId.")
                return@withContext
            }

            Log.d(TAG, "Token changed (or first time). Existing: ${existingToken?.take(10)}..., New: ${token.take(10)}.... Updating Firestore.")
            // Use set with merge for simplicity and robustness (creates doc if needed)
            userDocRef.set(mapOf("fcmToken" to token), SetOptions.merge()).await()
            Log.d(TAG, "FCM token stored successfully via set/merge for user $currentUserId")

        } catch (e: Exception) {
            Log.e(TAG, "CRITICAL: Error storing/updating FCM token in Firestore for user $currentUserId", e)
            // Consider retries or reporting this error
        }
    }

    override fun showNotification(title: String, body: String, data: Map<String, String>) {
        Log.d(TAG, "Preparing to show notification. Title: $title, Body: $body, Data: $data")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add specific data likely needed by MainActivity to navigate
            data["type"]?.let { putExtra("notificationType", it) }
            data["itemId"]?.let { putExtra("notificationItemId", it) }
            data["senderId"]?.let { putExtra("notificationSenderId", it) }
            data["notificationId"]?.let { putExtra("notificationDocId", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_ic_notification) // Ensure this drawable exists
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true) // Dismiss notification when tapped
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Use HIGH for important notifications
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Show longer text

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = data["notificationId"]?.hashCode() ?: System.currentTimeMillis().toInt()
        Log.d(TAG, "Showing notification with ID: $notificationId")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override suspend fun sendNotification(
        recipientUserId: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Initiating FCM send request for user $recipientUserId. Title: $title")

        try {
            val userDocRef = firestore.collection("users").document(recipientUserId)
            val document = userDocRef.get().await()
            val recipientToken = document.getString("fcmToken")
            
            if (recipientToken.isNullOrEmpty()) {
                Log.e(TAG, "Recipient user $recipientUserId has no FCM token")
                return@withContext false
            }
            
            Log.d(TAG, "Retrieved recipient's FCM token: ${recipientToken.take(10)}...")

            val response = apiService.sendNotificationRequest(recipientToken, title, body, data)

            val isSuccessful = response.isSuccessful
            
            if (isSuccessful) {
                val responseBody = response.body?.string()
                Log.d(TAG, "Server successfully processed notification request: $responseBody")
            } else {
                Log.e(TAG, "Server error: ${response.code} - ${response.message}")
            }
            
            return@withContext isSuccessful

        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification via local server", e)
            return@withContext false
        }
    }

    override suspend fun sendInteractionNotification(
        recipientUserId: String,
        senderUserId: String,
        notificationType: NotificationType,
        itemId: String // e.g., Post ID, Comment ID
    ): Boolean = withContext(Dispatchers.IO) {
        Log.d(TAG, "Processing interaction notification: Type=$notificationType, Recipient=$recipientUserId, Sender=$senderUserId, Item=$itemId")

        // Don't send notifications to yourself
        if (recipientUserId == senderUserId) {
            Log.d(TAG, "Skipping self-notification.")
            return@withContext true // Indicate success as no action needed
        }

        try {
            // Get sender user details
            val sender = userRepository.getUserById(senderUserId)
            if (sender == null) {
                Log.e(TAG, "Sender user not found: $senderUserId. Cannot send interaction notification.")
                return@withContext false
            }

            // Generate notification content
            val (title, body) = generateNotificationContent(sender, notificationType, itemId)

            // Prepare data payload for FCM (and backend)
            // Include enough info for the recipient app to handle the notification tap
            val fcmData = mapOf(
                "type" to notificationType.name,
                "itemId" to itemId,
                "senderId" to senderUserId,
            )

            // Store notification in Firestore first
            val notification = com.androidfinalproject.hacktok.model.Notification(
                userId = recipientUserId,
                type = notificationType,
                senderId = senderUserId,
                senderName = sender.username,
                senderImage = sender.profileImage,
                relatedId = itemId,
                content = body,
                createdAt = Date(),
                isRead = false,
                priority = "normal"
            )

            try {
                firestore.collection("notifications").add(notification).await()
                Log.d(TAG, "Notification stored in Firestore successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error storing notification in Firestore", e)
                return@withContext false
            }

            // Try to send FCM notification, but don't fail if it doesn't work
            try {
                val backendCallInitiated = sendNotification(
                    recipientUserId = recipientUserId,
                    title = title,
                    body = body,
                    data = fcmData
                )

                if (!backendCallInitiated) {
                    Log.w(TAG, "FCM notification failed, but notification was stored in Firestore")
                }
            } catch (e: Exception) {
                Log.w(TAG, "FCM notification failed, but notification was stored in Firestore", e)
            }

            return@withContext true

        } catch (e: Exception) {
            Log.e(TAG, "Error processing or sending interaction notification", e)
            return@withContext false
        }
    }

    private suspend fun generateNotificationContent(
        sender: User,
        notificationType: NotificationType,
        itemId: String
    ): Pair<String, String> {
        Log.d(TAG, "Generating content for type: $notificationType, item: $itemId")
        // Add try-catch blocks around repository calls for more robustness
        val senderName = sender.username ?: "Someone"
        return try {
            when (notificationType) {
                NotificationType.POST_LIKE -> {
                    val post = postRepository.getPost(itemId)
                    val shortContent = post?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: "your post"
                    Pair("New Like", "$senderName liked your post: $shortContent")
                }
                NotificationType.POST_COMMENT -> {
                    val post = postRepository.getPost(itemId)
                    val shortContent = post?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: "your post"
                    Pair("New Comment", "$senderName commented on your post: $shortContent")
                }
                NotificationType.COMMENT_LIKE -> {
                    val comment = commentRepository.getById(itemId).getOrNull()
                    val shortContent = comment?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: "your comment"
                    Pair("New Like", "$senderName liked your comment: $shortContent")
                }
                NotificationType.COMMENT_REPLY -> {
                    val comment = commentRepository.getById(itemId).getOrNull()
                    val shortContent = comment?.content?.take(30)?.let { if (it.length >= 30) "$it..." else it } ?: "your comment"
                    Pair("New Reply", "$senderName replied to your comment: $shortContent")
                }
                NotificationType.NEW_MESSAGE -> {
                    Pair("New Message", "$senderName send you a new message ")
                }
                NotificationType.SECRET_CRUSH -> {
                    Pair("Secret Crush", "$senderName has a secret crush on you! 💕")
                }
                else -> Pair("HackTok", "$senderName interacted with you")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching details in generateNotificationContent", e)
            Pair("HackTok Notification", "$senderName interacted with you") // Fallback content
        }
    }

    private fun createNotificationChannel() {
        val name = context.getString(R.string.default_notification_channel_name)
        val description = context.getString(R.string.default_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH // Use HIGH for notifications
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
            // Configure other channel settings if needed (vibration, lights etc.)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel '$CHANNEL_ID' created or already exists.")
    }

    // Keep the delay method
    suspend fun delay(timeMillis: Long) = kotlinx.coroutines.delay(timeMillis)

    override suspend fun removeFcmToken() = withContext(Dispatchers.IO) {
        try {
            val currentUserId = auth.currentUser?.uid
            if (currentUserId == null) {
                Log.d(TAG, "No user is currently logged in, no token to remove")
                TokenManager.clearTokenFromPreferences(context)
                return@withContext
            }

            Log.d(TAG, "Removing FCM token for user: $currentUserId")
            try {
                val userDocRef = firestore.collection("users").document(currentUserId)
                userDocRef.update("fcmToken", "").await()
                Log.d(TAG, "FCM token removed from Firestore for user: $currentUserId")
            } catch (e: Exception) {
                Log.e(TAG, "Error removing FCM token from Firestore", e)
            }

            TokenManager.clearTokenFromPreferences(context)
            Log.d(TAG, "FCM token cleared from local storage")

            try {
                FirebaseMessaging.getInstance().deleteToken().await()
                Log.d(TAG, "FCM token deleted from Firebase Messaging service")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting FCM token from Firebase", e)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in removeFcmToken()", e)
        }
    }
}