package com.androidfinalproject.hacktok.ui.profile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User

data class UserProfileState (
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val relationshipInfo: RelationInfo? = null,
    val currentUserId: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val userIdBeingLoaded: String? = null
)