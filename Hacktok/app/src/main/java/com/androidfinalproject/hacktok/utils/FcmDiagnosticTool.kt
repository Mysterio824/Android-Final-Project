package com.androidfinalproject.hacktok.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Diagnostic tool to help troubleshoot FCM token issues
 */
object FcmDiagnosticTool {
    private const val TAG = "FcmDiagnosticTool"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Check the FCM token status and attempt to fix any issues
     */
    fun runDiagnostic(context: Context) {
        serviceScope.launch {
            try {
                Log.d(TAG, "Starting FCM diagnostic")
                
                // 1. Check if user is logged in
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Log.d(TAG, "No user is logged in, cannot proceed with diagnostics")
                    return@launch
                }
                
                val userId = user.uid
                Log.d(TAG, "Running diagnostics for user: $userId")
                
                // 2. Get cached token from SharedPreferences
                val cachedToken = TokenManager.getTokenFromPreferences(context)
                Log.d(TAG, "Cached token from SharedPreferences: ${cachedToken?.take(10) ?: "null"}")
                
                // 3. Get token from Firebase
                var firebaseToken: String? = null
                try {
                    val task = FirebaseMessaging.getInstance().token
                    firebaseToken = task.await()
                    Log.d(TAG, "Current FCM token from Firebase: ${firebaseToken?.take(10) ?: "null"}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting FCM token from Firebase", e)
                }
                
                // 4. Get token from Firestore
                var firestoreToken: String? = null
                try {
                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                    firestoreToken = userDoc.getString("fcmToken")
                    Log.d(TAG, "Current FCM token in Firestore: ${firestoreToken?.take(10) ?: "null"}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting FCM token from Firestore", e)
                }
                
                // 5. Compare tokens and fix any issues
                if (firebaseToken != null && firebaseToken.isNotEmpty()) {
                    // We have a valid Firebase token
                    
                    // Update SharedPreferences if needed
                    if (cachedToken != firebaseToken) {
                        Log.d(TAG, "Updating cached token in SharedPreferences")
                        TokenManager.saveTokenToPreferences(context, firebaseToken)
                    }
                    
                    // Update Firestore if needed
                    if (firestoreToken != firebaseToken) {
                        Log.d(TAG, "Updating FCM token in Firestore")
                        try {
                            FirebaseFirestore.getInstance().collection("users").document(userId)
                                .update("fcmToken", firebaseToken)
                                .await()
                            Log.d(TAG, "FCM token updated successfully in Firestore")
                        } catch (e: Exception) {
                            Log.e(TAG, "Error updating FCM token in Firestore with update", e)
                            
                            // Try with set + merge
                            try {
                                FirebaseFirestore.getInstance().collection("users").document(userId)
                                    .set(mapOf("fcmToken" to firebaseToken), SetOptions.merge())
                                    .await()
                                Log.d(TAG, "FCM token updated successfully in Firestore with set+merge")
                            } catch (e2: Exception) {
                                Log.e(TAG, "CRITICAL: Failed to update FCM token in Firestore even with set+merge", e2)
                            }
                        }
                        
                        // Verify the update
                        try {
                            val verifyDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                            val updatedToken = verifyDoc.getString("fcmToken")
                            Log.d(TAG, "Verification - Updated token in Firestore: ${updatedToken?.take(10) ?: "null"}")
                            
                            if (updatedToken != firebaseToken) {
                                Log.e(TAG, "CRITICAL: Token verification failed! The token in Firestore does not match the Firebase token.")
                            } else {
                                Log.d(TAG, "✅ Token verification succeeded! Everything is in sync.")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error verifying updated token", e)
                        }
                    } else {
                        Log.d(TAG, "✅ FCM token is already correct in Firestore")
                    }
                } else {
                    Log.e(TAG, "No valid FCM token from Firebase, cannot proceed with fixes")
                }
                
                Log.d(TAG, "FCM diagnostic completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error running FCM diagnostic", e)
            }
        }
    }
} 