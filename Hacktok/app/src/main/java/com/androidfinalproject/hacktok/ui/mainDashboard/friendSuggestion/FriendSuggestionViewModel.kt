package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.RelationshipService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendSuggestionViewModel @Inject constructor(
    private val relationshipService: RelationshipService,
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(FriendSuggestionState())
    val state: StateFlow<FriendSuggestionState> = _state.asStateFlow()

    init {
        loadScreen()
        
        // Observe relationships for real-time updates
        viewModelScope.launch {
            relationshipService.observeMyRelationships().collect { relations ->
                _state.update {
                    it.copy(relations = relations)
                }
            }
        }
    }

    fun onAction(action: FriendSuggestionAction) {
        when (action) {
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
            
            try {
                val currentUser = authService.getCurrentUser()

                _state.update { it.copy(user = currentUser) }
                
                val friendRequests = relationshipService.getMyFriendRequests()
                val friendSuggestions = relationshipService.getFriendSuggestions(10)
                val relation = relationshipService.getMyRelationships()

                // Update state - remove explicit setting of relations
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        users = friendRequests + friendSuggestions,
                        relations = relation
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load suggestions: ${e.message}"
                )}
            }
        }
    }

    private fun handleRequest(userId: String, isAccepted: Boolean) {
        viewModelScope.launch {
            try {
                val success = if (isAccepted) {
                    relationshipService.acceptFriendRequest(userId)
                } else {
                    relationshipService.declineFriendRequest(userId)
                }
                
                if (!success) {
                    _state.update { it.copy(
                        error = "Failed to ${if (isAccepted) "accept" else "decline"} friend request"
                    )}
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipService.sendFriendRequest(userId)
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to send friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun unSendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipService.cancelFriendRequest(userId)
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to cancel friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun removeFriendSuggestion(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipService.removeFromSuggestions(userId)
                
                if (success) {
                    // Remove from the UI
                    _state.update { state ->
                        state.copy(users = state.users.filter { it.id != userId })
                    }
                } else {
                    _state.update { it.copy(error = "Failed to remove suggestion") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }
}