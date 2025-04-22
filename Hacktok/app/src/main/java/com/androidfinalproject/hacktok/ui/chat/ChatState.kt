package com.androidfinalproject.hacktok.ui.chat

import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User

data class ChatState(
    val chatId: String = "",
    val currentUser: User? = null,
    val otherUser: User? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUserMuted: Boolean = false,
    val relation: RelationInfo = RelationInfo(""),

    val isSearchMode: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Message> = emptyList(),
    val currentSearchIndex: Int = -1
)