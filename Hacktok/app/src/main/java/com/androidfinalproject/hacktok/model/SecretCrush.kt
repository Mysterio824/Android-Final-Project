package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class SecretCrush(
    @PropertyName("id") val id: String = "",
    @PropertyName("senderId") val senderId: String = "",
    @PropertyName("senderName") val senderName: String = "",
    @PropertyName("senderImageUrl") val senderImageUrl: String = "",
    @PropertyName("receiverId") val receiverId: String = "",
    @PropertyName("receiverName") val receiverName: String = "",
    @PropertyName("receiverImageUrl") val receiverImageUrl: String = "",
    @PropertyName("revealed") val revealed: Boolean = false,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("revealedAt") val revealedAt: Date? = null
) {
    // No-argument constructor required by Firestore
    constructor() : this(
        id = "",
        senderId = "",
        senderName = "",
        senderImageUrl = "",
        receiverId = "",
        receiverName = "",
        receiverImageUrl = "",
        revealed = false,
        createdAt = Date(),
        revealedAt = null
    )
}