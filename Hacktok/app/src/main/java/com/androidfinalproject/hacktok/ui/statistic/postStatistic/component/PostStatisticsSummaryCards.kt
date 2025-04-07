package com.androidfinalproject.hacktok.ui.statistic.postStatistic.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsState
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.component.formatPercentChange


@Composable
fun PostStatisticsSummaryCards(state: PostStatisticsState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = "Total Posts",
                value = state.totalPosts.toString(),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "New Posts",
                value = state.newPostsInPeriod.toString(),
                subtitle = formatPercentChange(state.postPercentChange),
                isPositiveChange = state.postPercentChange >= 0,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = "Total Banned Posts",
                value = state.totalBannedPosts.toString(),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "New Banned Posts",
                value = state.newBannedPostsInPeriod.toString(),
                subtitle = formatPercentChange(state.bannedPostPercentChange),
                isPositiveChange = state.bannedPostPercentChange < 0, // Negative is good for banned posts
                modifier = Modifier.weight(1f)
            )
        }
    }
}