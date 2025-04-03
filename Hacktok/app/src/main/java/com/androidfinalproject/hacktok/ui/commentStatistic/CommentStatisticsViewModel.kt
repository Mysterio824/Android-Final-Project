package com.androidfinalproject.hacktok.ui.commentStatistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class CommentStatisticsViewModel : ViewModel() {

    // State for statistics data
    private val _statistics = MutableStateFlow(
        CommentStatistics(
            dailyComments = 0,
            monthlyComments = 0,
            yearlyComments = 0,
            bannedComments = 0
        )
    )
    val statistics: StateFlow<CommentStatistics> = _statistics.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Repository would be injected in a real app
    // private val commentRepository: CommentRepository

    init {
        loadStatistics()
    }

    fun refreshStatistics() {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // In a real app, this would call the repository
                // val stats = commentRepository.getCommentStatistics()

                // For demonstration purposes, simulate a network delay and generate mock data
                delay(1500)

                // Mock data
                val mockStats = CommentStatistics(
                    dailyComments = Random.nextInt(10, 100),
                    monthlyComments = Random.nextInt(200, 1000),
                    yearlyComments = Random.nextInt(2000, 10000),
                    bannedComments = Random.nextInt(5, 50)
                )

                _statistics.value = mockStats
            } catch (e: Exception) {
                // Handle errors
                // In a real app, you might want to show an error message
                // _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // In a real app, you might have additional methods like:

    fun markCommentAsBanned(commentId: String) {
        viewModelScope.launch {
            // Call repository to mark comment as banned
            // commentRepository.markAsBanned(commentId)

            // Then refresh statistics
            loadStatistics()
        }
    }

    fun getCommentsByDate(date: LocalDate) {
        viewModelScope.launch {
            // Implementation to get comments for a specific date
            // val comments = commentRepository.getCommentsByDate(date)
        }
    }
}