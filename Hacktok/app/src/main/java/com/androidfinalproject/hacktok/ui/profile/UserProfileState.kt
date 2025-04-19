package com.androidfinalproject.hacktok.ui.profile

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class UserProfileState (
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val relationshipInfo: RelationInfo? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val userIdBeingLoaded: String? = null,
    val numberOfFriends: Int = 0,
    val isOwner: Boolean = false,
    val userMessage: String? = null,
    val sharePost: Post? = null,
    val showShareDialog: Boolean = false,
    val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
    val shareCaption: String = "",
)