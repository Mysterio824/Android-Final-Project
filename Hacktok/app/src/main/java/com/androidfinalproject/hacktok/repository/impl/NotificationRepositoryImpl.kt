package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Notification
import com.androidfinalproject.hacktok.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : NotificationRepository {
    private val collection = firestore.collection("notifications")
    private val TAG = "NotificationRepository"

    override fun observeNotifications(userId: String): Flow<Result<List<Notification>>> = callbackFlow {
        Log.d(TAG, "Setting up notification listener for user: $userId")
        val query = collection.whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.ASCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening for notification updates for user $userId: ${error.message}", error)
                trySend(Result.failure(error))
                close(error)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                 Log.w(TAG, "Received null snapshot for user $userId notification query.")
                 trySend(Result.success(emptyList()))
                 return@addSnapshotListener
            }

            try {
                val allNotifications = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Notification::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing notification document ${doc.id}: ${e.message}", e)
                        null
                    }
                }
                Log.d(TAG, "Emitting ${allNotifications.size} notifications for user $userId")
                trySend(Result.success(allNotifications))
            } catch (e: Exception) {
                Log.e(TAG, "Error processing notification snapshot for user $userId: ${e.message}", e)
                trySend(Result.failure(e))
            }
        }

        awaitClose { listener.remove() }
    }

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

    override suspend fun getNotificationsByUser(userId: String): List<Notification> {
        val snapshot = collection.whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.ASCENDING).get().await()
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