package com.androidfinalproject.hacktok.ui.mainDashboard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.ReportService
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.repository.PostShareRepository

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authService: AuthService,
    private val postRepository: PostRepository,
    private val reportService: ReportService,
    private val postShareRepository: PostShareRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun onAction(action: HomeScreenAction) {
        when (action) {
            is HomeScreenAction.OnSharePost -> {
                viewModelScope.launch {
                    try {
                        postShareRepository.sharePost(
                            postId = action.post.id ?: return@launch,
                            caption = action.caption,
                            privacy = action.privacy.name,
                        )
                    } catch (e: Exception) {
                        Log.d("ERROR", e.toString())
                    }
                }
            }
            is HomeScreenAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is HomeScreenAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is HomeScreenAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is HomeScreenAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is HomeScreenAction.LikePost -> likePost(action.postId)
            is HomeScreenAction.SharePost -> sharePost(action.postId)
            is HomeScreenAction.SubmitReport -> submitReport(
                reportedItemId = action.reportedItemId,
                reportType = action.reportType,
                reportCause = action.reportCause
            )
            else -> {}
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _state.update{
                it.copy(
                    user = authService.getCurrentUser(),
                    isLoading = true,
                    error = null
                )
            }
            try {
                val mockPosts = MockData.mockPosts

                _state.update {
                    it.copy(
                        posts = mockPosts,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load content: ${e.message}"
                    )
                }
                launch { clearErrorAfterDelay() }
            }
        }
    }

    private fun likePost(postId: String) {
        _state.update { currentState ->
            val updatedPosts = currentState.posts.map {
                if (it.id.toString() == postId) it.copy(likeCount = it.likeCount + 1) else it
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    private fun sharePost(postId: String) {
        // Placeholder logic for sharing a post
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