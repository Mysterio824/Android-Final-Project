package com.androidfinalproject.hacktok.ui.statistic.postStatistic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PostStatisticsScreenRoot (
    viewModel: PostStatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PostStatisticsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is PostStatisticsAction.NavigateBack
                    -> onNavigateBack()

                else -> viewModel.onAction(action)
            }
        }
    )
}