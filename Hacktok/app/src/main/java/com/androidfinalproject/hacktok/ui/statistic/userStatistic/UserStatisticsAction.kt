package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import com.androidfinalproject.hacktok.model.Timeframe

sealed class UserStatisticsAction {
    object LoadUserStatistics : UserStatisticsAction()
    data class SelectTimeframe(val timeframe: Timeframe) : UserStatisticsAction()
    data class SetDateRange(val startDate: Long, val endDate: Long) : UserStatisticsAction()
    object RefreshData : UserStatisticsAction()
    object NavigateBack: UserStatisticsAction()
}