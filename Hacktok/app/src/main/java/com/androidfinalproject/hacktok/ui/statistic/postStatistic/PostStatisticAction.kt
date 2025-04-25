package com.androidfinalproject.hacktok.ui.statistic.postStatistic

import com.androidfinalproject.hacktok.model.Timeframe

sealed class PostStatisticsAction {
    object LoadPostStatistics : PostStatisticsAction()
    data class SelectTimeframe(val timeframe: Timeframe) : PostStatisticsAction()
    data class SetDateRange(val startDate: Long, val endDate: Long) : PostStatisticsAction()
    data class ToggleDataType(val dataType: PostDataType) : PostStatisticsAction()
    object RefreshData : PostStatisticsAction()
    object NavigateBack : PostStatisticsAction()
}

enum class PostDataType {
    ALL_POSTS {
        override val displayName: String = "All Posts"
        override val description: String = "Show statistics for all posts"
    },
    BANNED_POSTS {
        override val displayName: String = "Banned Posts"
        override val description: String = "Show statistics for banned posts only"
    },
    BOTH {
        override val displayName: String = "Both"
        override val description: String = "Show statistics for both normal and banned posts"
    };

    abstract val displayName: String
    abstract val description: String
}