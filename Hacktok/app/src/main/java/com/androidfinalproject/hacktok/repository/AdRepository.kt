package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Ad
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("ads")

    // Thêm quảng cáo mới
    suspend fun addAd(ad: Ad): String {
        val documentRef = collection.add(ad).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy quảng cáo theo ID
    suspend fun getAd(adId: String): Ad? {
        val snapshot = collection.document(adId).get().await()
        return snapshot.toObject(Ad::class.java)
    }

    // Tăng lượt hiển thị
    suspend fun incrementImpressions(adId: String) {
        collection.document(adId).update("impressions", FieldValue.increment(1)).await()
    }

    // Tăng lượt nhấp
    suspend fun incrementClicks(adId: String) {
        collection.document(adId).update("clicks", FieldValue.increment(1)).await()
    }

    // Cập nhật quảng cáo
    suspend fun updateAd(adId: String, updates: Map<String, Any>) {
        collection.document(adId).update(updates).await()
    }

    // Xóa quảng cáo
    suspend fun deleteAd(adId: String) {
        collection.document(adId).delete().await()
    }
}