package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import java.util.Date

data class UserStatisticsState(
    val timeframe: Timeframe = Timeframe.MONTH,
    val userStats: List<UserStatPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: Date = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
    val endDate: Date = Date(),
    val totalUsers: Int = 0,
    val newUsersInPeriod: Int = 0,
    val percentChange: Float = 0f
)

data class UserStatPoint(
    val label: String,
    val count: Int,
    val date: Date
)