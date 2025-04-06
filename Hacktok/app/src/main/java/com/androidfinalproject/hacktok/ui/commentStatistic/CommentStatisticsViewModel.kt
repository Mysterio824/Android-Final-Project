package com.androidfinalproject.hacktok.ui.commentStatistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CommentStatisticsViewModel : ViewModel() {

    // State for statistics data
    private val _state = MutableStateFlow(CommentStatisticsState(isLoading = true))
    val state: StateFlow<CommentStatisticsState> = _state.asStateFlow()

    init {
        loadStatistics()
    }

    fun refreshStatistics() {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            // Update state to show loading
            _state.value = _state.value.copy(isLoading = true)

            try {
                // Simulate a network delay and generate mock data
                delay(1500)

                // Generate mock data
                val dailyComments = Random.nextInt(10, 100)
                val monthlyComments = Random.nextInt(200, 1000)
                val yearlyComments = Random.nextInt(2000, 10000)
                val bannedComments = Random.nextInt(5, 50)

                // Update the state
                _state.value = _state.value.copy(
                    dailyComments = dailyComments,
                    monthlyComments = monthlyComments,
                    yearlyComments = yearlyComments,
                    bannedComments = bannedComments,
                    isLoading = false
                )

            } catch (e: Exception) {
                // Handle errors (optional)
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun markCommentAsBanned(commentId: String) {
        viewModelScope.launch {
            // Simulate banning a comment
            loadStatistics()
        }
    }
}