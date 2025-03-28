package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")

    // Thêm người dùng mới
    suspend fun addUser(user: User): String {
        val documentRef = collection.add(user).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy người dùng theo ID
    suspend fun getUser(userId: String): User? {
        val snapshot = collection.document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    // Cập nhật thông tin người dùng
    suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        collection.document(userId).update(updates).await()
    }

    // Xóa người dùng
    suspend fun deleteUser(userId: String) {
        collection.document(userId).delete().await()
    }

    // Thêm bạn bè
    suspend fun addFriend(userId: String, friendId: String) {
        collection.document(userId)
            .update("friends", FieldValue.arrayUnion(friendId)).await()
    }

    // Xóa bạn bè
    suspend fun removeFriend(userId: String, friendId: String) {
        collection.document(userId)
            .update("friends", FieldValue.arrayRemove(friendId)).await()
    }
}