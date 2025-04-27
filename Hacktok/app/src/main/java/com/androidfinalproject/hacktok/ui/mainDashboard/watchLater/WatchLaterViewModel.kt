package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.ReportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchLaterViewModel @Inject constructor(
//    private val postService: PostService
    private val authService: AuthService,
    private val reportService: ReportService,
    private val likeService: LikeService
) : ViewModel() {
    private val _state = MutableStateFlow(WatchLaterState())
    val state: StateFlow<WatchLaterState> = _state.asStateFlow()

    init {
        loadSavedPosts()
    }

    fun onAction(action: WatchLaterAction) {
        when (action) {
            is WatchLaterAction.RemovePost -> removePost(action.postId)
            is WatchLaterAction.OnLikeClick -> likePost(action.postId)
            is WatchLaterAction.SubmitReport -> submitReport(action.reportedItemId, action.reportType, action.reportCause)
            is WatchLaterAction.OnUnLikeClick -> unlikePost(action.postId)
            else -> {}
        }
    }

    private fun loadSavedPosts() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    error = null,
                )
            }

            _state.update { currentState ->
                currentState.copy(
                    currentUserId = authService.getCurrentUserId(),
                    savedPosts = MockData.mockPosts,
                    isLoading = false
                )
            }
        }
    }

    private fun removePost(postId: String) {
        _state.update { currentState ->
            currentState.copy(
                savedPosts = currentState.savedPosts.filter { it.id != postId }
            )
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            val updatedPost = likeService.likePost(postId) ?: return@launch

            val newList = _state.value.savedPosts.map { post ->
                if (post.id == updatedPost.id) updatedPost.copy(
                    userId = post.userId,
                ) else post
            }

            _state.update { currentState ->
                currentState.copy(savedPosts = newList)
            }
        }
    }


    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            val updatedPost = likeService.unlikePost(postId) ?: return@launch

            val newList = _state.value.savedPosts.map { post ->
                if (post.id == updatedPost.id) updatedPost.copy(
                    userId = post.userId,
                ) else post
            }

            _state.update { currentState ->
                currentState.copy(savedPosts = newList)
            }
        }
    }

    private fun submitReport(reportedItemId: String, reportType: ReportType, reportCause: ReportCause) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, userMessage = null) }
            try {
                reportService.submitReport(
                    reportedItemId = reportedItemId,
                    reportType = reportType,
                    reportCause = reportCause
                )
                _state.update { it.copy(isLoading = false, userMessage = "Report submitted successfully.") }
                launch { clearMessageAfterDelay() }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to submit report: ${e.message}"
                    )
                }
                launch { clearErrorAfterDelay() }
            }
        }
    }

    private suspend fun clearMessageAfterDelay() {
        delay(1000)
        _state.update { it.copy(userMessage = null) }
    }

    private suspend fun clearErrorAfterDelay() {
        delay(3000)
        _state.update { it.copy(error = null) }
    }
}