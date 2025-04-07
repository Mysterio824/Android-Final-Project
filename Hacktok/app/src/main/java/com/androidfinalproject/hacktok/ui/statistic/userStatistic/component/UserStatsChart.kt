package com.androidfinalproject.hacktok.ui.statistic.userStatistic.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.Timeframe
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.UserStatPoint

@Composable
fun UserStatsChart(
    userStats: List<UserStatPoint>,
    timeframe: Timeframe,
    modifier: Modifier = Modifier
) {
    if (userStats.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("No data available")
        }
        return
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "New Users Over Time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                val maxValue = userStats.maxOfOrNull { it.count }?.toFloat() ?: 0f
                val chartColor = MaterialTheme.colorScheme.primary

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 40.dp.toPx()
                    val verticalStep = height / 5

                    // Draw y-axis grid lines
                    for (i in 0..5) {
                        val y = height - i * verticalStep
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )

                        // Draw y-axis labels
                        if (maxValue > 0) {
                            val value = (maxValue * i / 5).toInt()
                            drawContext.canvas.nativeCanvas.drawText(
                                value.toString(),
                                5f,
                                y - 5f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 10.sp.toPx()
                                }
                            )
                        }
                    }

                    if (userStats.size > 1 && maxValue > 0) {
                        val pointSize = width / (userStats.size - 1).coerceAtLeast(1)

                        // Draw line chart
                        val path = Path()
                        userStats.forEachIndexed { index, point ->
                            val x = index * pointSize
                            val y = height - (point.count / maxValue) * height

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }

                            // Draw data points
                            drawCircle(
                                color = chartColor,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            color = chartColor,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // X-axis labels at bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Limit number of labels to avoid overcrowding
                    val labelsToShow = when {
                        userStats.size <= 6 -> userStats
                        else -> {
                            val step = userStats.size / 4
                            listOf(
                                userStats.first(),
                                *userStats.filterIndexed { index, _ -> index % step == 0 && index > 0 && index < userStats.size - 1 }.toTypedArray(),
                                userStats.last()
                            )
                        }
                    }

//                    val totalWidth = with(LocalDensity.current) {
//                        (userStats.size) * 40.dp.toPx()
//                    }
//                    val step = if (totalWidth > 0) size.width / totalWidth else 0f

                    labelsToShow.forEachIndexed { _, point ->
//                        val originalIndex = userStats.indexOf(point)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = point.label,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}