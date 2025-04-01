package com.androidfinalproject.hacktok.ui.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class PostDetailViewModel : ViewModel() {
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
            is PostDetailAction.NavigateBack,
            is PostDetailAction.OnUserClick -> {
                Log.w("PostDetailViewModel", "Navigation action reached ViewModel but should be handled in Root: $action")
            }
        }
    }

    private fun toggleCommentInputFocus() {
        _state.update { currentState ->
            currentState.copy(isCommenting = !currentState.isCommenting)
        }
    }

    private fun setCommentFocus(focused: Boolean) {
        _state.update { it.copy(isCommenting = focused) }
    }

    private fun loadPost(postId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }

            try {
                // Mock data
                val post = MockData.mockPosts.first()
                val user = MockData.mockUsers.first()
                val comment = MockData.mockComments


                _state.update { it.copy(post = post, comments = comment ) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load post: ${e.message}") }
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingComments = true, error = null) }

            try {
                // Simulate API call delay
                kotlinx.coroutines.delay(500)

                // Mock comments
                val comments = MockData.mockComments

                _state.update { it.copy(comments = comments, isLoadingComments = false) }
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
        Log.d("Text:", text)
        _state.update {
            it.copy(commentText = text)
        }
    }

    private fun submitComment() {
        val currentText = _state.value.commentText.trim()
        if (currentText.isNotEmpty()) {
            val newComment = MockData.mockComments.first()

            _state.update {
                it.copy(
                    comments = listOf(newComment) + it.comments,
                    commentText = ""
                )
            }
        }
    }

    private fun handleLikeComment(commentId: String?) {
        //TODO
    }

}