package com.androidfinalproject.hacktok.ui.statistic.postStatistic

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
class PostStatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PostStatisticsState())
    val state: StateFlow<PostStatisticsState> = _state.asStateFlow()

    init {
        loadPostStatistics()
    }

    fun onAction(action: PostStatisticsAction) {
        when (action) {
            is PostStatisticsAction.LoadPostStatistics -> loadPostStatistics()
            is PostStatisticsAction.SelectTimeframe -> updateTimeframe(action.timeframe)
            is PostStatisticsAction.SetDateRange -> updateDateRange(action.startDate, action.endDate)
            is PostStatisticsAction.ToggleDataType -> updateDataType(action.dataType)
            is PostStatisticsAction.RefreshData -> refreshData()
            else -> {}
        }
    }

    private fun loadPostStatistics() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val currentTimeframe = state.value.timeframe
                val startDate = state.value.startDate
                val endDate = state.value.endDate

                statisticsRepository.observePostStatistics(
                    timeframe = currentTimeframe,
                    startDate = startDate,
                    endDate = endDate,
                    includeBanned = state.value.dataType != PostDataType.ALL_POSTS
                )
                    .catch { error ->
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
                                        postStats = stats.postStats.map { statPoint ->
                                            PostStatPoint(
                                                label = statPoint.label,
                                                count = statPoint.count,
                                                date = statPoint.date
                                            )
                                        },
                                        bannedPostStats = stats.bannedPostStats.map { statPoint ->
                                            PostStatPoint(
                                                label = statPoint.label,
                                                count = statPoint.count,
                                                date = statPoint.date
                                            )
                                        },
                                        totalPosts = stats.totalPosts,
                                        totalBannedPosts = stats.totalBannedPosts,
                                        newPostsInPeriod = stats.newPostsInPeriod,
                                        newBannedPostsInPeriod = stats.newBannedPostsInPeriod,
                                        postPercentChange = stats.postPercentChange,
                                        bannedPostPercentChange = stats.bannedPostPercentChange,
                                        error = null
                                    )
                                }
                            },
                            onFailure = { error ->
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
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateTimeframe(timeframe: Timeframe) {
        _state.update { it.copy(timeframe = timeframe) }
        loadPostStatistics()
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
        loadPostStatistics()
    }

    private fun updateDataType(dataType: PostDataType) {
        _state.update { it.copy(dataType = dataType) }
        loadPostStatistics()
    }

    private fun refreshData() {
        loadPostStatistics()
    }
}