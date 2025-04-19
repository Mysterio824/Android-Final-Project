package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.User

data class HomeScreenState(
    val posts: List<Post> = emptyList(),
    val stories: List<Story> = emptyList(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null // Added for showing transient messages
)