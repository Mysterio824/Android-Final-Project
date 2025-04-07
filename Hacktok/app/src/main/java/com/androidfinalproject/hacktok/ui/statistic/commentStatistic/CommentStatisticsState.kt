package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

data class CommentStatisticsState (
    val dailyComments: Int = 0,
    val monthlyComments: Int = 0,
    val yearlyComments: Int = 0,
    val bannedComments: Int = 0,
    val isLoading: Boolean = false,
)