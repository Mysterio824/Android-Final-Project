package com.androidfinalproject.hacktok.ui.messageDashboard.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardAction

@Composable
fun ChatList(
    friendList: List<User>,
    menuItems: List<Pair<String, () -> Unit>>,
    onAction: (MessageDashboardAction) -> Unit,
) {
    val chatList = listOf(
        ChatItem("Dani Alves", "You: What's man!", "9:40 AM"),
        ChatItem("Joshua Filler", "You: Ok, thanks!", "9:25 AM"),
        ChatItem("Martin Luther King", "You: See you in Toronto", "Fri"),
        ChatItem("Karen S", "Have a good day, Maisy!", "Fri"),
        ChatItem("Luther Vandross", "Let's go bowling!", "Wed")
    )

    LazyColumn {
        items(chatList) { chat ->
            ChatRow(chat, menuItems)
        }
    }
}

data class ChatItem(val name: String, val lastMessage: String, val time: String)