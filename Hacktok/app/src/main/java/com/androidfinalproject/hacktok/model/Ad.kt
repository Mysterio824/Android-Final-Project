package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Ad(
    @PropertyName("id") val id: String? = null,
    @PropertyName("advertiserId") val advertiserId: String,
    @PropertyName("content") val content: String,
    @PropertyName("mediaUrl") val mediaUrl: String,
    @PropertyName("targetAudience") val targetAudience: TargetAudience,
    @PropertyName("impressions") val impressions: Int = 0,
    @PropertyName("clicks") val clicks: Int = 0,
    @PropertyName("createdAt") val createdAt: Date = Date()
) {
    constructor() : this(null, "", "", "", TargetAudience(), 0, 0, Date())

    override fun toString(): String {
        return "Ad(id=$id, advertiserId='$advertiserId', content='$content', " +
                "impressions=$impressions, createdAt=$createdAt)"
    }
}

data class TargetAudience(
    @PropertyName("ageRange") val ageRange: List<Int> = listOf(18, 65),
    @PropertyName("location") val location: String? = null
) {
    constructor() : this(listOf(18, 65), null)
}