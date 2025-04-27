package com.androidfinalproject.hacktok.service.impl

import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.repository.AdRepository
import com.androidfinalproject.hacktok.service.AdService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdServiceImpl @Inject constructor(
    private val adRepository: AdRepository
) : AdService {

    override suspend fun createAd(ad: Ad, durationDays: Int): String {
        return adRepository.createAd(ad, durationDays)
    }

    override suspend fun getRandomEligibleAd(userId: String): Ad? {
        return adRepository.getRandomEligibleAd(userId)
    }

    override suspend fun incrementImpressions(adId: String) {
        adRepository.incrementImpressions(adId)
    }

    override suspend fun incrementClicks(adId: String) {
        adRepository.incrementClicks(adId)
    }

    override suspend fun getUserAds(userId: String): List<Ad> {
        return adRepository.getUserAds(userId)
    }

    override suspend fun updateAd(adId: String, updates: Map<String, Any>) {
        adRepository.updateAd(adId, updates)
    }

    override suspend fun deleteAd(adId: String) {
        adRepository.deleteAd(adId)
    }

    override suspend fun getAd(adId: String): Ad? {
        return adRepository.getAd(adId)
    }
} 