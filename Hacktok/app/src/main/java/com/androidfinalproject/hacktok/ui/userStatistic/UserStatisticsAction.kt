package com.androidfinalproject.hacktok.ui.userStatistic

sealed class UserStatisticsAction {
    object LoadUserStatistics : UserStatisticsAction()
    data class SelectTimeframe(val timeframe: Timeframe) : UserStatisticsAction()
    data class SetDateRange(val startDate: Long, val endDate: Long) : UserStatisticsAction()
    object RefreshData : UserStatisticsAction()
    object NavigateBack: UserStatisticsAction()
}

enum class Timeframe {
    DAY, MONTH, YEAR
}