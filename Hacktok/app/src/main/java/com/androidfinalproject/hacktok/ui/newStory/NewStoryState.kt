package com.androidfinalproject.hacktok.ui.newStory

import android.net.Uri
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

data class NewStoryState(
    val isLoading: Boolean = false,
    val privacySetting: PRIVACY = PRIVACY.PUBLIC,
    val storyType: String = "", // "image" or "text"
    val selectedImageUri: Uri? = null,
    val storyText: String = "",
    val error: String? = null,
    val isStoryCreated: Boolean = false,
    val successMessage: String? = null
)