package com.androidfinalproject.hacktok.ui.mainDashboard

import com.androidfinalproject.hacktok.model.Post

data class DashboardState (
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
)