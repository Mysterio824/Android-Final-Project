package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow<CurrentProfileState>(CurrentProfileState.Loading)
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
        loadPosts()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _state.value = CurrentProfileState.Loading
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _state.value = CurrentProfileState.Success(
                        user = user,
                        posts = MockData.mockPosts,
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

    fun loadPosts() {
        // For now, we'll use mock posts
        if (_state.value is CurrentProfileState.Success) {
            val currentState = _state.value as CurrentProfileState.Success
            _state.value = currentState.copy(posts = MockData.mockPosts)
        }
    }

    fun editPost(postId: String?, newContent: String) {
        viewModelScope.launch {
            try {
                // Implement your post editing logic here
                // This could involve a repository call to update the post
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to edit post: ${e.message}")
            }
        }
    }

    fun deletePost(postId: String?) {
        viewModelScope.launch {
            try {
                if (postId != null) {
                    // Implement your post deletion logic here
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
            is CurrentProfileAction.OnEditPost -> {
                // Handle edit post
            }
            is CurrentProfileAction.OnDeletePost -> {
                deletePost(action.post.id)
            }
            else -> {} // Handle other actions
        }
    }
}