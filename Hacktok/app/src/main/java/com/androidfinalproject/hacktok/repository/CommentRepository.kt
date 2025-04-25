package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getAll(postId: String): Flow<List<Comment>>
    /**
     * Observes comments for a specific post with real-time updates
     * 
     * @param postId The ID of the post to observe comments for
     * @param parentCommentId Optional parent comment ID for filtering replies
     * @param limit Maximum number of comments to return (for pagination)
     * @param sortAscending Whether to sort comments by creation time in ascending order
     * @return A Flow that emits updated comment lists whenever there are changes
     */
    fun observeCommentsForPost(
        postId: String,
        parentCommentId: String? = null,
        sortAscending: Boolean = false
    ): Flow<Result<List<Comment>>>

    suspend fun updateSnapshot(userId: String, user: User): Boolean
    suspend fun add(comment: Comment): Result<Comment>
    suspend fun getById(commentId: String): Result<Comment>
    suspend fun update(commentId: String, comment: Comment): Result<Unit>
    suspend fun delete(commentId: String): Result<Unit>
}