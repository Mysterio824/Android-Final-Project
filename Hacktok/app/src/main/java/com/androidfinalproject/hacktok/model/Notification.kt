package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Notification(
    @PropertyName("id") val id: String? = null,
    @PropertyName("userId") val userId: String,
    @PropertyName("type") val type: NotificationType,
    @PropertyName("senderId") val senderId: String? = null,
    @PropertyName("senderName") val senderName: String? = null,
    @PropertyName("senderImage") val senderImage: String? = null,
    @PropertyName("relatedId") val relatedId: String? = null, // postId, commentId, etc.
    @PropertyName("content") val content: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("actionUrl") val actionUrl: String? = null,
    @PropertyName("priority") val priority: String = "normal" // "high", "normal"
) {
    constructor() : this(null, "", NotificationType.ADMIN_NOTIFICATION,
        null, null, null, null, "")

    override fun toString(): String {
        return "Notification(id=$id, userId='$userId', type='$type', content='$content', " +
                "createdAt=$createdAt, isRead=$isRead)"
    }
}