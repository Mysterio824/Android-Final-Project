package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WatchLaterViewModel : ViewModel() {
    private val _state = MutableStateFlow(WatchLaterState())
    val state: StateFlow<WatchLaterState> = _state.asStateFlow()

    init {
        loadSavedPosts()
    }

    fun onAction(action: WatchLaterAction) {
        when (action) {
            is WatchLaterAction.RemovePost -> removePost(action.postId)
            is WatchLaterAction.OnLikeClick -> {}
            else -> {}
        }
    }

    private fun loadSavedPosts() {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    error = null
                )
            }

            _state.update { currentState ->
                currentState.copy(
                    savedPosts = MockData.mockPosts,
                    isLoading = false
                )
            }
        }
    }

    private fun removePost(postId: String) {
        _state.update { currentState ->
            currentState.copy(
                savedPosts = currentState.savedPosts.filter { it.id != postId }
            )
        }
    }
}