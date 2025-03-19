package com.androidfinalproject.hacktok.ui.search

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class SearchUiState(
    val users: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val filteredPosts: List<Post> = emptyList(),
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0
)