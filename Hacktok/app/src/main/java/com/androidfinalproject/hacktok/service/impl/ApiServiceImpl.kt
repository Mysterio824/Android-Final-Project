package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.service.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiServiceImpl @Inject constructor() : ApiService {
    val TAG = "ApiService"
    private val SERVER_URL = "http://10.0.2.2:3000"
    private val client = OkHttpClient()

    override suspend fun sendChangePasswordRequest(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Response = withContext(Dispatchers.IO) {
        val jsonPayload = JSONObject().apply {
            put("email", email)
            put("currentPassword", oldPassword)
            put("newPassword", newPassword)
        }
        Log.d(TAG, "Sending change password request to local server")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonPayload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$SERVER_URL/change-password")
            .post(requestBody)
            .build()

        client.newCall(request).execute()
    }

    override suspend fun sendNotificationRequest(
        token: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Response {
        val jsonPayload = JSONObject().apply {
            put("token", token)
            put("title", title)
            put("body", body)

            // Add data fields
            val dataJson = JSONObject()
            data.forEach { (key, value) ->
                dataJson.put(key, value)
            }

            put("data", dataJson)
        }

        Log.d(TAG, "Sending notification request to local server")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonPayload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$SERVER_URL/send-notification")
            .post(requestBody)
            .build()

        return client.newCall(request).execute()
    }

    override suspend fun sendSignUpRequest(
        email: String,
        password: String
    ): Response = withContext(Dispatchers.IO) {
        val jsonPayload = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        Log.d(TAG, "Sending change password request to local server")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonPayload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$SERVER_URL/signup")
            .post(requestBody)
            .build()

        client.newCall(request).execute()
    }

    override suspend fun sendResetPassword(email: String): Response = withContext(Dispatchers.IO) {
        val jsonPayload = JSONObject().apply {
            put("email", email)
        }
        Log.d(TAG, "Sending change password request to local server")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonPayload.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$SERVER_URL/reset-password")
            .post(requestBody)
            .build()

        client.newCall(request).execute()
    }
}