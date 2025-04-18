package com.androidfinalproject.hacktok.ui.currentProfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {
    private val _state = MutableStateFlow<CurrentProfileState>(CurrentProfileState.Loading)
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _state.value = CurrentProfileState.Loading
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    val userId = user.id ?: return@launch
                    val posts = postRepository.getPostsByUser(userId)
                    _state.value = CurrentProfileState.Success(
                        user = user,
                        posts = posts,
                        friendCount = 0
                    )
                } else {
                    _state.value = CurrentProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to load user: ${e.message}")
            }
        }
    }

    fun editPost(postId: String?, newContent: String) {
        viewModelScope.launch {
            try {
                if (postId != null) {
                    val updates = mapOf("content" to newContent)
                    postRepository.updatePost(postId, updates)
                    loadCurrentUser() // Reload to refresh the posts
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to edit post: ${e.message}")
            }
        }
    }

    fun deletePost(postId: String?) {
        viewModelScope.launch {
            try {
                if (postId != null) {
                    postRepository.deletePost(postId)
                    Log.d("CurrentProfileViewModel", "Post deleted successfully")
                    loadCurrentUser() // Reload to refresh the posts
                } else {
                    _state.value = CurrentProfileState.Error("Cannot delete post: Invalid post ID")
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to delete post: ${e.message}")
            }
        }
    }

    fun onAction(action: CurrentProfileAction) {
        when (action) {
            is CurrentProfileAction.OnNavigateBack -> {
                // Handle navigation back
            }
            is CurrentProfileAction.OnEditProfile -> {
                // Handle edit profile
            }
            is CurrentProfileAction.OnCreatePost -> {
                // Handle create post
            }
            is CurrentProfileAction.RetryLoading -> {
                loadCurrentUser()
            }
            is CurrentProfileAction.OnDeletePost -> {
                deletePost(action.post.id)
            }
            else -> {

            } // Handle other actions
        }
    }
}