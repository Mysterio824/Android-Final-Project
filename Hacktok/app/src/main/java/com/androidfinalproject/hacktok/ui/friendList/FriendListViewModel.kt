package com.androidfinalproject.hacktok.ui.friendList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.repository.RelationshipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FriendListState())
    val state: StateFlow<FriendListState> = _state.asStateFlow()
    
    private var currentUserId: String = ""

    fun initialize(userId: String) {
        currentUserId = userId
        loadFriends(userId)
        
        // Observe relationships for real-time updates
        viewModelScope.launch {
            relationshipRepository.observeRelationships(userId).collect { relations ->
                _state.update {
                    it.copy(relations = relations)
                }
                // Reload users when relationships change
                loadUsersForRelationships(relations)
            }
        }
    }

    fun onAction(action: FriendListAction) {
        when (action) {
            is FriendListAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is FriendListAction.SendFriendRequest -> sendRequest(action.userId, action.isSend)
            is FriendListAction.OnAcceptFriendRequest -> handleFriendRequest(action.userId, action.isAccepted)
            is FriendListAction.OnBlockFriend -> blockFriend(action.userId)
            is FriendListAction.OnUnBlockFriend -> unBlockFriend(action.userId)
            else -> {}
        }
    }

    private fun loadFriends(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // Get all relationships for the user
                val relations = relationshipRepository.getRelationshipsForUser(userId)
                _state.update { it.copy(relations = relations) }
                
                // Load users for these relationships
                loadUsersForRelationships(relations)
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Failed to load friends: ${e.message}")
                }
            }
        }
    }
    
    private suspend fun loadUsersForRelationships(relations: Map<String, RelationInfo>) {
        try {
            // Get all friend IDs to load
            val friendIds = relations.keys.toList()
            
            // Get friends and requests
            val friends = relationshipRepository.getFriendsForUser(currentUserId)
            val requests = relationshipRepository.getFriendRequestsForUser(currentUserId)
            
            // Update state with loaded users
            _state.update {
                it.copy(
                    users = friends + requests,
                    filteredUsers = if (it.searchQuery.isBlank()) {
                        friends + requests
                    } else {
                        (friends + requests).filter { user ->
                            (user.username?.contains(it.searchQuery, ignoreCase = true) ?: false) ||
                                    user.email.contains(it.searchQuery, ignoreCase = true)
                        }
                    },
                    isLoading = false
                )

            }
        } catch (e: Exception) {
            _state.update {
                it.copy(isLoading = false, error = "Failed to load users: ${e.message}")
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { current ->
            val filtered = if (query.isBlank()) current.users
            else current.users.filter {
                (it.username?.contains(query, ignoreCase = true) ?: false) ||
                        it.email.contains(query, ignoreCase = true)
            }
            current.copy(searchQuery = query, filteredUsers = filtered)
        }
    }

    private fun sendRequest(userId: String, isSend: Boolean) {
        viewModelScope.launch {
            try {
                val success = if (isSend) {
                    relationshipRepository.sendFriendRequest(currentUserId, userId)
                } else {
                    relationshipRepository.cancelFriendRequest(currentUserId, userId)
                }
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to ${if (isSend) "send" else "cancel"} friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun handleFriendRequest(userId: String, isAccepted: Boolean) {
        viewModelScope.launch {
            try {
                val success = if (isAccepted) {
                    relationshipRepository.acceptFriendRequest(currentUserId, userId)
                } else {
                    relationshipRepository.declineFriendRequest(currentUserId, userId)
                }
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to ${if (isAccepted) "accept" else "decline"} friend request") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun blockFriend(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipRepository.blockUser(currentUserId, userId)
                if (!success) {
                    _state.update { it.copy(error = "Failed to block user") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun unBlockFriend(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipRepository.unblockUser(currentUserId, userId)
                if (!success) {
                    _state.update { it.copy(error = "Failed to unblock user") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }
}
