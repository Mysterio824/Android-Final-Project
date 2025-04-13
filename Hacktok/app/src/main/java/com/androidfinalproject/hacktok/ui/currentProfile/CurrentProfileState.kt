package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class CurrentProfileState (
    var user: User = MockData.mockUsers.first(),
    val posts: List<Post> = emptyList(),
    val friendCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)