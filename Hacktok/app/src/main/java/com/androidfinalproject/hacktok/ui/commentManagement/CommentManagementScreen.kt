package com.androidfinalproject.hacktok.ui.commentManagement.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CommentStatistics(
    val dailyComments: Int,
    val monthlyComments: Int,
    val yearlyComments: Int,
    val bannedComments: Int
)

@Composable
fun CommentManagementScreen (
    statistics: CommentStatistics
) {
    Column (
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

        // Statistic Cards
        StatisticCard(title = "Comments Today", count = statistics.dailyComments)
        StatisticCard(title = "Comments This Month", count = statistics.monthlyComments)
        StatisticCard(title = "Comments This Year", count = statistics.yearlyComments)
        StatisticCard(title = "Banned Comments", count = statistics.bannedComments, color = Color.Red)
    }
}