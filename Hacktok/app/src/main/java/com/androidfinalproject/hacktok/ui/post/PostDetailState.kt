package com.androidfinalproject.hacktok.ui.post

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.Post


data class PostDetailState(
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val isLoadingComments: Boolean = false,
    val isCommentsVisible: Boolean = false,
    val commentText: String = "",
    val isShare: Boolean = false,
    val isKeyboardVisible: Boolean = false,
    val error: String? = null
)