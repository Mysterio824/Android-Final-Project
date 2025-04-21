package com.androidfinalproject.hacktok.ui.chat

import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User

data class ChatState(
    val chatId: String = "",
    val currentUser: User = User(username = "user1", email = "user1@example.com"),
    val otherUser: User = User(username = "user2", email = "user2@example.com"),
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserMuted: Boolean = false,
    val relation: RelationInfo = RelationInfo("")
)