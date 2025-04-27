package com.androidfinalproject.hacktok.ui.post

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

data class PostDetailState(
    val post: Post? = null,
    val postUser: User? = null,
    val currentUser: User? = null,
    val comments: List<Comment> = emptyList(),
    val isLoadingComments: Boolean = false,
    val error: String? = null,
    val userMessage: String? = null,
    val showComments: Boolean = false,
    val isCommenting: Boolean = false,
    val commentText: String = "",
    val commentIdReply: String = "",
    val highlightedCommentId: String? = null,
    val showShareDialog: Boolean = false,
    val listLikeUser: List<FullReaction> = emptyList(),
    val referencePost: Post? = null,
    val referenceUser: User? = null,
    val trigger: Boolean = false,
    val savedPosts: List<String> = emptyList()
)