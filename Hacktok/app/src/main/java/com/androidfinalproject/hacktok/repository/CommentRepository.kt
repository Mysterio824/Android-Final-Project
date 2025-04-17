package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getAll(postId: String): Flow<List<Comment>>
    suspend fun add(comment: Comment): Result<Comment>
    suspend fun getById(commentId: String): Result<Comment>
    suspend fun update(commentId: String, comment: Comment): Result<Unit>
    suspend fun delete(commentId: String): Result<Unit>
}