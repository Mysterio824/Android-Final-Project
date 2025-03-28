package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("notifications")

    // Thêm thông báo mới
    suspend fun addNotification(notification: Notification): String {
        val documentRef = collection.add(notification).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy thông báo theo ID
    suspend fun getNotification(notificationId: String): Notification? {
        val snapshot = collection.document(notificationId).get().await()
        return snapshot.toObject(Notification::class.java)
    }

    // Lấy danh sách thông báo của một người dùng
    suspend fun getNotificationsByUser(userId: String): List<Notification> {
        val snapshot = collection.whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        return snapshot.toObjects(Notification::class.java)
    }

    // Đánh dấu thông báo là đã đọc
    suspend fun markAsRead(notificationId: String) {
        collection.document(notificationId).update("isRead", true).await()
    }

    // Xóa thông báo
    suspend fun deleteNotification(notificationId: String) {
        collection.document(notificationId).delete().await()
    }
}