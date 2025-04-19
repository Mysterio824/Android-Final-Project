package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserSnapshot
import kotlinx.coroutines.flow.Flow

interface CommentService {
    fun getCommentsForPost(postId: String): Flow<List<Comment>>
    
    /**
     * Observes comments for a specific post with real-time updates and better error handling
     * 
     * @param postId The ID of the post to observe comments for
     * @param parentCommentId Optional parent comment ID for filtering replies
     * @param limit Maximum number of comments to return (for pagination)
     * @param sortAscending Whether to sort comments by creation time in ascending order
     * @return A Flow that emits Result<List<Comment>> with success or failure state
     */
    fun observeCommentsForPost(
        postId: String,
        parentCommentId: String? = null,
        limit: Int = 50,
        sortAscending: Boolean = false
    ): Flow<Result<List<Comment>>>
    
    suspend fun addComment(content:String, postId: String): Result<Comment>
    suspend fun replyComment(content:String, userId: String): Result<Comment>
    suspend fun likeComment(commentId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
    suspend fun updateUserSnapshots(commentIds: List<String>, newSnapshots: Map<String, UserSnapshot>): Result<Unit>
}