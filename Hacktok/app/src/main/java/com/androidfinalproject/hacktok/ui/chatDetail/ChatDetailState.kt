package com.androidfinalproject.hacktok.ui.chatDetail

import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.User
import java.util.Date

data class ChatDetailState(
    val currentUser: User = User(),
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserMuted: Boolean = false,

    val group: Group = Group(),
    val membersList: List<User> = listOf(),

    val otherUser: User? = null,

    val isGroup: Boolean = false
)