package com.androidfinalproject.hacktok

import android.app.Application
import android.util.Log
import com.androidfinalproject.hacktok.utils.GooglePlayServicesHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        
        // Initialize security provider first to help with Google Play Services issues
        val securityProviderUpdated = GooglePlayServicesHelper.updateAndroidSecurityProvider(this)
        Log.d(TAG, "Security provider updated: $securityProviderUpdated")
        
        // Check if Google Play Services is available
        val gpsAvailable = GooglePlayServicesHelper.isGooglePlayServicesAvailable(this)
        Log.d(TAG, "Google Play Services available: $gpsAvailable")
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        Log.d(TAG, "Firebase initialized")
        
        // Initialize Google Play Services
        try {
            // This will trigger the initialization of Google Play Services
            firebaseAuth.currentUser?.let {
                Log.d(TAG, "Current user: ${it.uid}")
            } ?: Log.d(TAG, "No current user")
        } catch (e: Exception) {
            // Handle any initialization errors
            Log.e(TAG, "Error initializing Google Play Services", e)
        }
    }
} 