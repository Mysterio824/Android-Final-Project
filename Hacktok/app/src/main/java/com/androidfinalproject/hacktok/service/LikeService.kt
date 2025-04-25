package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User

interface LikeService {
    suspend fun likePost(postId: String): Post?
    suspend fun unlikePost(postId: String): Post?
    suspend fun likeComment(commentId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String): Result<Unit>
    suspend fun getPostLike(postId: String) : List<User>
    suspend fun getCommentLike(commentId: String) : List<User>
}