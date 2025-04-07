package com.androidfinalproject.hacktok.ui.statistic.postStatistic

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
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.component.DataTypeSelector
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.component.DetailedDataTable
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.component.PostStatisticsSummaryCards
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.component.PostStatsChart
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.component.TimeframeSelector
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun PostStatisticsScreen(
    state: PostStatisticsState,
    onAction: (PostStatisticsAction) -> Unit
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            StatisticHeader(
                name = "Post Statistics",
                isLoading = state.isLoading,
                refreshAction = { onAction(PostStatisticsAction.RefreshData) }
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
                    PostStatisticsSummaryCards(state)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TimeframeSelector(
                            selectedTimeframe = state.timeframe,
                            onTimeframeSelected = { onAction(PostStatisticsAction.SelectTimeframe(it)) },
                            modifier = Modifier.weight(1f)
                        )

                        DataTypeSelector(
                            selectedDataType = state.dataType,
                            onDataTypeSelected = { onAction(PostStatisticsAction.ToggleDataType(it)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    PostStatsChart(
                        postStats = state.postStats,
                        bannedPostStats = state.bannedPostStats,
                        timeframe = state.timeframe,
                        dataType = state.dataType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }

                item {
                    DetailedDataTable(
                        postStats = state.postStats,
                        bannedPostStats = state.bannedPostStats,
                        dataType = state.dataType
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PostStatisticsScreenPreview() {
    val mockState = MockData.createMockPostStatisticsState()

    MainAppTheme {
        PostStatisticsScreen(
            state = mockState,
            onAction = {}
        )
    }
}