package com.androidfinalproject.hacktok.ui.statistic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StatisticsScreenRoot (
    viewModel: StatisticViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    StatisticsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is StatisticsAction.NavigateBack
                    -> onNavigateBack()

                else -> viewModel.onAction(action)
            }
        }
    )
}