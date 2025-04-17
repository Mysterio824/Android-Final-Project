package com.androidfinalproject.hacktok.ui.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.CommentService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
//    private val postService: PostService,
    private val authService: AuthService,
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
            is PostDetailAction.LikeComment -> handleLikeComment(action.commentId)
            is PostDetailAction.SelectCommentToReply -> selectComment(action.commentId)
            is PostDetailAction.DeleteComment -> deleteComment(action.commentId)
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

    private fun loadPost(postId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            try {
                Log.d(tag, "here")
                // Simulate fetching the post
                val post = MockData.mockPosts.first()
                val currentUser = authService.getCurrentUser()
                    ?: throw IllegalStateException("User not found")

                Log.d(tag, "username: ${currentUser.username.toString()}")
                _state.update { it.copy(post = post, currentUser = currentUser) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load post: ${e.message}") }
            }
        }
        if(state.value.post != null){
            loadComments()
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingComments = true, error = null) }

            try {
                // Call the service to load comments
                commentService.getCommentsForPost(state.value.post!!.id!!).collect { comments ->
                    _state.update { it.copy(comments = comments, isLoadingComments = false) }
                }
            } catch (e: Exception) {
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

            viewModelScope.launch {
                try {
                    val newComment : Comment = if (parenId.isEmpty()) {
                        commentService.addComment(currentText, state.value.post!!.id!!)
                    } else {
                        commentService.replyComment(currentText, state.value.commentIdReply)
                    }.getOrThrow()

                    _state.update {
                        it.copy(
                            comments = listOf(newComment) + it.comments,
                            commentText = ""
                        )
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Failed to submit comment: ${e.message}") }
                }
            }
        }
    }

    private fun handleLikeComment(commentId: String?) {
        viewModelScope.launch {
            commentId?.let { id ->
                try {
                    commentService.likeComment(id, "user_id_placeholder") // Replace with actual user ID
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Failed to like comment: ${e.message}") }
                }
            }
        }
    }

    private fun deleteComment(commentId: String) {
        viewModelScope.launch {
            try {
                commentService.deleteComment(commentId)
                _state.update {
                    it.copy(comments = it.comments.filter { comment -> comment.id != commentId })
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete comment: ${e.message}") }
            }
        }
    }
}