package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.User
import com.google.firebase.auth.FirebaseUser

/**
 * Service to handle authentication and user operations
 */
interface AuthService {
    // Get the current authenticated user
    suspend fun getCurrentUser(): User?
    
    // Get the ID of the current authenticated user
    suspend fun getCurrentUserId(): String?
    
    // Non-suspending version to get current user ID (for use in Flow contexts)
    fun getCurrentUserIdSync(): String?
    
    // Check if the user is authenticated
    suspend fun isAuthenticated(): Boolean
    
    // Logout the current user
    suspend fun logout(): Boolean

    suspend fun changePassword(oldPass: String, newPassword: String): String
    suspend fun signUp(email: String, password: String): String
    suspend fun resetPassword(email: String): String
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser?
    suspend fun isUserAdmin(userId: String): Boolean
} 