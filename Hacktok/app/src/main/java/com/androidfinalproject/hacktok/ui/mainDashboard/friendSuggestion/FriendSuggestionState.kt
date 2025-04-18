package com.androidfinalproject.hacktok.ui.mainDashboard.friendSuggestion

import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User

data class FriendSuggestionState (
    val user: User? = null,
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val relations: Map<String, RelationInfo> = emptyMap()
)