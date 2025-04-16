package com.androidfinalproject.hacktok.repository.impl

import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {
    private val collection = firestore.collection("notifications")

    // Thêm thông báo mới
    override suspend fun addNotification(notification: Notification): String {
        val documentRef = collection.add(notification).await()
        collection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    // Lấy thông báo theo ID
    override suspend fun getNotification(notificationId: String): Notification? {
        val snapshot = collection.document(notificationId).get().await()
        return snapshot.toObject(Notification::class.java)
    }

    // Lấy danh sách thông báo của một người dùng
    override suspend fun getNotificationsByUser(userId: String): List<Notification> {
        val snapshot = collection.whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING).get().await()
        return snapshot.toObjects(Notification::class.java)
    }

    // Đánh dấu thông báo là đã đọc
    override suspend fun markAsRead(notificationId: String) {
        collection.document(notificationId).update("isRead", true).await()
    }

    // Xóa thông báo
    override suspend fun deleteNotification(notificationId: String) {
        collection.document(notificationId).delete().await()
    }
}