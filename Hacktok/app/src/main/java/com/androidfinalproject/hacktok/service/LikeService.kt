package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Post

interface LikeService {
    suspend fun likePost(postId: String): Post?
    suspend fun unlikePost(postId: String): Post?
    suspend fun likeComment(commentId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String): Result<Unit>
}