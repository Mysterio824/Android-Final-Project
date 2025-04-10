package com.androidfinalproject.hacktok.ui.statistic.postStatistic.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostDataType
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatPoint

@SuppressLint("DefaultLocale")
@Composable
fun DetailedDataTable(
    postStats: List<PostStatPoint>,
    bannedPostStats: List<PostStatPoint>,
    dataType: PostDataType
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Detailed Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                if (dataType == PostDataType.ALL_POSTS || dataType == PostDataType.BOTH) {
                    Text(
                        text = "All Posts",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                if (dataType == PostDataType.BANNED_POSTS || dataType == PostDataType.BOTH) {
                    Text(
                        text = if (dataType == PostDataType.BOTH) "Banned Posts" else "Count",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                if (dataType == PostDataType.BOTH) {
                    Text(
                        text = "Banned %",
                        modifier = Modifier.weight(0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Data rows
            val dataToUse = when (dataType) {
                PostDataType.ALL_POSTS -> postStats
                PostDataType.BANNED_POSTS -> bannedPostStats
                PostDataType.BOTH -> postStats // We'll use postStats as the base for combined display
            }

            dataToUse.forEachIndexed { index, stat ->
                val bannedStat = if (dataType == PostDataType.BOTH) {
                    // Find matching banned stat by date/label
                    bannedPostStats.find { it.label == stat.label }
                } else null

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stat.label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (dataType == PostDataType.ALL_POSTS || dataType == PostDataType.BOTH) {
                        Text(
                            text = stat.count.toString(),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }

                    if (dataType == PostDataType.BANNED_POSTS || dataType == PostDataType.BOTH) {
                        val bannedCount = when (dataType) {
                            PostDataType.BANNED_POSTS -> stat.count
                            PostDataType.BOTH -> bannedStat?.count ?: 0
                            else -> 0
                        }

                        Text(
                            text = bannedCount.toString(),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }

                    if (dataType == PostDataType.BOTH) {
                        val bannedCount = bannedStat?.count ?: 0
                        val percentage = if (stat.count > 0) {
                            (bannedCount.toFloat() / stat.count) * 100
                        } else 0f

                        Text(
                            text = String.format("%.1f%%", percentage),
                            modifier = Modifier.weight(0.8f),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (percentage > 5f) Color(0xFFF44336) else Color(0xFF4CAF50),
                            textAlign = TextAlign.End
                        )
                    }
                }

                if (index < dataToUse.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                }
            }

            if (dataToUse.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available for this period",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Helper function that was missing from the provided code
private fun formatPercentChange(percentChange: Float): String {
    return if (percentChange >= 0) {
        "+%.1f%%".format(percentChange)
    } else {
        "%.1f%%".format(percentChange)
    }
}