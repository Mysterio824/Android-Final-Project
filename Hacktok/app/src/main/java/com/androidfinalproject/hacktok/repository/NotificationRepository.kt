package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

interface NotificationRepository {
    // Thêm thông báo mới
    suspend fun addNotification(notification: Notification): String

    // Lấy thông báo theo ID
    suspend fun getNotification(notificationId: String): Notification?

    // Lấy danh sách thông báo của một người dùng
    suspend fun getNotificationsByUser(userId: String): List<Notification>

    // Đánh dấu thông báo là đã đọc
    suspend fun markAsRead(notificationId: String)

    // Xóa thông báo
    suspend fun deleteNotification(notificationId: String)

    fun observeNotifications(userId: String): Flow<List<Notification>>
}