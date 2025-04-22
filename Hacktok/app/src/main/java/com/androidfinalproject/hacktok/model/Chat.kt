package com.androidfinalproject.hacktok.model

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
    @PropertyName("isMutedByUser1") val isMutedByUser1: Boolean = false,
    @PropertyName("isMutedByUser2") val isMutedByUser2: Boolean = false
) {
    constructor() : this(null, emptyList(), "", Date(), 0, 0, false, null, emptyList())

    override fun toString(): String {
        return "Chat(id=$id, participants=$participants, lastMessage='$lastMessage', " +
                "lastMessageAt=$lastMessageAt, isGroup=$isGroup)"
    }
}