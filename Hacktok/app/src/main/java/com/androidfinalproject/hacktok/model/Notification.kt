package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Notification(
    @PropertyName("id") val id: String? = null,
    @PropertyName("userId") val userId: String,
    @PropertyName("type") val type: String, // "new_message", "new_post", "new_comment"
    @PropertyName("relatedId") val relatedId: String,
    @PropertyName("content") val content: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("actionUrl") val actionUrl: String? = null,
    @PropertyName("priority") val priority: String = "normal" // "high", "normal"
) {
    constructor() : this(null, "", "", "", "", Date(), false, null, "normal")

    override fun toString(): String {
        return "Notification(id=$id, userId='$userId', type='$type', content='$content', " +
                "createdAt=$createdAt, isRead=$isRead)"
    }
}