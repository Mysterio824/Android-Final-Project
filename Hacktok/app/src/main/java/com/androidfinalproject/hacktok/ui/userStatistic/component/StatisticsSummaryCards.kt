package com.androidfinalproject.hacktok.ui.userStatistic.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.annotation.SuppressLint
import kotlin.math.abs
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.userStatistic.UserStatisticsState

@Composable
fun StatisticsSummaryCards(
    state: UserStatisticsState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            title = "Total Users",
            value = state.totalUsers.toString(),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "New Users",
            value = state.newUsersInPeriod.toString(),
            subtitle = formatPercentChange(state.percentChange),
            isPositiveChange = state.percentChange >= 0,
            modifier = Modifier.weight(1f)
        )
    }
}

@SuppressLint("DefaultLocale")
fun formatPercentChange(percentChange: Float): String {
    val formattedValue = String.format("%.1f", abs(percentChange))
    return if (percentChange >= 0) {
        "+$formattedValue%"
    } else {
        "-$formattedValue%"
    }
}