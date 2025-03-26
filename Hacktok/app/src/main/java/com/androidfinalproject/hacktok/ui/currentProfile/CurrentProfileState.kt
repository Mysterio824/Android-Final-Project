package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class CurrentProfileState (
    val user: User,
    val posts: List<Post> = emptyList(),
    val friendCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)