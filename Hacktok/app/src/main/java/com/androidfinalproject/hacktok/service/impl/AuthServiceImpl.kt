package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.AuthRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.ApiService
import com.androidfinalproject.hacktok.service.AuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
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

    override suspend fun resetPassword(email: String): String {
        val user = userRepository.getUserByEmail(email)
            ?: return "Email has not been registered!"

        return try {
            // Send the request and handle the response
            val response = apiService.sendResetPassword(email)

            // Check if the response is successful
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return jsonObject.optString("message", "Send request successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON response: ${e.message}")
                        return "Send request successfully"
                    }
                }
                return "Send request successfully"
            } else {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return "Failed to send request: ${jsonObject.optString("error", "Unknown error")}"
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON error response: ${e.message}")
                        return "Failed to send request: ${response.code}"
                    }
                }
                return "Failed to send request: ${response.code}"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error sending request: ${e.message}")
            return "Error: ${e.message}"
        }
    }

    override suspend fun signUp(email: String, password: String): String {
        return try {
            val response = apiService.sendSignUpRequest(email, password)
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return jsonObject.optString("message", "Verification code sent to your email")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON response: ${e.message}")
                        return "Verification code sent to your email"
                    }
                }
                return "Verification code sent to your email"
            } else {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return "Failed to signup: ${jsonObject.optString("error", "Unknown error")}"
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON error response: ${e.message}")
                        return "Failed to signup: ${response.code}"
                    }
                }
                return "Failed to signup: ${response.code}"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error creating new account: ${e.message}")
            return "Error: ${e.message}"
        }
    }

    override suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        return authRepository.signInWithGoogle(idToken)
    }

    override suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        return authRepository.signInWithEmail(email, password)
    }

    override suspend fun isUserAdmin(userId: String): Boolean {
        val user = userRepository.getCurrentUser()
            ?: return false
        return user.role == UserRole.ADMIN
    }

    override suspend fun verifyCode(email: String, code: String): Boolean {
        return try {
            val response = apiService.verifyCode(email, code)
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        val success = jsonObject.optBoolean("verified", false)
                        return success
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON response: ${e.message}")
                    }
                }
                return true // Assume success if we can't parse the response
            }
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying code: ${e.message}")
            return false
        }
    }
    
    override suspend fun resendVerificationCode(email: String): String {
        return try {
            val response = apiService.resendVerificationCode(email)
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val jsonObject = JSONObject(responseBody)
                        return jsonObject.optString("message", "Verification code resent")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing JSON response: ${e.message}")
                    }
                }
                return "Verification code resent"
            }
            return "Failed to resend verification code"
        } catch (e: Exception) {
            Log.e(TAG, "Error resending verification code: ${e.message}")
            return "Error: ${e.message}"
        }
    }
    
    override suspend fun setUsername(email: String, username: String): Boolean {
        return try {
            val response = apiService.setUsername(email, username)
            
            if (response.isSuccessful) {
                // After successful username set, sign in with the credentials
                authRepository.signInWithEmail(email, "")  // Server should allow sign in once user is verified
                return true
            }
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error setting username: ${e.message}")
            return false
        }
    }

    override fun checkIfUserLoginGoogle(): Boolean {
        val currentUser = firebaseAuth.currentUser ?: return false
        
        for (profile in currentUser.providerData) {
            if (profile.providerId == GoogleAuthProvider.PROVIDER_ID) {
                return true
            }
        }
        
        return false
    }
}