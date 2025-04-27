package com.androidfinalproject.hacktok.repository

import android.util.Log
import com.androidfinalproject.hacktok.model.Ad
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import java.util.*

@Singleton
class AdRepository @Inject constructor() {
    private val TAG = "AdRepository"
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

    // Get all active ads
    suspend fun getAllActiveAds(): List<Ad> {
        val currentDate = Date()
        val snapshot = collection
            .whereGreaterThan("endDate", currentDate)
            .get()
            .await()
        return snapshot.toObjects(Ad::class.java)
    }

    // Get ads by user ID
    suspend fun getUserAds(userId: String): List<Ad> {
        val snapshot = collection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Ad::class.java)
    }

    // Get a random eligible ad for a user
    suspend fun getRandomEligibleAd(userId: String): Ad? {
        try {
            Log.d(TAG, "Getting random eligible ad for user: $userId")
            val currentDate = Date()
            Log.d(TAG, "Current date: $currentDate")

            // First get all active ads
            val snapshot = collection
                .whereGreaterThan("endDate", currentDate)
                .get()
                .await()
            
            Log.d(TAG, "Query returned ${snapshot.documents.size} documents")
            
            val ads = snapshot.toObjects(Ad::class.java)
            Log.d(TAG, "Converted to ${ads.size} Ad objects")
            
            // Filter out ads from the same user
//            val eligibleAds = ads.filter { it.userId != userId }
            val eligibleAds = ads

            Log.d(TAG, "After filtering same user ads: ${eligibleAds.size} eligible ads")
            
            if (eligibleAds.isEmpty()) {
                Log.d(TAG, "No eligible ads found")
                return null
            }

            val randomAd = eligibleAds.random()
            Log.d(TAG, "Selected random ad with ID: ${randomAd.id}")
            return randomAd
        } catch (e: Exception) {
            Log.e(TAG, "Error message: ${e.message}")
            return null
        }
    }

    // Increment impressions
    suspend fun incrementImpressions(adId: String) {
        try {
            Log.d(TAG, "Incrementing impressions for ad: $adId")
            collection.document(adId).update("impressions", FieldValue.increment(1)).await()
            Log.d(TAG, "Successfully incremented impressions for ad: $adId")
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing impressions for ad: $adId", e)
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
        }
    }

    // Increment clicks
    suspend fun incrementClicks(adId: String) {
        try {
            Log.d(TAG, "Incrementing clicks for ad: $adId")
            collection.document(adId).update("clicks", FieldValue.increment(1)).await()
            Log.d(TAG, "Successfully incremented clicks for ad: $adId")
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing clicks for ad: $adId", e)
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Error stack trace: ${e.stackTraceToString()}")
        }
    }

    // Update ad
    suspend fun updateAd(adId: String, updates: Map<String, Any>) {
        collection.document(adId).update(updates).await()
    }

    // Delete ad
    suspend fun deleteAd(adId: String) {
        collection.document(adId).delete().await()
    }

    // Get ad by ID
    suspend fun getAd(adId: String): Ad? {
        val snapshot = collection.document(adId).get().await()
        return snapshot.toObject(Ad::class.java)
    }

    // Add user to interested list
    suspend fun addInterestedUser(adId: String, userId: String) {
        try {
            Log.d(TAG, "Adding user $userId to interested list for ad $adId")
            collection.document(adId).update(
                "interestedUserIds", 
                FieldValue.arrayUnion(userId)
            ).await()
            Log.d(TAG, "Successfully added user to interested list")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user to interested list", e)
            throw e
        }
    }

    // Remove user from interested list
    suspend fun removeInterestedUser(adId: String, userId: String) {
        try {
            Log.d(TAG, "Removing user $userId from interested list for ad $adId")
            collection.document(adId).update(
                "interestedUserIds", 
                FieldValue.arrayRemove(userId)
            ).await()
            Log.d(TAG, "Successfully removed user from interested list")
        } catch (e: Exception) {
            Log.e(TAG, "Error removing user from interested list", e)
            throw e
        }
    }

    // Get ad with updated interested status
    suspend fun getAdWithInterestedStatus(adId: String): Ad? {
        try {
            Log.d(TAG, "Getting ad $adId with interested status")
            val snapshot = collection.document(adId).get().await()
            val ad = snapshot.toObject(Ad::class.java)
            Log.d(TAG, "Successfully got ad with interested status")
            return ad
        } catch (e: Exception) {
            Log.e(TAG, "Error getting ad with interested status", e)
            return null
        }
    }
}