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

        // Log entire message for debugging
        // Log.d(TAG, "Message Data payload: ${remoteMessage.data}")
        // remoteMessage.notification?.let { Log.d(TAG, "Message Notification payload: Title=${it.title}, Body=${it.body}") }

        // Prioritize Data Payload (more reliable for background handling)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Processing message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        // Handle Notification Payload (if app is in foreground)
        // You might choose to ignore this if data payload contains same info
        else if (remoteMessage.notification != null) {
            Log.d(TAG, "Processing message notification payload (app likely foreground).")
            val notification = remoteMessage.notification!!
            // Construct a data map if needed, or use defaults
            val data = mapOf(
                "title" to (notification.title ?: "Notification"),
                "body" to (notification.body ?: "")
                // Add other fields if available/needed
            )
            handleDataMessage(data) // Reuse data handling logic
        } else {
            Log.w(TAG, "Received empty FCM message.")
        }
    }


    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Notification" // Provide default title
        val body = data["body"] ?: "" // Provide default body
        val notificationDocId = data["notificationDocId"] // Get history ID if sent from backend

        Log.d(TAG, "Handling data message: Title='$title', Body='$body', Data=$data")
        fcmService.showNotification(title, body, data)
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel() // Cancel coroutines when service is destroyed
    }
}