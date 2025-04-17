package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.repository.AuthRepository
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
    
    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        Log.d("AuthRepository", "Attempting Firebase sign-in with email: ${email.take(10)}...")
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Log.d("AuthRepository", "Firebase sign-in successful. User: ${it.uid}")
                checkAndCreateUserData(it)
            }
            authResult.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase sign-in with email failed", e)
            null
        }
    }

    override suspend fun createUserWithEmail(email: String, password: String): FirebaseUser? {
        Log.d("AuthRepository", "Attempting to create user with email: ${email.take(10)}...")
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Log.d("AuthRepository", "User created successfully. User: ${it.uid}")
                checkAndCreateUserData(it)
            }
            authResult.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to create user with email", e)
            null
        }
    }
    
    private suspend fun checkAndCreateUserData(user: FirebaseUser) {
        Log.d("AuthRepository", "Checking/Creating user data for: ${user.uid}")
        val userRef = firestore.collection("users").document(user.uid)
        try {
            val document = userRef.get().await()
            if (!document.exists()) {
                val userData = hashMapOf(
                    "active" to true,
                    "bio" to "",
                    "blockedUsers" to arrayListOf<String>(),
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "email" to (user.email ?: ""),
                    "followerCount" to 0,
                    "followers" to arrayListOf<String>(),
                    "following" to arrayListOf<String>(),
                    "followingCount" to 0,
                    "friends" to arrayListOf<String>(),
                    "fullName" to (user.displayName ?: ""),
                    "id" to user.uid,
                    "language" to "en",
                    "lastActive" to com.google.firebase.Timestamp.now(),
                    "privacySettings" to hashMapOf(
                        "allowMessagesFrom" to "everyone",
                        "postVisibility" to "public",
                        "profileVisibility" to "public"
                    ),
                    "profileImage" to null,
                    "role" to "USER",
                    "username" to (user.displayName?.replace(" ", "") ?: "user${System.currentTimeMillis()}"),
                    "videosCount" to 0
                )
                userRef.set(userData).await()
                Log.d("AuthRepository", "Created new user data for: ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking/creating user data for ${user.uid}", e)
        }
    }
} 