package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.FcmService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
) : AuthService {

    private val TAG = "AuthService"

    override suspend fun getCurrentUser(): User? {
        return try {
            userRepository.getCurrentUser()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user: ${e.message}")
            null
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return try {
            val currentUser = firebaseAuth.currentUser
            currentUser?.uid
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user ID: ${e.message}")
            null
        }
    }

    override fun getCurrentUserIdSync(): String? {
        return try {
            val currentUser = firebaseAuth.currentUser
            currentUser?.uid
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user ID (sync): ${e.message}")
            null
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun logout(): Boolean {
        return try {
            firebaseAuth.signOut()
//            fcmService.removeFcmToken()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out: ${e.message}")
            false
        }
    }
} 