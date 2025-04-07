package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserStatisticsViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserStatisticsState())
    val state: StateFlow<UserStatisticsState> = _state.asStateFlow()

    init {
        loadUserStatistics()
    }

    fun onAction(action: UserStatisticsAction) {
        when (action) {
            is UserStatisticsAction.LoadUserStatistics -> loadUserStatistics()
            is UserStatisticsAction.SelectTimeframe -> updateTimeframe(action.timeframe)
            is UserStatisticsAction.SetDateRange -> updateDateRange(action.startDate, action.endDate)
            is UserStatisticsAction.RefreshData -> refreshData()
            else -> {}
        }
    }

    private fun loadUserStatistics() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(1000)

                val mockData = MockData.createMockUserStatisticsState()

                _state.update { it.copy(
                    isLoading = false,
                    userStats = mockData.userStats,
                    totalUsers = mockData.totalUsers,
                    newUsersInPeriod = mockData.newUsersInPeriod,
                    percentChange = mockData.percentChange
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateTimeframe(timeframe: Timeframe) {
        _state.update { it.copy(timeframe = timeframe) }
        loadUserStatistics()
    }

    private fun updateDateRange(startDate: Long, endDate: Long) {
        _state.update {
            it.copy(
                startDate = Date(startDate),
                endDate = Date(endDate)
            )
        }
        loadUserStatistics()
    }

    private fun refreshData() {
        loadUserStatistics()
    }

    // Helper method to generate mock data for demonstration
    private fun generateMockData(timeframe: Timeframe, startDate: Date, endDate: Date): List<UserStatPoint> {
        val calendar = Calendar.getInstance()
        val result = mutableListOf<UserStatPoint>()
        val dateFormat = when (timeframe) {
            Timeframe.DAY -> SimpleDateFormat("MMM dd", Locale.getDefault())
            Timeframe.MONTH -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
            Timeframe.YEAR -> SimpleDateFormat("yyyy", Locale.getDefault())
        }

        calendar.time = startDate
        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        // Random base to make the data look more realistic
        val random = java.util.Random(42)

        while (calendar.time <= endCalendar.time) {
            val date = calendar.time
            val baseCount = when (timeframe) {
                Timeframe.DAY -> 15
                Timeframe.MONTH -> 450
                Timeframe.YEAR -> 5400
            }
            val randomFactor = random.nextInt(baseCount / 2)
            val count = baseCount + randomFactor - baseCount / 4

            val label = dateFormat.format(date)
            result.add(UserStatPoint(label, count, date))

            when (timeframe) {
                Timeframe.DAY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                Timeframe.MONTH -> calendar.add(Calendar.MONTH, 1)
                Timeframe.YEAR -> calendar.add(Calendar.YEAR, 1)
            }
        }

        return result
    }
}