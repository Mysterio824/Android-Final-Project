package com.androidfinalproject.hacktok.ui.friendList

import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import org.bson.types.ObjectId

// UI State
data class FriendListState(
    val users: List<User> = emptyList(),
    val currentUserId: String = "",
    val filteredUsers: List<User> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val relations: Map<String, RelationInfo> = emptyMap()
)
