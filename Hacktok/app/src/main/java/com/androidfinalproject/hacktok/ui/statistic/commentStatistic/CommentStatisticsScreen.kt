package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.statistic.commentStatistic.component.StatisticCard
import com.androidfinalproject.hacktok.ui.statistic.common.StatisticHeader
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun CommentStatisticsScreen(
    state: CommentStatisticsState,
    onAction: (CommentStatisticsAction) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            StatisticHeader(
                name = "Comments Statistics",
                isLoading = state.isLoading,
                refreshAction = { onAction(CommentStatisticsAction.RefreshData) }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                StatisticCard(title = "Comments Today", count = state.dailyComments)
                Spacer(modifier = Modifier.height(8.dp))

                StatisticCard(title = "Comments This Month", count = state.monthlyComments)
                Spacer(modifier = Modifier.height(8.dp))

                StatisticCard(title = "Comments This Year", count = state.yearlyComments)
                Spacer(modifier = Modifier.height(8.dp))

                StatisticCard(
                    title = "Banned Comments",
                    count = state.bannedComments,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onAction(CommentStatisticsAction.RefreshData) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Refresh Statistics")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CommentStatisticScreenPreview() {
    val mockState = CommentStatisticsState(
        isLoading = false,
        dailyComments = 12,
        monthlyComments = 320,
        yearlyComments = 4021,
        bannedComments = 18
    )
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            CommentStatisticsScreen(
                state = mockState,
                onAction = {},
            )
        }
    }
}