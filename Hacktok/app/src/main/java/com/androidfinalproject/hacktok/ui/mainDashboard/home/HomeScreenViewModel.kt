package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun onAction(action: HomeScreenAction) {
        when (action) {
            is HomeScreenAction.LikePost -> likePost(action.postId)
            is HomeScreenAction.UpdateStatusText -> updateText(action.text)
            is HomeScreenAction.SharePost -> {}
            is HomeScreenAction.OnPostClick -> {}
            is HomeScreenAction.OnUserClick -> {}
            else -> {}
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
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
                        error = "Failed to load posts: ${e.message}"
                    )
                }
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

    private fun updateText(text: String) {
        _state.update { it.copy(query = text) }
    }

    private fun sharePost(postId: String) {
        // Placeholder logic for sharing a post
    }
}