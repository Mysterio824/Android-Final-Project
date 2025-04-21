package com.androidfinalproject.hacktok.ui.messageDashboard.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.messageDashboard.ChatItem
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardAction

@Composable
fun ChatList(
    chatList: List<ChatItem>,
    onOptionClick: (ChatItem) -> Unit,
    onAction: (MessageDashboardAction) -> Unit,
) {
    LazyColumn {
        items(chatList) { entry ->
            ChatRow(
                user = entry.user,
                chat = entry.chat,
                status = entry.relationInfo.status,
                onClick = { onAction(MessageDashboardAction.GoToChat(entry.user.id)) },
                onOptionClick = { onOptionClick(entry) }
            )
        }
    }
}