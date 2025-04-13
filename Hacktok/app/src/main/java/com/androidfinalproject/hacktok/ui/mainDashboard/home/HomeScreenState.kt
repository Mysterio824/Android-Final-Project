package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class HomeScreenState(
    val user: User? = null,
    val query: String = "",
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0,
    val notificationCount: Int = 3,
    val messageCount: Int = 2,
    val newStoriesCount: Int = 4,
    val userProfilePicUrl: String = ""
)