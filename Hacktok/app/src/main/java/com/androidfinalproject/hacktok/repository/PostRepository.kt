package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("posts")

    // Thêm bài đăng mới
    suspend fun addPost(post: Post): String {
        val documentRef = collection.add(post).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy bài đăng theo ID
    suspend fun getPost(postId: String): Post? {
        val snapshot = collection.document(postId).get().await()
        return snapshot.toObject(Post::class.java)
    }

    // Lấy danh sách bài đăng của một người dùng
    suspend fun getPostsByUser(userId: String): List<Post> {
        val snapshot = collection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Post::class.java)
    }

    // Cập nhật bài đăng
    suspend fun updatePost(postId: String, updates: Map<String, Any>) {
        collection.document(postId).update(updates).await()
    }

    // Xóa bài đăng
    suspend fun deletePost(postId: String) {
        collection.document(postId).delete().await()
    }

    // Tăng lượt thích
    suspend fun incrementLikeCount(postId: String) {
        collection.document(postId).update("likeCount", FieldValue.increment(1)).await()
    }
}