package com.androidfinalproject.hacktok

import android.app.Application
import android.util.Log
import com.androidfinalproject.hacktok.utils.GooglePlayServicesHelper
import com.androidfinalproject.hacktok.utils.TokenManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HacktokApplication : Application() {
    companion object {
        private const val TAG = "HacktokApplication"
    }

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase only once
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
        }

        // Initialize security provider first to help with Google Play Services issues
        val securityProviderUpdated = GooglePlayServicesHelper.updateAndroidSecurityProvider(this)
        Log.d(TAG, "Security provider updated: $securityProviderUpdated")

        // Check if Google Play Services is available
        val gpsAvailable = GooglePlayServicesHelper.isGooglePlayServicesAvailable(this)
        Log.d(TAG, "Google Play Services available: $gpsAvailable")

        // Only proceed with token request if Play Services is available
        if (gpsAvailable) {
            // Request FCM token with detailed error handling
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Get new FCM registration token
                    val token = task.result
                    Log.d(TAG, "FCM Registration Token: $token")

                    // Save token in SharedPreferences
                    TokenManager.saveTokenToPreferences(applicationContext, token)

                    // Save token for current user if logged in
                    firebaseAuth.currentUser?.uid?.let { userId ->
                        firestore.collection("users").document(userId)
                            .set(mapOf("fcmToken" to token), SetOptions.merge())
                            .addOnSuccessListener { Log.d(TAG, "FCM Token saved to Firestore") }
                            .addOnFailureListener { e -> Log.e(TAG, "Error saving FCM token", e) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting FCM token", e)
            }
        } else {
            Log.e(TAG, "Cannot request FCM token - Google Play Services not available")
        }

        // Initialize Google Play Services
        try {
            // This will trigger the initialization of Google Play Services
            firebaseAuth.currentUser?.let {
                Log.d(TAG, "Current user: ${it.uid}")
            } ?: Log.d(TAG, "No current user")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Google Play Services", e)
        }
    }
}