package com.androidfinalproject.hacktok.ui.groupChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.groupChat.components.GroupChatBubble
import com.androidfinalproject.hacktok.ui.groupChat.components.GroupChatTopBar
import com.androidfinalproject.hacktok.ui.groupChat.components.MessageInput
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.util.*

@Composable
fun GroupChatScreen(
    state: GroupChatState,
    onAction: (GroupChatAction) -> Unit,
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(state.messages) {
        onAction(GroupChatAction.LoadInitialMessages)
    }

    Scaffold(
        topBar = {
            GroupChatTopBar(
                group = state.group,
                onBackClick = { onAction(GroupChatAction.NavigateBack) },
                onInfoClick = { onAction(GroupChatAction.NavigateToManageGroup(state.group.id)) }
            )
        },
        bottomBar = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                MessageInput(
                    text = messageText,
                    onTextChanged = { messageText = it },
                    onSendClicked = {
                        if (messageText.text.isNotEmpty()) {
                            onAction(GroupChatAction.SendMessage(messageText.text))
                            messageText = TextFieldValue("")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(state.messages.sortedByDescending { it.createdAt }) { message ->
                val sender = state.membersList.find { it.id == message.senderId }
                GroupChatBubble(
                    message = message,
                    senderName = sender?.username ?: "Unknown",
                    isCurrentUser = message.senderId == state.currentUser.id,
                    onDeleteMessage = { onAction(GroupChatAction.DeleteMessage(message.id)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupChatScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            // Sample data for preview
            val group = Group(
                id = "group1",
                groupName = "Nhóm Dự Án Android",
                creatorId = "user1",
                members = listOf("user1", "user2", "user3"),
                admins = listOf("user1"),
                isPublic = true,
                createdAt = Date()
            )

            val currentUser = User(id = "user1", username = "user1", email = "user1@example.com")
            val membersList = listOf(
                currentUser,
                User(id = "user2", username = "user2", email = "user2@example.com"),
                User(id = "user3", username = "user3", email = "user3@example.com")
            )

            val messages = listOf(
                Message(
                    id = "1",
                    senderId = "user2",
                    content = "Chào cả nhóm!",
                    createdAt = Date(System.currentTimeMillis() - 3600000)
                ),
                Message(
                    id = "2",
                    senderId = "user1",
                    content = "Chào mọi người, dự án của chúng ta tiến triển thế nào rồi?",
                    createdAt = Date(System.currentTimeMillis() - 3500000)
                ),
                Message(
                    id = "3",
                    senderId = "user3",
                    content = "Mình đã hoàn thành phần UI, đang chờ API.",
                    createdAt = Date(System.currentTimeMillis() - 3400000)
                )
            )

            val groupState = GroupChatState(
                currentUser = currentUser,
                group = group,
                messages = messages,
                membersList = membersList
            )

            GroupChatScreen(
                state = groupState,
                onAction = {}
            )
        }
    }
}
