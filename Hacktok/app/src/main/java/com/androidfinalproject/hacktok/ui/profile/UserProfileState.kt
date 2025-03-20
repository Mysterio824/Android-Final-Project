package com.androidfinalproject.hacktok.ui.profile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class UserProfileState (
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val isFriend: Boolean = false,
    val isBlocked: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)