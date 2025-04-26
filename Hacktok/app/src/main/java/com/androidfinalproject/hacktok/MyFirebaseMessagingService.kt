package com.androidfinalproject.hacktok

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
open class AndroidEntryPointFirebaseMessagingService : FirebaseMessagingService(){

    // Inject the FcmServiceImpl (as FcmService)
    @Inject lateinit var fcmService: FcmService

    private val serviceJob = SupervisorJob()

    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val TAG = "MyFirebaseMsgService"


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "FCM Token Refreshed: ${token.take(10)}...")

        // 1. Immediately save to SharedPreferences cache
        TokenManager.saveTokenToPreferences(applicationContext, token)

        // 2. Launch a coroutine to store the token in Firestore via FcmService
        serviceScope.launch {
            try {
                Log.d(TAG, "Calling fcmService.storeToken() after token refresh...")
                // Ensure FcmService is properly injected via Hilt
                fcmService.storeToken() // Call the suspend function
                Log.d(TAG, "fcmService.storeToken() completed after refresh.")
            } catch (e: Exception) {
                Log.e(TAG, "Error calling fcmService.storeToken after refresh", e)
                // Consider retry logic or error reporting
            }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message Received! From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Processing message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        else if (remoteMessage.notification != null) {
            Log.d(TAG, "Processing message notification payload (app likely foreground).")
            val notification = remoteMessage.notification!!
            val data = mapOf(
                "title" to (notification.title ?: "Notification"),
                "body" to (notification.body ?: "")
            )
            handleDataMessage(data)
        } else {
            Log.w(TAG, "Received empty FCM message.")
        }
    }


    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Notification" // Provide default title
        val body = data["body"] ?: "" // Provide default body
        val notificationDocId = data["notificationDocId"] // Get history ID if sent from backend
        val deepLink = data["deepLink"] // Extract deep link for navigation

        // Log complete data payload for debugging
        Log.d(TAG, "Handling notification data message:")
        Log.d(TAG, "  Title: $title")
        Log.d(TAG, "  Body: $body")
        Log.d(TAG, "  NotificationDocId: $notificationDocId")
        Log.d(TAG, "  DeepLink: $deepLink")
        Log.d(TAG, "  Complete data: $data")
        
        fcmService.showNotification(title, body, data)
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Cancel coroutines when service is destroyed
    }
}