package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import com.androidfinalproject.hacktok.model.Post

data class WatchLaterState(
    val savedPosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)