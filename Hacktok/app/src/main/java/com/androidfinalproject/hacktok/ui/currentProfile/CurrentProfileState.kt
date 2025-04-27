package com.androidfinalproject.hacktok.ui.currentProfile

import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class CurrentProfileState {
    data object Loading : CurrentProfileState()
    data class Error(val message: String) : CurrentProfileState()
    data class Success(
        val user: User = User(),
        val posts: List<Post> = emptyList(),
        val friendCount: Int = 0,
        val showShareDialog: Boolean = false,
        val postToShare: Post? = null,
        val sharePrivacy: PRIVACY = PRIVACY.PUBLIC,
        val shareCaption: String = "",
        val listLikeUser: List<FullReaction> = emptyList()
    ) : CurrentProfileState()
}