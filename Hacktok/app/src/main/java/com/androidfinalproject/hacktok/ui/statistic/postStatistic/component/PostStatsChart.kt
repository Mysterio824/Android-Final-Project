package com.androidfinalproject.hacktok.ui.statistic.postStatistic.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Timeframe
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostDataType
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatPoint
import kotlin.math.max


@Composable
fun PostStatsChart(
    postStats: List<PostStatPoint>,
    bannedPostStats: List<PostStatPoint>,
    timeframe: Timeframe,
    dataType: PostDataType,
    modifier: Modifier = Modifier
) {
    if ((dataType != PostDataType.BANNED_POSTS && postStats.isEmpty()) ||
        (dataType != PostDataType.ALL_POSTS && bannedPostStats.isEmpty())) {
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
            val chartTitle = when (dataType) {
                PostDataType.ALL_POSTS -> "New Posts Over Time"
                PostDataType.BANNED_POSTS -> "Banned Posts Over Time"
                PostDataType.BOTH -> "Posts Comparison Over Time"
            }

            Text(
                text = chartTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Legend if showing both data types
            if (dataType == PostDataType.BOTH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "All Posts",
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFFF44336), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "Banned Posts",
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(modifier = Modifier.weight(1f)) {
                // Determine max value for Y-axis scaling
                val maxAllPostsValue = if (dataType != PostDataType.BANNED_POSTS) {
                    postStats.maxOfOrNull { it.count }?.toFloat() ?: 0f
                } else 0f

                val maxBannedPostsValue = if (dataType != PostDataType.ALL_POSTS) {
                    bannedPostStats.maxOfOrNull { it.count }?.toFloat() ?: 0f
                } else 0f

                // Use the maximum value for scaling
                val maxValue = if (dataType == PostDataType.BOTH) {
                    maxAllPostsValue
                } else {
                    max(maxAllPostsValue, maxBannedPostsValue)
                }

                val allPostsColor = MaterialTheme.colorScheme.primary
                val bannedPostsColor = Color(0xFFF44336) // Red color for banned posts

                if (maxValue > 0) {
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

                        // Number of data points to display
                        // We'll base our x-coordinates on postStats, but they should be the same length as bannedPostStats
                        val dataSetSize = if (dataType != PostDataType.BANNED_POSTS) postStats.size else bannedPostStats.size

                        if (dataSetSize > 1) {
                            val pointSize = width / (dataSetSize - 1).coerceAtLeast(1)

                            // Draw all posts line if selected
                            if (dataType == PostDataType.ALL_POSTS || dataType == PostDataType.BOTH) {
                                val path = Path()
                                postStats.forEachIndexed { index, point ->
                                    val x = index * pointSize
                                    val y = height - (point.count / maxValue) * height

                                    if (index == 0) {
                                        path.moveTo(x, y)
                                    } else {
                                        path.lineTo(x, y)
                                    }

                                    // Draw data points
                                    drawCircle(
                                        color = allPostsColor,
                                        radius = 4.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                }

                                drawPath(
                                    path = path,
                                    color = allPostsColor,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Draw banned posts line if selected
                            if (dataType == PostDataType.BANNED_POSTS || dataType == PostDataType.BOTH) {
                                val bannedPath = Path()
                                bannedPostStats.forEachIndexed { index, point ->
                                    val x = index * pointSize
                                    // For "Both" mode, scale banned posts for better visibility
                                    val scaleFactor = if (dataType == PostDataType.BOTH) {
                                        // Find what percentage of all posts are banned on average
                                        val avgBannedPct = bannedPostStats.sumOf { it.count }.toFloat() /
                                                postStats.sumOf { it.count }.toFloat()
                                        // Use this to scale up the banned posts line (e.g. if 5% are banned, scale by 10-20x)
                                        val scale = (1f / avgBannedPct) * 0.3f
                                        scale.coerceIn(1f, 20f) // Limit scaling factor
                                    } else 1f

                                    // Scale the y-value but preserve actual numbers in tooltips/labels
                                    val adjustedCount = if (dataType == PostDataType.BOTH) {
                                        point.count * scaleFactor
                                    } else point.count.toFloat()

                                    val y = height - (adjustedCount / maxValue) * height

                                    if (index == 0) {
                                        bannedPath.moveTo(x, y)
                                    } else {
                                        bannedPath.lineTo(x, y)
                                    }

                                    // Draw data points
                                    drawCircle(
                                        color = bannedPostsColor,
                                        radius = 4.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                }

                                drawPath(
                                    path = bannedPath,
                                    color = bannedPostsColor,
                                    style = Stroke(width = if (dataType == PostDataType.BOTH) 1.dp.toPx() else 2.dp.toPx())
                                )

                                // If we're in "Both" mode, add a note about scaling
                                if (dataType == PostDataType.BOTH) {
                                    drawContext.canvas.nativeCanvas.drawText(
                                        "* Banned posts scaled for visibility",
                                        width - 180f,
                                        height - 5f,
                                        android.graphics.Paint().apply {
                                            color = android.graphics.Color.GRAY
                                            textSize = 9.sp.toPx()
                                            textAlign = android.graphics.Paint.Align.RIGHT
                                        }
                                    )
                                }
                            }
                        }
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
                    // Get the right dataset based on what's being shown
                    val dataToUse = when {
                        dataType == PostDataType.BANNED_POSTS -> bannedPostStats
                        else -> postStats
                    }

                    // Limit number of labels to avoid overcrowding
                    val labelsToShow = when {
                        dataToUse.size <= 6 -> dataToUse
                        else -> {
                            val step = dataToUse.size / 4
                            listOf(
                                dataToUse.first(),
                                *dataToUse.filterIndexed { index, _ ->
                                    index % step == 0 && index > 0 && index < dataToUse.size - 1
                                }.toTypedArray(),
                                dataToUse.last()
                            )
                        }
                    }

//                    val totalWidth = with(LocalDensity.current) {
//                        (dataToUse.size) * 40.dp.toPx()
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