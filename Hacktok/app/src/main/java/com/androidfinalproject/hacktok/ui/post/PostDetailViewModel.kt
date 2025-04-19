package com.androidfinalproject.hacktok.ui.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.CommentService
import com.androidfinalproject.hacktok.service.ReportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val authService: AuthService,
    private val reportService: ReportService,
    private val commentService: CommentService
) : ViewModel() {
    private val tag = "PostViewModel"
    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    fun onAction(action: PostDetailAction) {
        when (action) {
            is PostDetailAction.LoadPost -> loadPost(action.postId)
            is PostDetailAction.LoadComments -> loadComments()
            is PostDetailAction.ToggleLike -> toggleLike()
            is PostDetailAction.Share -> sharePost()
            is PostDetailAction.UpdateCommentText -> updateCommentText(action.text)
            is PostDetailAction.SubmitComment -> submitComment()
            is PostDetailAction.ToggleCommentInputFocus -> toggleCommentInputFocus()
            is PostDetailAction.SetCommentFocus -> setCommentFocus(action.focused)
            is PostDetailAction.LikeComment -> handleLikeComment(action.commentId, true)
            is PostDetailAction.UnLikeComment -> handleLikeComment(action.commentId, false)
            is PostDetailAction.SelectCommentToReply -> selectComment(action.commentId)
            is PostDetailAction.DeleteComment -> deleteComment(action.commentId)
            is PostDetailAction.SubmitReport -> submitReport(action.reportedItemId, action.reportType, action.reportCause)
            else -> {}
        }
    }

    private fun toggleCommentInputFocus() {
        _state.update { currentState ->
            currentState.copy(
                isCommenting = !currentState.isCommenting,
                commentIdReply = ""
            )
        }
    }

    private fun setCommentFocus(focused: Boolean) {
        _state.update { it.copy(isCommenting = focused) }
    }

    private fun loadPost(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            try {
                val post = postRepository.getPost(postId)
                val currentUser = authService.getCurrentUser()
                    ?: throw IllegalStateException("User not found")

                _state.update { it.copy(post = post, currentUser = currentUser) }
                loadComments()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load post: ${e.message}") }
            }
        }
    }

    private fun loadComments() {
        val postId = state.value.post?.id
        if (postId == null) {
            Log.e(tag, "Cannot load comments: post ID is null")
            _state.update { it.copy(error = "Cannot load comments: post ID is missing") }
            return
        }
        
        viewModelScope.launch {
            Log.d(tag, "Starting to load comments for post: $postId")
            _state.update { it.copy(isLoadingComments = true, error = null) }

            try {
                // Use the improved observeCommentsForPost method
                commentService.observeCommentsForPost(
                    postId = postId,
                    parentCommentId = null, // Only top-level comments
                    limit = 100
                )
                .catch { error ->
                    Log.e(tag, "Error in comment flow", error)
                    _state.update { 
                        it.copy(
                            isLoadingComments = false,
                            error = "Failed to load comments: ${error.message}"
                        ) 
                    }
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { comments ->
                            Log.d(tag, "Successfully loaded ${comments.size} comments")
                            _state.update { 
                                it.copy(
                                    comments = comments, 
                                    isLoadingComments = false, 
                                    error = null
                                ) 
                            }
                        },
                        onFailure = { error ->
                            Log.e(tag, "Error loading comments", error)
                            _state.update { 
                                it.copy(
                                    isLoadingComments = false,
                                    error = "Failed to load comments: ${error.message}"
                                ) 
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Exception in loadComments", e)
                _state.update {
                    it.copy(
                        isLoadingComments = false,
                        error = "Failed to load comments: ${e.message}"
                    )
                }
            }
        }
    }

    private fun toggleLike() {
        _state.value.post?.let { post ->
            val updatedPost = post.copy(likeCount = post.likeCount + 1)
            _state.update { it.copy(post = updatedPost) }
        }
    }

    private fun sharePost() {
        // TODO
    }

    private fun updateCommentText(text: String) {
        _state.update {
            it.copy(commentText = text)
        }
    }

    private fun selectComment(commentId: String) {
        _state.update { it.copy(commentIdReply = commentId) }
    }

    private fun submitComment() {
        val currentText = _state.value.commentText.trim()
        if (currentText.isNotEmpty()) {
            val parenId = _state.value.commentIdReply
            val postId = state.value.post?.id

            if (postId == null) {
                _state.update { it.copy(error = "Cannot add comment: post ID is missing") }
                return
            }

            viewModelScope.launch {
                try {
                    if (parenId.isEmpty()) {
                        commentService.addComment(currentText, postId)
                    } else {
                        commentService.replyComment(currentText, parenId)
                    }.getOrThrow()

                    // Clear comment text and reply ID after submission
                    _state.update { it.copy(commentText = "", commentIdReply = "") }
                } catch (e: Exception) {
                    Log.e(tag, "Error submitting comment", e)
                    _state.update { it.copy(error = "Failed to submit comment: ${e.message}") }
                }
            }
        }
    }

    private fun handleLikeComment(commentId: String?, isLiking: Boolean) {
        if (commentId == null) {
            Log.e(tag, "Cannot like comment: comment ID is null")
            return
        }
        
        viewModelScope.launch {
            try {
                if(isLiking) commentService.likeComment(commentId)
                else commentService.unlikeComment(commentId)
                
                // The likes will be updated automatically through the observeCommentsForPost Flow
            } catch (e: Exception) {
                Log.e(tag, "Error liking comment", e)
                _state.update { it.copy(error = "Failed to like comment: ${e.message}") }
            }
        }
    }

    private fun deleteComment(commentId: String) {
        viewModelScope.launch {
            try {
                commentService.deleteComment(commentId)
                // The comment will be removed automatically through the observeCommentsForPost Flow
            } catch (e: Exception) {
                Log.e(tag, "Error deleting comment", e)
                _state.update { it.copy(error = "Failed to delete comment: ${e.message}") }
            }
        }
    }

    private fun submitReport(reportedItemId: String, reportType: ReportType, reportCause: ReportCause) {
        viewModelScope.launch {
            try {
                reportService.submitReport(
                    reportedItemId = reportedItemId,
                    reportType = reportType,
                    reportCause = reportCause
                )
            } catch (e: Exception) {
                Log.e(tag, "Error submitting report", e)
                _state.update {
                    it.copy(
                        error = "Failed to submit report: ${e.message}"
                    )
                }
            }
        }
    }
}