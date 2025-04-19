package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {
    private val TAG = "CommentRepositoryImpl"
    private val commentsCollection = firestore.collection("comments")

    override fun getAll(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = commentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents?.mapNotNull {
                    it.toObject(Comment::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }
    
    override fun observeCommentsForPost(
        postId: String,
        parentCommentId: String?,
        limit: Int,
        sortAscending: Boolean
    ): Flow<Result<List<Comment>>> = callbackFlow {
        try {
            Log.d(TAG, "Starting observeCommentsForPost for postId: $postId, parentCommentId: $parentCommentId")
            
            // Create the query differently based on whether we need top-level comments or replies
            val query = commentsCollection
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
            
            // Apply limit if needed
            val limitedQuery = if (limit > 0) {
                query.limit(limit.toLong())
            } else {
                query
            }
            
            // Add the snapshot listener for real-time updates
            val listener = limitedQuery.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing comments: ${error.message}", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                
                try {
                    // Extract comments from snapshot
                    val allComments = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            val comment = doc.toObject(Comment::class.java)
                            if (comment != null) {
                                // Log the parent ID for debugging
                                Log.d(TAG, "Comment ${doc.id} has parentId: ${comment.parentCommentId}")
                                comment.copy(id = doc.id)
                            } else {
                                Log.w(TAG, "Failed to convert document to Comment: ${doc.id}")
                                null
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting document to Comment: ${doc.id}", e)
                            null
                        }
                    } ?: emptyList()
                    
                    // Apply additional filtering for top-level comments (if needed)
                    val comments = allComments
                    // Filter out deleted comments
                    val filteredComments = comments.filter { !it.isDeleted }
                    
                    // If sortAscending is false, reverse the list (since we always query in ascending order)
                    val sortedComments = if (!sortAscending) {
                        filteredComments.reversed()
                    } else {
                        filteredComments
                    }
                    
                    trySend(Result.success(sortedComments))
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing comment snapshot", e)
                    trySend(Result.failure(e))
                }
            }
            
            // Clean up listener when flow is cancelled
            awaitClose { 
                Log.d(TAG, "Removing comment snapshot listener for postId: $postId")
                listener.remove() 
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up comment observer", e)
            trySend(Result.failure(e))
            close(e)
        }
    }

    override suspend fun add(comment: Comment): Result<Comment> = runCatching {
        val doc = commentsCollection.document()
        val commentWithId = comment.copy(id = doc.id)
        doc.set(commentWithId).await()
        commentWithId
    }

    override suspend fun getById(commentId: String): Result<Comment> = runCatching {
        val snapshot = commentsCollection.document(commentId).get().await()
        snapshot.toObject(Comment::class.java)?.copy(id = snapshot.id)
            ?: throw IllegalArgumentException("Comment not found")
    }

    override suspend fun update(commentId: String, comment: Comment): Result<Unit> = runCatching {
        commentsCollection.document(commentId).set(comment).await()
    }

    override suspend fun delete(commentId: String): Result<Unit> = runCatching {
        commentsCollection.document(commentId).delete().await()
    }
}