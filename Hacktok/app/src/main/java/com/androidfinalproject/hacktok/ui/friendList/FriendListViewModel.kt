package com.androidfinalproject.hacktok.ui.friendList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
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
class FriendListViewModel @Inject constructor(
    private val relationshipService: RelationshipService,
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(FriendListState())
    val state: StateFlow<FriendListState> = _state.asStateFlow()

    init {
        // No auto-loading in init since we need userId first
    }
    
    // This is still needed for backward compatibility with screens that manually initialize
    fun initialize(userId: String) {
        _state.update { it.copy(userId = userId, currentUserId = authService.getCurrentUserIdSync()!!) }
        loadFriendsData()
    }

    private fun loadFriendsData() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                
                // Get relationships between current user and the friends of target user
                val relation = relationshipService.getRelationshipsWithFriendsOfUser(_state.value.userId)
                _state.update { it.copy(relations = relation) }
                
                // Load user data for these relationships
                loadUsersForRelationships(relation)
                
                // Also get friend requests
                val friendRequests = relationshipService.getMyFriendRequests()
                _state.update { it.copy(friendRequests = friendRequests) }
            } catch (e: Exception) {
                Log.e("FriendListViewModel", "Error loading friends data: ${e.message}", e)
                _state.update { it.copy(
                    isLoading = false,
                    error = "Failed to load relationship data: ${e.message}"
                )}
            }
        }
    }

    fun onAction(action: FriendListAction) {
        when (action) {
            is FriendListAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is FriendListAction.SendFriendRequest -> sendRequest(action.userId, action.isSend)
            is FriendListAction.OnAcceptFriendRequest -> handleFriendRequest(action.userId, action.isAccepted)
            is FriendListAction.UnFriend -> unfriendUser(action.userId)
            is FriendListAction.OnBlockFriend -> blockFriend(action.userId)
            is FriendListAction.OnUnBlockFriend -> unblockFriend(action.userId)
            else -> {}
        }
    }

    private suspend fun loadUsersForRelationships(relations: Map<String, RelationInfo>) {
        try {
            _state.update { it.copy( isLoading = true ) }
            Log.d("FriendListViewModel", "relations: " + relations.count().toString())

            val users = relationshipService.getUserFromRelationship(relations)
            Log.d("FriendListViewModel", "users: " + users.count().toString())

            _state.update {
                it.copy(
                    users = users,
                    filteredUsers = if (it.searchQuery.isBlank()) {
                        users
                    } else {
                        (users).filter { user ->
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
                    relationshipService.sendFriendRequest(userId)
                } else {
                    relationshipService.cancelFriendRequest(userId)
                }
                
                if (!success) {
                    _state.update { it.copy(error = "Failed to ${if (isSend) "send" else "cancel"} friend request") }
                } else {
                    // Reload data after successful action
                    loadFriendsData()
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
                    relationshipService.acceptFriendRequest(userId)
                } else {
                    relationshipService.declineFriendRequest(userId)
                }
                
                if (!success) {
                    _state.update {
                        it.copy(error = "Failed to ${if (isAccepted) "accept" else "decline"} friend request")
                    }
                } else {
                    // Reload data after successful action
                    loadFriendsData()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun blockFriend(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipService.blockUser(userId)
                if (!success) {
                    _state.update { it.copy(error = "Failed to block user") }
                } else {
                    // Reload data after successful action
                    loadFriendsData()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun unblockFriend(userId: String) {
        viewModelScope.launch {
            try {
                val success = relationshipService.unblockUser(userId)
                if (!success) {
                    _state.update { it.copy(error = "Failed to unblock user") }
                } else {
                    // Reload data after successful action
                    loadFriendsData()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error: ${e.message}") }
            }
        }
    }

    private fun unfriendUser(userId: String) {
        viewModelScope.launch {
            try {
                Log.d("FriendListViewModel", "Unfriend user: $userId")
                val success = relationshipService.cancelFriendRequest(userId)
                if (!success) {
                    _state.update { it.copy(error = "Failed to unfriend user") }
                } else {
                    // Reload data after successful action
                    loadFriendsData()
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error unfriending user: ${e.message}") }
            }
        }
    }
}
