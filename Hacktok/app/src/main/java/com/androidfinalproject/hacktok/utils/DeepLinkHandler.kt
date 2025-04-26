package com.androidfinalproject.hacktok.utils

import android.content.Intent
import android.util.Log
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.google.firebase.auth.FirebaseAuth

object DeepLinkHandler {
    private const val TAG = "DeepLinkHandler"
    
    // Store pending deep links for navigation after authentication
    private var pendingDeepLink: String? = null

    fun handleDeepLink(navController: NavController, intent: Intent, auth: FirebaseAuth) {
        val deepLink = intent.getStringExtra("deepLink")
        if (deepLink != null) {
            Log.d(TAG, "Processing deep link: $deepLink")
            
            // Check if we're already authenticated
            if (auth.currentUser != null) {
                navigateToDeepLink(navController, deepLink)
            } else {
                // Cache the deep link to navigate after authentication
                pendingDeepLink = deepLink
                Log.d(TAG, "Saving deep link for post-authentication navigation: $deepLink")
            }
        }
    }

    private fun navigateToDeepLink(navController: NavController, deepLink: String) {
        Log.d(TAG, "Navigating to deep link: $deepLink")
        
        // First navigate to MainRoute.Graph to ensure we're in the main navigation
        navController.navigate(MainRoute.Graph.route) {
            // Clear backstack to avoid nested navigation issues
            popUpTo(0) { inclusive = true }
        }
        
        // Then navigate to the specific deep link
        navController.navigate(deepLink)
    }

    fun checkPendingDeepLinks(navController: NavController) {
        pendingDeepLink?.let { deepLink ->
            Log.d(TAG, "Processing pending deep link after authentication: $deepLink")
            navigateToDeepLink(navController, deepLink)
            pendingDeepLink = null  // Clear after handling
        }
    }
} 