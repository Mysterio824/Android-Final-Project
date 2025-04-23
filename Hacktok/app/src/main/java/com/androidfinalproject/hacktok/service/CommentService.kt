package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserSnapshot
import kotlinx.coroutines.flow.Flow

interface CommentService {
    fun getCommentsForPost(postId: String): Flow<List<Comment>>

    fun observeCommentsForPost(
        postId: String,
        parentCommentId: String? = null,
        sortAscending: Boolean = false
    ): Flow<Result<List<Comment>>>

    suspend fun getComment(commentId: String) : Comment?
    suspend fun addComment(content:String, postId: String): Result<Comment>
    suspend fun replyComment(content:String, userId: String): Result<Comment>
    suspend fun deleteComment(commentId: String): Result<Unit>
    suspend fun updateUserSnapshots(commentIds: List<String>, newSnapshots: Map<String, UserSnapshot>): Result<Unit>
}