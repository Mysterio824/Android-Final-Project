package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User

data class MessageDashboardState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: MutableState<String> = mutableStateOf(""),
    val currentUserId: String = "",
    val chatList: List<ChatItem> = emptyList(),
    val filterChatList: List<ChatItem> = emptyList()
)

data class ChatItem(
    val user: User,
    val chat: Chat,
    val relationInfo: RelationInfo
)