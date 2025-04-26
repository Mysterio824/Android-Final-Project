package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Ad
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import java.util.*

@Singleton
class AdRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("ads")

    // Create a new ad with duration
    suspend fun createAd(ad: Ad, durationDays: Int): String {
        val adWithDuration = ad.copy(
            createdAt = Date(),
            endDate = Date(System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L))
        )
        val documentRef = collection.add(adWithDuration).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

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

    // Lấy quảng cáo theo userId
    suspend fun getUserAds(userId: String): List<Ad> {
        val snapshot = collection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Ad::class.java)
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