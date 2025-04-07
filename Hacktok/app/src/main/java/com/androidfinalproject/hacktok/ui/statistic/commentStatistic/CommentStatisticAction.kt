package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

sealed class CommentStatisticsAction {
    object RefreshData : CommentStatisticsAction()
    object NavigateBack : CommentStatisticsAction()
}