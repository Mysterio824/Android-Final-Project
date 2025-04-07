package com.androidfinalproject.hacktok.ui.statistic.postStatistic

import java.util.Date

data class PostStatisticsState(
    val timeframe: Timeframe = Timeframe.MONTH,
    val dataType: PostDataType = PostDataType.BOTH,
    val postStats: List<PostStatPoint> = emptyList(),
    val bannedPostStats: List<PostStatPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: Date = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), // Default 30 days back
    val endDate: Date = Date(),
    val totalPosts: Int = 0,
    val totalBannedPosts: Int = 0,
    val newPostsInPeriod: Int = 0,
    val newBannedPostsInPeriod: Int = 0,
    val postPercentChange: Float = 0f,
    val bannedPostPercentChange: Float = 0f
)

data class PostStatPoint(
    val label: String,
    val count: Int,
    val date: Date
)