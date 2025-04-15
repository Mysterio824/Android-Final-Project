package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

sealed class CurrentProfileState {
    data object Loading : CurrentProfileState()
    data class Error(val message: String) : CurrentProfileState()
    data class Success(
        val user: User = User(),
        val posts: List<Post> = emptyList(),
        val friendCount: Int = 0
    ) : CurrentProfileState()
}