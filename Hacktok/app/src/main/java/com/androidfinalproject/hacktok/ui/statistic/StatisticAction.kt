package com.androidfinalproject.hacktok.ui.statistic

sealed class StatisticsAction {
    data class SelectTab(val index: Int) : StatisticsAction()
    object NavigateBack : StatisticsAction()
}