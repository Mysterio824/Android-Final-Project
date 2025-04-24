package com.androidfinalproject.hacktok.ui.statistic.postStatistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Timeframe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PostStatisticsViewModel : ViewModel() {
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
                // Simulate API call with delay
                kotlinx.coroutines.delay(1000)

                val mockState = MockData.createMockPostStatisticsState()

                _state.update { it.copy(
                    isLoading = false,
                    postStats = mockState.postStats,
                    bannedPostStats = mockState.bannedPostStats,
                    totalPosts = mockState.totalPosts,
                    totalBannedPosts = mockState.totalBannedPosts,
                    newPostsInPeriod = mockState.newPostsInPeriod,
                    newBannedPostsInPeriod = mockState.newBannedPostsInPeriod,
                    postPercentChange = mockState.postPercentChange,
                    bannedPostPercentChange = mockState.bannedPostPercentChange
                ) }
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
    }

    private fun refreshData() {
        loadPostStatistics()
    }

    // Helper method to generate mock data for demonstration
    private fun generateMockData(
        timeframe: Timeframe,
        startDate: Date,
        endDate: Date
    ): Pair<List<PostStatPoint>, List<PostStatPoint>> {
        val calendar = Calendar.getInstance()
        val resultPosts = mutableListOf<PostStatPoint>()
        val resultBannedPosts = mutableListOf<PostStatPoint>()

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

            // Generate post count data
            val basePostCount = when (timeframe) {
                Timeframe.DAY -> 100
                Timeframe.MONTH -> 3000
                Timeframe.YEAR -> 36000
            }
            val randomPostFactor = random.nextInt(basePostCount / 2)
            val postCount = basePostCount + randomPostFactor - basePostCount / 4

            // Generate banned post count data (roughly 3-5% of total posts)
            val bannedPostRatio = 0.03 + (random.nextDouble() * 0.02) // 3-5%
            val bannedPostCount = (postCount * bannedPostRatio).toInt()

            val label = dateFormat.format(date)
            resultPosts.add(PostStatPoint(label, postCount, date))
            resultBannedPosts.add(PostStatPoint(label, bannedPostCount, date))

            when (timeframe) {
                Timeframe.DAY -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                Timeframe.MONTH -> calendar.add(Calendar.MONTH, 1)
                Timeframe.YEAR -> calendar.add(Calendar.YEAR, 1)
            }
        }

        return Pair(resultPosts, resultBannedPosts)
    }
}