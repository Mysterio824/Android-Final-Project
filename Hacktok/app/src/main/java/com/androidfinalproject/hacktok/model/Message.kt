package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import com.androidfinalproject.hacktok.utils.MessageEncryptionUtil
import java.util.Date
import com.google.firebase.firestore.Exclude

data class Message(
    @PropertyName("id") val id: String? = null,
    @PropertyName("senderId") val senderId: String,
    @PropertyName("encryptedContent") val encryptedContent: String,
    @PropertyName("createdAt") val createdAt: Date = Date(),
    @PropertyName("isRead") val isRead: Boolean = false,
    @PropertyName("media") val media: Media? = null,
    @PropertyName("isDeleted") val isDeleted: Boolean = false,
    @PropertyName("replyTo") val replyTo: String? = null
) {
    // Property that will not be stored in Firestore
    @get:Exclude
    private var decryptedContent: String? = null

    // Make default constructor private to prevent creating invalid messages
    private constructor() : this(null, "", "", Date(), false, null, false, null)

    // Get decrypted content (lazy decryption)
    @get:Exclude
    val content: String
        get() {
            if (decryptedContent == null) {
                try {
                    decryptedContent = MessageEncryptionUtil.decrypt(encryptedContent)
                } catch (e: Exception) {
                    // If decryption fails, return a placeholder message
                    decryptedContent = "[Message could not be decrypted]"
                }
            }
            return decryptedContent!!
        }

    // Create a new message with encrypted content
    companion object {
        fun create(
            id: String? = null,
            senderId: String,
            content: String,
            createdAt: Date = Date(),
            isRead: Boolean = false,
            media: Media? = null,
            isDeleted: Boolean = false,
            replyTo: String? = null
        ): Message {
            // Validate input
            if (senderId.isBlank()) {
                throw IllegalArgumentException("Sender ID cannot be empty")
            }
            if (content.isBlank()) {
                throw IllegalArgumentException("Message content cannot be empty")
            }

            val encryptedContent = MessageEncryptionUtil.encrypt(content)
            return Message(
                id = id,
                senderId = senderId,
                encryptedContent = encryptedContent,
                createdAt = createdAt,
                isRead = isRead,
                media = media,
                isDeleted = isDeleted,
                replyTo = replyTo
            )
        }
    }

    // Convert to map for Firestore, excluding transient fields
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "encryptedContent" to encryptedContent,
            "createdAt" to createdAt,
            "isRead" to isRead,
            "media" to media,
            "isDeleted" to isDeleted,
            "replyTo" to replyTo
        )
    }

    override fun toString(): String {
        return "Message(id=$id, senderId='$senderId', content='${content.take(20)}...', " +
                "createdAt=$createdAt, isRead=$isRead, isDeleted=$isDeleted)"
    }
}