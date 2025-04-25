package com.androidfinalproject.hacktok.ui.statistic.commentStatistic

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
class CommentStatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CommentStatisticsState())
    val state: StateFlow<CommentStatisticsState> = _state.asStateFlow()

    init {
        loadCommentStatistics()
    }

    fun onAction(action: CommentStatisticsAction) {
        when (action) {
            is CommentStatisticsAction.LoadCommentStatistics -> loadCommentStatistics()
            is CommentStatisticsAction.SelectTimeframe -> updateTimeframe(action.timeframe)
            is CommentStatisticsAction.SetDateRange -> updateDateRange(action.startDate, action.endDate)
            is CommentStatisticsAction.ToggleDataType -> updateDataType(action.dataType)
            is CommentStatisticsAction.RefreshData -> refreshData()
            else -> {}
        }
    }

    private fun loadCommentStatistics() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val currentTimeframe = state.value.timeframe
                val startDate = state.value.startDate
                val endDate = state.value.endDate

                statisticsRepository.observeCommentStatistics(
                    timeframe = currentTimeframe,
                    startDate = startDate,
                    endDate = endDate,
                    includeBanned = state.value.dataType != CommentDataType.ALL_COMMENTS
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
                                        commentStats = stats.commentStats.map { statPoint ->
                                            CommentStatPoint(
                                                label = statPoint.label,
                                                count = statPoint.count,
                                                date = statPoint.date
                                            )
                                        },
                                        bannedCommentStats = stats.bannedCommentStats.map { statPoint ->
                                            CommentStatPoint(
                                                label = statPoint.label,
                                                count = statPoint.count,
                                                date = statPoint.date
                                            )
                                        },
                                        totalComments = stats.totalComments,
                                        totalBannedComments = stats.totalBannedComments,
                                        newCommentsInPeriod = stats.newCommentsInPeriod,
                                        newBannedCommentsInPeriod = stats.newBannedCommentsInPeriod,
                                        commentPercentChange = stats.commentPercentChange,
                                        bannedCommentPercentChange = stats.bannedCommentPercentChange,
                                        dailyComments = stats.dailyCount,
                                        monthlyComments = stats.monthlyCount,
                                        yearlyComments = stats.yearlyCount,
                                        bannedComments = stats.totalBannedComments,
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
        loadCommentStatistics()
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
        loadCommentStatistics()
    }

    private fun updateDataType(dataType: CommentDataType) {
        _state.update { it.copy(dataType = dataType) }
        loadCommentStatistics()
    }

    private fun refreshData() {
        loadCommentStatistics()
    }
}