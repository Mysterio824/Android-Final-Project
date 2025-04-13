package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrentProfileViewModel(userId: String) : ViewModel() {
    private val _state = MutableStateFlow(CurrentProfileState())
    val state = _state.asStateFlow()

    init {
        loadUser(userId)
        loadPosts(userId)
    }

    fun loadUser(userId: String) {
        _state.update { it.copy(user = MockData.mockUsers.first()) }
    }

    fun loadPosts(userId: String) {
        _state.update { it.copy(posts = MockData.mockPosts) }
    }

    fun editPost(postId: String?, newContent: String) {
        viewModelScope.launch {
            try {
                // Implement your post editing logic here
                // This could involve a repository call to update the post
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to edit post: ${e.message}")
                }
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                // Implement your post deletion logic here

            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Failed to delete post: ${e.message}")
                }
            }
        }
    }
}