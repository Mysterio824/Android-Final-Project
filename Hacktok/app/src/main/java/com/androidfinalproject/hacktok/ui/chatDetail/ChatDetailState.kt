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

    // Thông tin nhóm
    val group: Group = Group(
        id = "group1",
        groupName = "Nhóm",
        description = null,
        creatorId = "user1",
        members = listOf("user1", "user2", "user3"),
        admins = listOf("user1"),
        isPublic = true,
        createdAt = Date(),
        coverImage = null
    ),
    val membersList: List<User> = listOf(
        User(id = "user1", username = "user1", email = "user1@example.com"),
        User(id = "user2", username = "user2", email = "user2@example.com"),
        User(id = "user3", username = "user3", email = "user3@example.com")
    ),

    // Thông tin chat cá nhân
    val otherUser: User? = null,

    // Biến xác định là chat nhóm hay chat cá nhân
    val isGroup: Boolean = false
)