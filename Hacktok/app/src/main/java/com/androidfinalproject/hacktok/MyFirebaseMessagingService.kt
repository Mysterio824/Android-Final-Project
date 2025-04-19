package com.androidfinalproject.hacktok

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.AuthService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var authService: AuthService

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val TAG = "MyFirebaseMsgService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "Notification", it.body ?: "", null)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"]
        val title = data["title"]
        val body = data["body"]
        val postId = data["postId"]

        if (title == null || body == null) {
            Log.e(TAG, "Received message with missing title or body in data payload")
            return
        }

        sendNotification(title, body, postId)
    }

    private fun sendRegistrationToServer(token: String?) {
        serviceScope.launch {
            try {
                val userId = authService.getCurrentUserId()

                if (userId != null && token != null) {
                    val db = FirebaseFirestore.getInstance()
                    val userDocRef = db.collection("users").document(userId)

                    userDocRef.set(mapOf("fcmToken" to token), SetOptions.merge())
                        .addOnSuccessListener { Log.d(TAG, "FCM Token successfully written to Firestore for user $userId") }
                        .addOnFailureListener { e -> Log.e(TAG, "Error writing FCM Token to Firestore", e) }

                } else {
                    Log.w(TAG, "Cannot update FCM token: User not logged in or token is null.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send FCM token to Firestore", e)
            }
        }
    }

    private fun sendNotification(messageTitle: String, messageBody: String, postId: String?) {
        val intent = Intent(this, MainActivity::class.java)
        if (postId != null) {
            intent.putExtra("postId", postId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val name = resources.getString(R.string.default_notification_channel_name)
        val description = resources.getString(R.string.default_notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        notificationManager.createNotificationChannel(channel)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
