package com.androidfinalproject.hacktok.ui.statistic

import androidx.compose.ui.graphics.vector.ImageVector

data class StatisticsState(
    val selectedTab: Int = 0
)

data class StatisticsTabItem(val label: String, val icon: ImageVector)
