package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class WatchLaterState(
    val currentUserId: String? = null,
    val postUsers: Map<String, User> = emptyMap(),
    val savedPosts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val listLikeUser: List<FullReaction> = emptyList(),
    val userMessage: String? = null,
    val referencePosts: Map<String, Post> = emptyMap(),
    val referenceUsers: Map<String, User> = emptyMap(),
    val showShareDialog: Boolean = false,
    val sharePost: Post? = null,
    val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
    val shareCaption: String = "",
    val user: User? = null
)