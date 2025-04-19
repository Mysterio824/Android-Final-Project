package com.androidfinalproject.hacktok.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


object TokenManager {
    private const val TAG = "TokenManager"
    private const val PREF_NAME = "fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_token"
    
    fun saveTokenToPreferences(context: Context, token: String) {
        try {
            getPreferences(context).edit().apply {
                putString(KEY_FCM_TOKEN, token)
                apply()
            }
            Log.d(TAG, "FCM token saved to SharedPreferences")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving FCM token to SharedPreferences", e)
        }
    }

    fun getTokenFromPreferences(context: Context): String? {
        return try {
            val token = getPreferences(context).getString(KEY_FCM_TOKEN, null)
            if (token != null) {
                Log.d(TAG, "FCM token retrieved from SharedPreferences")
            } else {
                Log.d(TAG, "No FCM token found in SharedPreferences")
            }
            token
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving FCM token from SharedPreferences", e)
            null
        }
    }

    fun clearTokenFromPreferences(context: Context) {
        try {
            getPreferences(context).edit().apply {
                remove(KEY_FCM_TOKEN)
                apply()
            }
            Log.d(TAG, "FCM token cleared from SharedPreferences")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing FCM token from SharedPreferences", e)
        }
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
} 