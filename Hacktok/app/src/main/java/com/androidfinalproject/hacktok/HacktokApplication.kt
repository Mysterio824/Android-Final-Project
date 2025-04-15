package com.androidfinalproject.hacktok

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HacktokApplication : Application() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Google Play Services
        try {
            // This will trigger the initialization of Google Play Services
            firebaseAuth.currentUser
        } catch (e: Exception) {
            // Handle any initialization errors
            e.printStackTrace()
        }
    }
} 