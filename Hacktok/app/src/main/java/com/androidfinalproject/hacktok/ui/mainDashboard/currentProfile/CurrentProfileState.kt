package com.androidfinalproject.hacktok.ui.mainDashboard.currentProfile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class CurrentProfileState (
    var user: User? = null,
    val posts: List<Post> = emptyList(),
    val friendCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)