package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.repository.PostRepository
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
    private val authService: AuthService,
    private val reportService: ReportService,
    private val likeService: LikeService,
    private val postRepository: PostRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WatchLaterState())
    val state: StateFlow<WatchLaterState> = _state.asStateFlow()

    init {
        loadSavedPosts()
    }

    fun onAction(action: WatchLaterAction) {
        when (action) {
            is WatchLaterAction.RemovePost -> removePost(action.postId)
            is WatchLaterAction.OnLikeClick -> likePost(action.postId, action.emoji)
            is WatchLaterAction.SubmitReport -> submitReport(action.reportedItemId, action.reportType, action.reportCause)
            is WatchLaterAction.OnUnLikeClick -> unlikePost(action.postId)
            is WatchLaterAction.OnLikesShowClick -> loadLikesUser(action.targetId)
            is WatchLaterAction.OnSharePost -> {
                viewModelScope.launch {
                    val post = Post(
                        content = action.caption,
                        userId = state.value.user?.id ?: "",
                        refPostId = action.post.id,
                        privacy = action.privacy.name,
                    )
                    try {
                        postRepository.addPost(post)
                    } catch (e: Exception) {
                        Log.d("ERROR", e.toString())
                    }
                }
            }
            is WatchLaterAction.DeletePost -> deletePost(action.postId)
            is WatchLaterAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is WatchLaterAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is WatchLaterAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is WatchLaterAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            else -> {}
        }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                postRepository.deletePost(postId)
            } catch(e: Exception) {
                _state.update {
                    Log.e("WatchlaterViewmodel", e.message.toString())
                    it.copy(error = e.message)
                }
            }
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
//        _state.update { currentState ->
//            currentState.copy(
//                savedPosts = currentState.savedPosts.filter { it.id != postId }
//            )
//        }
    }

    private fun loadLikesUser(targetId: String) {
        viewModelScope.launch {
            try {
                val likeUsers = likeService.getPostLike(targetId)
                _state.update{
                    it.copy(listLikeUser = likeUsers)
                }
            } catch(e: Exception){
                Log.d("PostDetailViewModel", e.message.toString())
            }
        }
    }

    private fun likePost(postId: String, emoji: String) {
        viewModelScope.launch {
            val updatedPost = likeService.likePost(postId, emoji) ?: return@launch

            val newList = _state.value.savedPosts.map { post ->
                if (post.id == updatedPost.id) updatedPost else post
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
                if (post.id == updatedPost.id) updatedPost else post
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