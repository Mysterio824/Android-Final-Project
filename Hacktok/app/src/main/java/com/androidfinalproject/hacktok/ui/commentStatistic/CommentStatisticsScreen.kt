package com.androidfinalproject.hacktok.ui.commentStatistic

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.commentStatistic.component.StatisticCard

data class CommentStatistics(
    val dailyComments: Int,
    val monthlyComments: Int,
    val yearlyComments: Int,
    val bannedComments: Int
)

@Composable
fun CommentStatisticsScreen(
    viewModel: CommentStatisticsViewModel = viewModel()
) {
    // Collect state from ViewModel
    val statistics by viewModel.statistics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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

        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Statistic Cards
            StatisticCard(title = "Comments Today", count = statistics.dailyComments)
            Spacer(modifier = Modifier.height(8.dp))

            StatisticCard(title = "Comments This Month", count = statistics.monthlyComments)
            Spacer(modifier = Modifier.height(8.dp))

            StatisticCard(title = "Comments This Year", count = statistics.yearlyComments)
            Spacer(modifier = Modifier.height(8.dp))

            StatisticCard(
                title = "Banned Comments",
                count = statistics.bannedComments,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.refreshStatistics() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Statistics")
            }
        }
    }
}