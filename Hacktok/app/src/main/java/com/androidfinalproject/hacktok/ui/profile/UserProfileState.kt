package com.androidfinalproject.hacktok.ui.profile

import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class UserProfileState (
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val postUsers: Map<String, User> = emptyMap(),
    val relationshipInfo: RelationInfo? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val userIdBeingLoaded: String? = null,
    val numberOfFriends: Int = 0,
    val userMessage: String? = null,
    val sharePost: Post? = null,
    val showShareDialog: Boolean = false,
    val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
    val shareCaption: String = "",
    val currentUser: User? = null,
    val listLikeUser: List<FullReaction> = emptyList(),
    val referencePosts: Map<String, Post> = emptyMap(),
    val referenceUsers: Map<String, User> = emptyMap()
)