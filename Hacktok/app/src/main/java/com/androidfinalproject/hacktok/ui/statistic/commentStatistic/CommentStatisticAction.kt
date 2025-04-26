package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

import com.androidfinalproject.hacktok.model.Timeframe

sealed class CommentStatisticsAction {
    object LoadCommentStatistics : CommentStatisticsAction()
    data class SelectTimeframe(val timeframe: Timeframe) : CommentStatisticsAction()
    data class SetDateRange(val startDate: Long, val endDate: Long) : CommentStatisticsAction()
    data class ToggleDataType(val dataType: CommentDataType) : CommentStatisticsAction()
    object RefreshData : CommentStatisticsAction()
    object NavigateBack : CommentStatisticsAction()
}

enum class CommentDataType {
    ALL_COMMENTS {
        override val displayName: String = "All Comments"
        override val description: String = "Show statistics for all comments"
    },
    BANNED_COMMENTS {
        override val displayName: String = "Banned Comments"
        override val description: String = "Show statistics for banned comments only"
    },
    BOTH {
        override val displayName: String = "Both"
        override val description: String = "Show statistics for both normal and banned comments"
    };

    abstract val displayName: String
    abstract val description: String
}