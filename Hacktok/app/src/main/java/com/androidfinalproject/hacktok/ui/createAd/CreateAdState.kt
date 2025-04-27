package com.androidfinalproject.hacktok.ui.createAd

import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.model.TargetAudience
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.AdType
import java.util.Date

data class CreateAdState(
    val adContent: String = "",
    val mediaUrl: String = "",
    val url: String = "",
    val adType: AdType = AdType.SPONSORED_POST,
    val durationDays: Int = 7,
    val targetAudience: TargetAudience = TargetAudience(),
    val currentUser: User? = null,
    val isSubmitting: Boolean = false,
    val endDate: Date = Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)), // Default 7 days
    val error: String? = null,
    val isSuccess: Boolean = false,
    val userAds: List<Ad> = emptyList(),
    val isLoadingAds: Boolean = false,
    val isDeletingAd: Boolean = false,
    val isLoading: Boolean = false
)