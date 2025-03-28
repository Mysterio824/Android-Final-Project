package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Settings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("settings")

    // Thêm hoặc cập nhật cài đặt
    suspend fun upsertSettings(settings: Settings) {
        if (settings.id == null) {
            val documentRef = collection.add(settings).await()
            collection.document(documentRef.id).update("id", documentRef.id).await()
        } else {
            collection.document(settings.id).set(settings).await()
        }
    }

    // Lấy cài đặt theo ID
    suspend fun getSettings(settingsId: String): Settings? {
        val snapshot = collection.document(settingsId).get().await()
        return snapshot.toObject(Settings::class.java)
    }
}