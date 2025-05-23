package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserStatisticsScreenRoot (
    viewModel: UserStatisticsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UserStatisticsScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is UserStatisticsAction.NavigateBack
                    -> onNavigateBack()

                else -> viewModel.onAction(action)
            }
        }
    )
}