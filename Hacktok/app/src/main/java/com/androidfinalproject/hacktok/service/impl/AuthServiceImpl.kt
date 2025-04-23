package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.ApiService
import com.androidfinalproject.hacktok.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val apiService: ApiService
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
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out: ${e.message}")
            false
        }
    }

    override suspend fun changePassword(oldPass: String, newPassword: String): String {
        val user = firebaseAuth.currentUser
        val email = user?.email ?: return "User not found"

        return try {
            // Send the request and handle the response
            val response = apiService.sendChangePasswordRequest(email, oldPass, newPassword)

            // Check if the response is successful
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return jsonObject.optString("message", "Password changed successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON response: ${e.message}")
                        return "Password changed successfully"
                    }
                }
                return "Password changed successfully"
            } else {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return "Failed to change password: ${jsonObject.optString("error", "Unknown error")}"
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON error response: ${e.message}")
                        return "Failed to change password: ${response.code}"
                    }
                }
                return "Failed to change password: ${response.code}"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error changing password: ${e.message}")
            return "Error: ${e.message}"
        }
    }
}