package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.utils.MessageEncryptionUtil
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Chat(
    @PropertyName("id") val id: String? = null,
    @PropertyName("participants") val participants: List<String>,
    @PropertyName("lastMessage") val lastMessage: String,
    @PropertyName("lastMessageAt") val lastMessageAt: Date = Date(),
    @PropertyName("unreadCountUser1") val unreadCountUser1: Int = 0,
    @PropertyName("unreadCountUser2") val unreadCountUser2: Int = 0,
    @PropertyName("isGroup") val isGroup: Boolean = false,
    @PropertyName("groupName") val groupName: String? = null,
    @PropertyName("groupAdmins") val groupAdmins: List<String> = emptyList(),
    @PropertyName("mutedByUser1") val isMutedByUser1: Boolean = false,
    @PropertyName("mutedByUser2") val isMutedByUser2: Boolean = false
) {
    constructor() : this(null, emptyList(), "", Date(), 0, 0, false, null, emptyList())

    // Get decrypted last message
    val decryptedLastMessage: String
        get() {
            return try {
                if (lastMessage.isBlank()) return ""
                if (lastMessage == "[Message could not be decrypted]") return lastMessage
                MessageEncryptionUtil.decrypt(lastMessage)
            } catch (e: Exception) {
                "[Message could not be decrypted]"
            }
        }

    override fun toString(): String {
        return "Chat(id=$id, participants=$participants, lastMessage='${decryptedLastMessage.take(20)}...', " +
                "lastMessageAt=$lastMessageAt, isGroup=$isGroup)"
    }
}