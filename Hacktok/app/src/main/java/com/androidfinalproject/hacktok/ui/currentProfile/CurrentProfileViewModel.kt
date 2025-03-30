package com.androidfinalproject.hacktok.ui.currentProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrentProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(CurrentProfileState(
        user = User(username = "", email = "")
    ))
    val state = _state.asStateFlow()

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