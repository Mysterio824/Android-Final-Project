package com.androidfinalproject.hacktok.ui.friendList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class FriendListViewModel : ViewModel() {
    private val _state = MutableStateFlow(FriendListState())
    val state: StateFlow<FriendListState> = _state.asStateFlow()

    init {
        loadFriends()
    }

    fun onAction(action: FriendListAction) {
        when (action) {
            is FriendListAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is FriendListAction.AddFriend -> addFriend(action.userId)
            is FriendListAction.LoadFriends -> loadFriends()
            else -> {}
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { currentState ->
            val filteredList = if (query.isBlank()) {
                currentState.users
            } else {
                currentState.users.filter {
                    it.username.contains(query, ignoreCase = true) ||
                            it.email.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(searchQuery = query, filteredUsers = filteredList)
        }
    }

    private fun addFriend(userId: ObjectId?) {
        viewModelScope.launch {
            // Implement logic to add friend
            // This is a placeholder for actual implementation
            _state.update { currentState ->
                val updatedFriendIds = currentState.friendIds.toMutableSet()
                updatedFriendIds.add(userId)

                currentState.copy(friendIds = updatedFriendIds)
            }
        }
    }

    private fun loadFriends() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {

                val mockUsers = listOf(
                    User(
                        ObjectId(),
                        "john_doe",
                        "john@example.com"
                    ),
                    User(
                        ObjectId(),
                        "jane_smith",
                        "jane@example.com"
                    ),
                    User(
                        ObjectId(),
                        "robert_johnson",
                        "robert@example.com"
                    )
                )

                val mockFriendIds = setOf(mockUsers[0].id, mockUsers[1].id)

                _state.update {
                    it.copy(
                        users = mockUsers,
                        filteredUsers = mockUsers,
                        friendIds = mockFriendIds,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load friends: ${e.message}"
                    )
                }
            }
        }
    }
}
