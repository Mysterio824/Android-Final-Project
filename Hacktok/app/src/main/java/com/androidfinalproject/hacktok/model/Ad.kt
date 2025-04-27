package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Ad(
    @PropertyName("id") val id: String? = null,
    @PropertyName("advertiserId") val advertiserId: String,
    @PropertyName("userId") val userId: String,
    @PropertyName("content") val content: String,
    @PropertyName("mediaUrl") val mediaUrl: String,
    @PropertyName("targetAudience") val targetAudience: TargetAudience,
    @PropertyName("impressions") val impressions: Int = 0,
    @PropertyName("clicks") val clicks: Int = 0,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("endDate") val endDate: Date = Date(),
    @PropertyName("interestedUserIds") val interestedUserIds: List<String> = emptyList()
) {
    constructor() : this(null, "", "", "", "", TargetAudience(), 0, 0, Date(), Date(), emptyList())

    override fun toString(): String {
        return "Ad(id=$id, advertiserId='$advertiserId', userId='$userId', content='$content', " +
                "impressions=$impressions, createdAt=$createdAt, endDate=$endDate)"
    }

    fun getInterestedCount(): Int = interestedUserIds.size

    fun isInterested(userId: String): Boolean = interestedUserIds.contains(userId)
}