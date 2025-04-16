package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Message(
    @PropertyName("id") val id: String? = null,
    @PropertyName("senderId") val senderId: String,
    @PropertyName("content") val content: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("media") val media: Media? = null,
    @PropertyName("isDeleted") val isDeleted: Boolean = false,
    @PropertyName("replyTo") val replyTo: String? = null
) {
    constructor() : this(null, "", "", Date(), false, null, false, null)

    override fun toString(): String {
        return "Message(id=$id, senderId='$senderId', content='$content', createdAt=$createdAt, " +
                "isRead=$isRead, isDeleted=$isDeleted)"
    }
}