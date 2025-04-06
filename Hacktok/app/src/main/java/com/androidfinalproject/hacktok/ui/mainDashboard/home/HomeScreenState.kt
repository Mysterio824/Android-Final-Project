package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Post

data class HomeScreenState (
    val query: String = "",
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)