package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.SavedPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SavedPostRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("savedPosts")

    // Lưu bài đăng
    suspend fun savePost(savedPost: SavedPost): String {
        val documentRef = collection.add(savedPost).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy bài đăng đã lưu theo ID
    suspend fun getSavedPost(savedPostId: String): SavedPost? {
        val snapshot = collection.document(savedPostId).get().await()
        return snapshot.toObject(SavedPost::class.java)
    }

    // Lấy danh sách bài đăng đã lưu của một người dùng
    suspend fun getSavedPostsByUser(userId: String): List<SavedPost> {
        val snapshot = collection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(SavedPost::class.java)
    }

    // Xóa bài đăng đã lưu
    suspend fun deleteSavedPost(postId: String) {
        val snapshot = collection.whereEqualTo("postId", postId).get().await()
        if (!snapshot.isEmpty) {
            snapshot.documents.forEach { doc ->
                collection.document(doc.id).delete().await()
            }
        }
    }
}