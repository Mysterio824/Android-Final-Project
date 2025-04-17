package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserSnapshot
import kotlinx.coroutines.flow.Flow

interface CommentService {
    fun getCommentsForPost(postId: String): Flow<List<Comment>>
    suspend fun addComment(content:String, postId: String): Result<Comment>
    suspend fun replyComment(content:String, parentId: String): Result<Comment>
    suspend fun likeComment(commentId: String, userId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String, userId: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
    suspend fun updateUserSnapshots(commentIds: List<String>, newSnapshots: Map<String, UserSnapshot>): Result<Unit>
}