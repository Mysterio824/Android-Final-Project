package com.androidfinalproject.hacktok.ui.friendList

import com.androidfinalproject.hacktok.model.User
import org.bson.types.ObjectId

// UI State
data class FriendListState(
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val friendIds: Set<String?> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
