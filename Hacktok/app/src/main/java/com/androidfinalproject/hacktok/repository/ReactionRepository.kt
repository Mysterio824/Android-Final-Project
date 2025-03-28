package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Reaction
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReactionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("reactions")

    // Thêm reaction mới
    suspend fun addReaction(reaction: Reaction): String {
        val documentRef = collection.add(reaction).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        // Tăng likeCount trong Post
        db.collection("posts").document(reaction.postId)
            .update("likeCount", FieldValue.increment(1)).await()
        return documentRef.id
    }

    // Lấy reaction theo ID
    suspend fun getReaction(reactionId: String): Reaction? {
        val snapshot = collection.document(reactionId).get().await()
        return snapshot.toObject(Reaction::class.java)
    }

    // Lấy danh sách reaction của một bài đăng
    suspend fun getReactionsByPost(postId: String): List<Reaction> {
        val snapshot = collection.whereEqualTo("postId", postId).get().await()
        return snapshot.toObjects(Reaction::class.java)
    }

    // Xóa reaction
    suspend fun deleteReaction(reactionId: String, postId: String) {
        collection.document(reactionId).delete().await()
        db.collection("posts").document(postId)
            .update("likeCount", FieldValue.increment(-1)).await()
    }
}