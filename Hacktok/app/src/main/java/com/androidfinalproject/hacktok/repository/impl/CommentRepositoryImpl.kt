package com.androidfinalproject.hacktok.repository.impl

import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {
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