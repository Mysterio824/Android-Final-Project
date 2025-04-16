package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendSuggestionViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FriendSuggestionState())
    val state: StateFlow<FriendSuggestionState> = _state.asStateFlow()
    
    private var currentUser: User? = null

    fun initialize(user: User) {
        currentUser = user
        loadScreen(user.id ?: return)
        
        // Observe relationships for real-time updates
        user.id?.let { userId ->
            viewModelScope.launch {
                relationshipRepository.observeRelationships(userId).collect { relations ->
                    _state.update {
                        it.copy(relations = relations)
                    }
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

    private fun loadScreen(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, user = currentUser) }
            
            try {
                // Get relationships
                val relations = relationshipRepository.getRelationshipsForUser(userId)
                
                // Get friend requests
                val friendRequests = relationshipRepository.getFriendRequestsForUser(userId)
                
                // Get friend suggestions
                val friendSuggestions = relationshipRepository.getFriendSuggestions(userId, 10)
                
                _state.update { it.copy(
                    isLoading = false,
                    users = friendSuggestions,
                    incomingRequests = friendRequests,
                    relations = relations
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load suggestions: ${e.message}"
                )}
            }
        }
    }

    private fun handleRequest(userId: String, isAccepted: Boolean) {
        val currentUserId = currentUser?.id ?: return
        
        viewModelScope.launch {
            try {
                val success = if (isAccepted) {
                    relationshipRepository.acceptFriendRequest(currentUserId, userId)
                } else {
                    relationshipRepository.declineFriendRequest(currentUserId, userId)
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
        val currentUserId = currentUser?.id ?: return
        
        viewModelScope.launch {
            try {
                val success = relationshipRepository.sendFriendRequest(currentUserId, userId)
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to send friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun unSendRequest(userId: String) {
        val currentUserId = currentUser?.id ?: return
        
        viewModelScope.launch {
            try {
                val success = relationshipRepository.cancelFriendRequest(currentUserId, userId)
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to cancel friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun removeFriendSuggestion(userId: String) {
        val currentUserId = currentUser?.id ?: return
        
        viewModelScope.launch {
            try {
                val success = relationshipRepository.removeFromSuggestions(currentUserId, userId)
                
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