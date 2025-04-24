package com.androidfinalproject.hacktok.ui.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidfinalproject.hacktok.ui.statistic.commentStatistic.CommentStatisticsScreenRoot
import com.androidfinalproject.hacktok.ui.statistic.commentStatistic.CommentStatisticsViewModel
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsScreenRoot
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsViewModel
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.UserStatisticsScreenRoot
import com.androidfinalproject.hacktok.ui.statistic.userStatistic.UserStatisticsViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun StatisticsScreen(
    state: StatisticsState,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        StatisticsTabItem("Users", Icons.Filled.Person),
        StatisticsTabItem("Posts", Icons.AutoMirrored.Filled.Article),
        StatisticsTabItem("Comments", Icons.AutoMirrored.Filled.Comment)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = state.selectedTab == index,
                        onClick = { onAction(StatisticsAction.SelectTab(index)) }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            when (state.selectedTab) {
                0 ->
                    UserStatisticsScreenRoot(
                        onNavigateBack = { onAction(StatisticsAction.NavigateBack) }
                    )

                1 ->
                    PostStatisticsScreenRoot(
                    viewModel = PostStatisticsViewModel(),
                    onNavigateBack = { onAction(StatisticsAction.NavigateBack) }
                )

                2 ->
                    CommentStatisticsScreenRoot(
                    viewModel = CommentStatisticsViewModel(),
                    onNavigateBack = { onAction(StatisticsAction.NavigateBack) }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            StatisticsScreen(
                state = StatisticsState(selectedTab = 0),
                onAction = {},
                modifier = Modifier
            )
        }
    }
}