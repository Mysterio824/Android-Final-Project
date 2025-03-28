package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Stats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class StatsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("stats")

    // Thêm hoặc cập nhật thống kê
    suspend fun upsertStats(stats: Stats) {
        if (stats.id == null) {
            val documentRef = collection.add(stats).await()
            collection.document(documentRef.id).update("id", documentRef.id).await()
        } else {
            collection.document(stats.id).set(stats).await()
        }
    }

    // Lấy thống kê theo ID
    suspend fun getStats(statsId: String): Stats? {
        val snapshot = collection.document(statsId).get().await()
        return snapshot.toObject(Stats::class.java)
    }

    // Lấy thống kê theo ngày
    suspend fun getStatsByDate(date: String): Stats? {
        val snapshot = collection.document(date).get().await()
        return snapshot.toObject(Stats::class.java)
    }
}