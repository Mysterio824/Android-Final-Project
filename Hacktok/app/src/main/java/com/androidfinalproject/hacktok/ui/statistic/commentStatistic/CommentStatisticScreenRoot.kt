package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsAction
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsScreen
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostStatisticsViewModel

@Composable
fun CommentStatisticsScreenRoot (
    viewModel: CommentStatisticsViewModel= hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CommentStatisticsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is CommentStatisticsAction.NavigateBack
                    -> onNavigateBack()

                else -> viewModel.onAction(action)
            }
        }
    )
}