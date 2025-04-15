package com.androidfinalproject.hacktok.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        Log.d("AuthRepository", "Attempting Firebase sign-in with Google token: ${idToken.take(10)}...")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.let {
                Log.d("AuthRepository", "Firebase sign-in successful. User: ${it.uid}")
                checkAndCreateUserData(it)
            }
            authResult.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase sign-in with Google failed", e)
            null
        }
    }
    
    override suspend fun isUserAdmin(userId: String): Boolean {
        Log.d("AuthRepository", "Checking admin status for user: $userId")
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.getBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking admin status for $userId", e)
            false
        }
    }
    
    override suspend fun signOut() {
        Log.d("AuthRepository", "Signing out user")
        try {
            firebaseAuth.signOut()
            Log.d("AuthRepository", "User signed out successfully")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out user", e)
            throw e
        }
    }
    
    override fun getCurrentUser(): FirebaseUser? {
        Log.d("AuthRepository", "Getting current user")
        return firebaseAuth.currentUser
    }
    
    private suspend fun checkAndCreateUserData(user: FirebaseUser) {
        Log.d("AuthRepository", "Checking/Creating user data for: ${user.uid}")
        val userRef = firestore.collection("users").document(user.uid)
        try {
            val document = userRef.get().await()
            if (!document.exists()) {
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "isAdmin" to false,
                    "createdAt" to System.currentTimeMillis()
                )
                userRef.set(userData).await()
                Log.d("AuthRepository", "Created new user data for: ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking/creating user data for ${user.uid}", e)
        }
    }
} 