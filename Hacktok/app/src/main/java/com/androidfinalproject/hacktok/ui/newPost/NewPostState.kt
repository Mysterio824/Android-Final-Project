package com.androidfinalproject.hacktok.ui.newPost

import android.net.Uri

enum class PRIVACY {
    PUBLIC,
    FRIENDS,
    PRIVATE
}

data class NewPostState (
    val caption: String = "",
    val imageUri: Uri? = null,
    val privacy: PRIVACY = PRIVACY.PUBLIC,
    val isPosting: Boolean = false,
    val username: String = "",
    val postSubmitted: Boolean = false
)