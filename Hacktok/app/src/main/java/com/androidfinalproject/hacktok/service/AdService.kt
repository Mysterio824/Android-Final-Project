package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Ad

interface AdService {
    suspend fun createAd(ad: Ad, durationDays: Int): String
    suspend fun getRandomEligibleAd(userId: String): Ad?
    suspend fun incrementImpressions(adId: String)
    suspend fun incrementClicks(adId: String)
    suspend fun getUserAds(userId: String): List<Ad>
    suspend fun updateAd(adId: String, updates: Map<String, Any>)
    suspend fun deleteAd(adId: String)
    suspend fun getAd(adId: String): Ad?
} 