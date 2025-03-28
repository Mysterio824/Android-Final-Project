package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Comment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CommentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("comments")

    // Thêm bình luận mới
    suspend fun addComment(comment: Comment): String {
        val documentRef = collection.add(comment).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        // Tăng commentCount trong Post
        db.collection("posts").document(comment.postId)
            .update("commentCount", FieldValue.increment(1)).await()
        return documentRef.id
    }

    // Lấy bình luận theo ID
    suspend fun getComment(commentId: String): Comment? {
        val snapshot = collection.document(commentId).get().await()
        return snapshot.toObject(Comment::class.java)
    }

    // Lấy danh sách bình luận của một bài đăng
    suspend fun getCommentsByPost(postId: String): List<Comment> {
        val snapshot = collection.whereEqualTo("postId", postId).get().await()
        return snapshot.toObjects(Comment::class.java)
    }

    // Cập nhật bình luận
    suspend fun updateComment(commentId: String, updates: Map<String, Any>) {
        collection.document(commentId).update(updates).await()
    }

    // Xóa bình luận
    suspend fun deleteComment(commentId: String, postId: String) {
        collection.document(commentId).delete().await()
        db.collection("posts").document(postId)
            .update("commentCount", FieldValue.increment(-1)).await()
    }

    // Tăng lượt thích bình luận
    suspend fun incrementLikeCount(commentId: String) {
        collection.document(commentId).update("likeCount", FieldValue.increment(1)).await()
    }
}