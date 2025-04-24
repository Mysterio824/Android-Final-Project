package com.androidfinalproject.hacktok.ui.statistic.userStatistic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Timeframe
import com.androidfinalproject.hacktok.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UserStatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {
    private val tag = "UserStatisticsViewModel"
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
                val currentTimeframe = state.value.timeframe
                val startDate = state.value.startDate
                val endDate = state.value.endDate

                statisticsRepository.observeUserStatistics(currentTimeframe, startDate, endDate)
                    .catch { error ->
                        Log.e(tag, "Error in user statistics flow", error)
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to load statistics: ${error.message}"
                            )
                        }
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = { stats ->
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        userStats = convertToUserStatPoints(stats.userStats),
                                        totalUsers = stats.totalUsers,
                                        newUsersInPeriod = stats.newUsersInPeriod,
                                        percentChange = stats.userPercentChange,
                                        error = null
                                    )
                                }
                            },
                            onFailure = { error ->
                                Log.e(tag, "Error loading user statistics", error)
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = "Failed to load statistics: ${error.message}"
                                    )
                                }
                            }
                        )
                    }
            } catch (e: Exception) {
                Log.e(tag, "Exception in loadUserStatistics", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateTimeframe(timeframe: com.androidfinalproject.hacktok.model.Timeframe) {
        _state.update { it.copy(timeframe = timeframe) }
        loadUserStatistics()
    }

    private fun updateDateRange(startDate: Long, endDate: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val normalizedStartDate = calendar.time

        calendar.timeInMillis = endDate
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val normalizedEndDate = calendar.time

        _state.update {
            it.copy(
                startDate = normalizedStartDate,
                endDate = normalizedEndDate
            )
        }
        loadUserStatistics()
    }

    private fun refreshData() {
        loadUserStatistics()
    }

    // Convert from UI Timeframe to model Timeframe
    private fun convertTimeframe(timeframe: com.androidfinalproject.hacktok.model.Timeframe): com.androidfinalproject.hacktok.model.Timeframe {
        return when (timeframe) {
            Timeframe.DAY -> com.androidfinalproject.hacktok.model.Timeframe.DAY
            Timeframe.MONTH -> com.androidfinalproject.hacktok.model.Timeframe.MONTH
            Timeframe.YEAR -> com.androidfinalproject.hacktok.model.Timeframe.YEAR
        }
    }

    // Convert from StatPoint of model to UserStatPoint of view
    private fun convertToUserStatPoints(statPoints: List<com.androidfinalproject.hacktok.model.StatPoint>): List<UserStatPoint> {
        return statPoints.map { statPoint ->
            UserStatPoint(
                label = statPoint.label,
                count = statPoint.count,
                date = statPoint.date
            )
        }
    }
}