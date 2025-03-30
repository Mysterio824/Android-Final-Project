package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.util.Date


class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadPosts -> loadPosts()
            is DashboardAction.LikePost -> likePost(action.postId)
            is DashboardAction.SharePost -> sharePost(action.postId)
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

    private fun sharePost(postId: String) {
        // Placeholder logic for sharing a post
    }
}
