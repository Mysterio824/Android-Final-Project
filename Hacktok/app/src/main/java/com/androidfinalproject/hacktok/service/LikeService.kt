package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post

interface LikeService {
    suspend fun likePost(postId: String, emoji: String): Post?
    suspend fun unlikePost(postId: String): Post?
    suspend fun likeComment(commentId: String, emoji: String): Result<Unit>
    suspend fun unlikeComment(commentId: String): Result<Unit>
    suspend fun getPostLike(postId: String) : List<FullReaction>
    suspend fun getCommentLike(commentId: String) : List<FullReaction>
}