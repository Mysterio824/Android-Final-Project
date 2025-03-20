package com.androidfinalproject.hacktok.ui.post

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

    // External action handler for navigation events
    var onUserProfileNavigate: ((userId: ObjectId?) -> Unit)? = null

    fun onAction(action: PostDetailAction) {
        when (action) {
            is PostDetailAction.LoadPost -> loadPost(action.postId)
            is PostDetailAction.LoadComments -> loadComments()
            is PostDetailAction.ToggleCommentSection -> toggleCommentSection()
            is PostDetailAction.ToggleLike -> toggleLike()
            is PostDetailAction.Share -> sharePost()
            is PostDetailAction.UpdateCommentText -> updateCommentText(action.text)
            is PostDetailAction.SubmitComment -> submitComment()
            is PostDetailAction.OnUserClick -> handleUserClick(action.user.id)
            is PostDetailAction.KeyboardShown -> showKeyboard()
            is PostDetailAction.KeyboardHidden -> hideKeyboard()
            PostDetailAction.NavigateBack -> TODO()
        }
    }

    private fun loadPost(postId: ObjectId?) {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }

            try {
                // Mock data
                val post = MockData.mockPosts.first()
                val user = post.user


                _state.update { it.copy(post = post) }
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
                val comments = listOf(
                    Comment(
                        id = ObjectId(),
                        comment = "Great post!",
                        like = 5,
                        user = User(id = ObjectId(), username = "Alice", email = "alice@example.com")
                    ),
                    Comment(
                        id = ObjectId(),
                        comment = "I disagree with this point.",
                        like = 2,
                        user = User(id = ObjectId(), username = "Bob", email = "bob@example.com")
                    ),
                    Comment(
                        id = ObjectId(),
                        comment = "Very insightful, thanks for sharing!",
                        like = 8,
                        user = User(id = ObjectId(), username = "Carol", email = "carol@example.com")
                    )
                )

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

    private fun toggleCommentSection() {
        val newVisibility = !_state.value.isCommentsVisible
        _state.update { it.copy(isCommentsVisible = newVisibility) }

        if (newVisibility && _state.value.comments.isEmpty()) {
            onAction(PostDetailAction.LoadComments)
        }
    }

    private fun toggleLike() {
        _state.value.post?.let { post ->
            val updatedPost = post.copy(likeCount = post.likeCount + 1)
            _state.update { it.copy(post = updatedPost) }
        }
    }

    private fun sharePost() {
        // Implementation for sharing would be handled here
        // Usually involves platform-specific code
    }

    private fun updateCommentText(text: String) {
        _state.update { it.copy(commentText = text) }
    }

    private fun submitComment() {
        val currentText = _state.value.commentText.trim()
        if (currentText.isNotEmpty()) {
            val newComment = Comment(
                id = ObjectId(),
                comment = currentText,
                like = 0,
                user = User(id = ObjectId(), username = "CurrentUser", email = "me@example.com")
            )

            _state.update {
                it.copy(
                    comments = listOf(newComment) + it.comments,
                    commentText = "",
                    isCommentsVisible = true
                )
            }
        }
    }

    private fun handleUserClick(userId: ObjectId?) {
        onUserProfileNavigate?.invoke(userId)
    }

    private fun showKeyboard() {
        _state.update { it.copy(isKeyboardVisible = true) }
    }

    private fun hideKeyboard() {
        _state.update { it.copy(isKeyboardVisible = false) }
    }
}