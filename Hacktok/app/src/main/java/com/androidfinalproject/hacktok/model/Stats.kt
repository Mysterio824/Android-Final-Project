package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Stats(
    @PropertyName("id") val id: String? = null,
    @PropertyName("activeUsers") val activeUsers: Int = 0,
    @PropertyName("newUsers") val newUsers: Int = 0,
    @PropertyName("totalPosts") val totalPosts: Int = 0,
    @PropertyName("totalComments") val totalComments: Int = 0,
    @PropertyName("timestamp") val timestamp: Date = Date(),
    @PropertyName("totalReports") val totalReports: Int = 0,
    @PropertyName("totalMessages") val totalMessages: Int = 0,
    @PropertyName("avgSessionTime") val avgSessionTime: Double = 0.0
) {
    constructor() : this(null, 0, 0, 0, 0, Date(), 0, 0, 0.0)

    override fun toString(): String {
        return "Stats(id=$id, activeUsers=$activeUsers, newUsers=$newUsers, " +
                "totalPosts=$totalPosts, timestamp=$timestamp)"
    }
}