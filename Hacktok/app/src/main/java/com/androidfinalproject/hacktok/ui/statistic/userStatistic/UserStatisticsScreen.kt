package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.statistic.common.StatisticHeader
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.component.DetailedDataCard
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.component.StatisticsSummaryCards
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.component.TimeframeSelector
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.component.UserStatsChart
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun UserStatisticsScreen(
    state: UserStatisticsState,
    onAction: (UserStatisticsAction) -> Unit = {},
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            StatisticHeader(
                name = "User Statistics",
                isLoading = state.isLoading,
                refreshAction = { onAction(UserStatisticsAction.RefreshData) }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatisticsSummaryCards(state)
                }

                item {
                    TimeframeSelector(
                        selectedTimeframe = state.timeframe,
                        onTimeframeSelected = { onAction(UserStatisticsAction.SelectTimeframe(it)) }
                    )
                }

                item {
                    UserStatsChart(
                        userStats = state.userStats,
                        timeframe = state.timeframe,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }

                item {
                    DetailedDataCard(state.userStats)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserStatisticScreenReview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            UserStatisticsScreen(
                state = MockData.createMockUserStatisticsState(),
                onAction = {}
            )
        }
    }
}