package com.androidfinalproject.hacktok.ui.mainDashboard.home

import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class HomeScreenState(
    val posts: List<Post> = emptyList(),
    val postUsers: Map<String, User> = emptyMap(),
    val stories: List<Story> = emptyList(),
    val user: User? = null,
    val isLoading: Boolean = false,
    val showShareDialog: Boolean = false,
    val sharePost: Post? = null,
    val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
    val shareCaption: String = "",
    val error: String? = null,
    val userMessage: String? = null,
    val isPaginating: Boolean = false,
    val hasMorePosts: Boolean = true,
    val postAuthorNames: Map<String, String> = emptyMap(),
    val isStoryLoading: Boolean = false,
    val listLikeUser: List<FullReaction> = emptyList(),
    val referencePosts: Map<String, Post> = emptyMap(),
    val referenceUsers: Map<String, User> = emptyMap(),
    val currentAd: Ad? = null,
    val savedPosts: List<String> = emptyList()
)