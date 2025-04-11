package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.RelationshipStatus
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendSuggestionViewModel(user: User): ViewModel() {
    private val _state = MutableStateFlow(FriendSuggestionState(
        user = user
    ))
    val state: StateFlow<FriendSuggestionState> = _state.asStateFlow()

    init {
        loadScreen()
    }

    fun onAction(action: FriendSuggestionAction){
        when(action){
            is FriendSuggestionAction.HandleRequest -> handleRequest(action.userId, action.isAccepted)
            is FriendSuggestionAction.OnRemove -> removeFriendSuggestion(action.userId)
            is FriendSuggestionAction.SendRequest -> sendFriendRequest(action.userId)
            is FriendSuggestionAction.UnSendRequest -> unSendRequest(action.userId)
            else -> {}
        }
    }

    private fun loadScreen() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val users = MockData.mockUsers
            val relations = MockData.mockRelations

            _state.update { it.copy(
                    isLoading = false,
                    users = users,
                    relations = relations
                )
            }
        }
    }

    private fun handleRequest(userId: String, isAccepted: Boolean) {}

    private fun sendFriendRequest(userId: String) {}

    private fun unSendRequest(userId: String) {}

    private fun removeFriendSuggestion(userId: String) {}
}