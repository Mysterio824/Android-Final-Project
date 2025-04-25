package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

enum class Timeframe {
    DAY, MONTH, YEAR
}

// Base StatPoint dùng cho tất cả loại thống kê
data class StatPoint(
    val label: String,
    val count: Int,
    val date: Date
)

data class Stats(
    @PropertyName("id") val id: String? = null,

    // Người dùng
    @PropertyName("activeUsers") val activeUsers: Int = 0,
    @PropertyName("newUsers") val newUsers: Int = 0,
    @PropertyName("totalUsers") val totalUsers: Int = 0,
    @PropertyName("newUsersInPeriod") val newUsersInPeriod: Int = 0,
    @PropertyName("userPercentChange") val userPercentChange: Float = 0f,

    // Bài viết
    @PropertyName("totalPosts") val totalPosts: Int = 0,
    @PropertyName("newPostsInPeriod") val newPostsInPeriod: Int = 0,
    @PropertyName("totalBannedPosts") val totalBannedPosts: Int = 0,
    @PropertyName("newBannedPostsInPeriod") val newBannedPostsInPeriod: Int = 0,
    @PropertyName("postPercentChange") val postPercentChange: Float = 0f,
    @PropertyName("bannedPostPercentChange") val bannedPostPercentChange: Float = 0f,

    // Bình luận
    @PropertyName("totalComments") val totalComments: Int = 0,
    @PropertyName("newCommentsInPeriod") val newCommentsInPeriod: Int = 0,
    @PropertyName("totalBannedComments") val totalBannedComments: Int = 0,
    @PropertyName("newBannedCommentsInPeriod") val newBannedCommentsInPeriod: Int = 0,
    @PropertyName("commentPercentChange") val commentPercentChange: Float = 0f,
    @PropertyName("bannedCommentPercentChange") val bannedCommentPercentChange: Float = 0f,

    // Khác
    @PropertyName("totalReports") val totalReports: Int = 0,
    @PropertyName("totalMessages") val totalMessages: Int = 0,
    @PropertyName("avgSessionTime") val avgSessionTime: Double = 0.0,
    @PropertyName("timestamp") val timestamp: Date = Date(),

    // Thống kê theo thời gian
    @PropertyName("dailyCount") val dailyCount: Int = 0,
    @PropertyName("monthlyCount") val monthlyCount: Int = 0,
    @PropertyName("yearlyCount") val yearlyCount: Int = 0,

    // Danh sách điểm thống kê
    @PropertyName("userStats") val userStats: List<StatPoint> = emptyList(),
    @PropertyName("postStats") val postStats: List<StatPoint> = emptyList(),
    @PropertyName("bannedPostStats") val bannedPostStats: List<StatPoint> = emptyList(),
    @PropertyName("commentStats") val commentStats: List<StatPoint> = emptyList(),
    @PropertyName("bannedCommentStats") val bannedCommentStats: List<StatPoint> = emptyList()
) {
    constructor() : this(
        id = null,
        activeUsers = 0,
        newUsers = 0,
        totalUsers = 0,
        newUsersInPeriod = 0,
        userPercentChange = 0f,
        totalPosts = 0,
        newPostsInPeriod = 0,
        totalBannedPosts = 0,
        newBannedPostsInPeriod = 0,
        postPercentChange = 0f,
        bannedPostPercentChange = 0f,
        totalComments = 0,
        newCommentsInPeriod = 0,
        totalBannedComments = 0,
        newBannedCommentsInPeriod = 0,
        commentPercentChange = 0f,
        bannedCommentPercentChange = 0f,
        totalReports = 0,
        totalMessages = 0,
        avgSessionTime = 0.0,
        timestamp = Date(),
        dailyCount = 0,
        monthlyCount = 0,
        yearlyCount = 0,
        userStats = emptyList(),
        postStats = emptyList(),
        bannedPostStats = emptyList(),
        commentStats = emptyList(),
        bannedCommentStats = emptyList()
    )

    override fun toString(): String {
        return "Stats(id=$id, activeUsers=$activeUsers, newUsers=$newUsers, totalPosts=$totalPosts, timestamp=$timestamp)"
    }
}
