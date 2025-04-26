package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

import com.androidfinalproject.hacktok.model.Timeframe
import java.util.Date

data class CommentStatisticsState(
    val timeframe: Timeframe = Timeframe.MONTH,
    val dataType: CommentDataType = CommentDataType.BOTH,
    val commentStats: List<CommentStatPoint> = emptyList(),
    val bannedCommentStats: List<CommentStatPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val startDate: Date = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), // Default 30 days back
    val endDate: Date = Date(),
    val totalComments: Int = 0,
    val totalBannedComments: Int = 0,
    val newCommentsInPeriod: Int = 0,
    val newBannedCommentsInPeriod: Int = 0,
    val commentPercentChange: Float = 0f,
    val bannedCommentPercentChange: Float = 0f,
    val dailyComments: Int = 0,
    val monthlyComments: Int = 0,
    val yearlyComments: Int = 0,
    val bannedComments: Int = 0
) {
    val displayStats: List<CommentStatPoint>
        get() = when (dataType) {
            CommentDataType.ALL_COMMENTS -> commentStats
            CommentDataType.BANNED_COMMENTS -> bannedCommentStats
            CommentDataType.BOTH -> commentStats + bannedCommentStats
        }

    val displayNewCount: Int
        get() = when (dataType) {
            CommentDataType.ALL_COMMENTS -> newCommentsInPeriod
            CommentDataType.BANNED_COMMENTS -> newBannedCommentsInPeriod
            CommentDataType.BOTH -> newCommentsInPeriod + newBannedCommentsInPeriod
        }

    val displayPercentChange: Float
        get() = when (dataType) {
            CommentDataType.ALL_COMMENTS -> commentPercentChange
            CommentDataType.BANNED_COMMENTS -> bannedCommentPercentChange
            CommentDataType.BOTH -> (commentPercentChange + bannedCommentPercentChange) / 2
        }

    val timeframeLabel: String
        get() = when (timeframe) {
            Timeframe.DAY -> "Daily"
            Timeframe.MONTH -> "Monthly"
            Timeframe.YEAR -> "Yearly"
        }
}

data class CommentStatPoint(
    val label: String,
    val count: Int,
    val date: Date
)