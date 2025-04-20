package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class HomeScreenState(
    val posts: List<Post> = emptyList(),
    val stories: List<Story> = emptyList(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val showShareDialog: Boolean = false,
    val sharePost: Post? = null,
    val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
    val shareCaption: String = "",
    val error: String? = null,
    val userMessage: String? = null, // Added for showing transient messages
    val isPaginating: Boolean = false,
    val hasMorePosts: Boolean = true,
    val postAuthorNames: Map<String, String> = emptyMap(), // postId -> authorFullName
    val isStoryLoading: Boolean = false,
)