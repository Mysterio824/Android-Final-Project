package com.androidfinalproject.hacktok.ui.statistic

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardAction
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatisticViewModel : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    fun onAction(action: StatisticsAction) {
        when (action) {
            is StatisticsAction.SelectTab -> changeTab(action.index)
            else -> {}
        }
    }

    private fun changeTab(tabIndex: Int) {
        _state.update { currentState ->
            currentState.copy(selectedTab = tabIndex)
        }
    }
}