package com.androidfinalproject.hacktok.ui.statistic.postStatistic

import com.androidfinalproject.hacktok.model.Timeframe

sealed class PostStatisticsAction {
    object LoadPostStatistics : PostStatisticsAction()
    data class SelectTimeframe(val timeframe: Timeframe) : PostStatisticsAction()
    data class SetDateRange(val startDate: Long, val endDate: Long) : PostStatisticsAction()
    object RefreshData : PostStatisticsAction()
    data class ToggleDataType(val dataType: PostDataType) : PostStatisticsAction()
    object NavigateBack : PostStatisticsAction()
}

enum class PostDataType {
    ALL_POSTS, BANNED_POSTS, BOTH
}