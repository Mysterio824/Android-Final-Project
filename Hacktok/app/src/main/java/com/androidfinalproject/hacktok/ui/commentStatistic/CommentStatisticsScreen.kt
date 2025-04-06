package com.androidfinalproject.hacktok.ui.commentStatistic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.commentStatistic.component.StatisticCard
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun CommentStatisticsScreen(
    state: CommentStatisticsState,
    onRefresh: () -> Unit,
) {
    // Collect state from ViewModel
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Comments Management Statistics",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Statistic Cards
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
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Statistics")
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
                onRefresh = {},
            )
        }
    }
}