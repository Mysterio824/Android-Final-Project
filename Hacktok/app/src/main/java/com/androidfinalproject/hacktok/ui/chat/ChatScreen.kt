package com.androidfinalproject.hacktok.ui.chat

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
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.chat.component.ChatBubble
import com.androidfinalproject.hacktok.ui.currentProfile.component.ChatTopBar
import com.androidfinalproject.hacktok.ui.currentProfile.component.MessageInput
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun ChatScreen(
    state: ChatState,
    onAction: (ChatAction) -> Unit,
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(state.messages) {
        onAction(ChatAction.LoadInitialMessages)
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                otherUser = state.otherUser,
                onBackClick = { onAction(ChatAction.NavigateBack) },
                onInfoClick = { onAction(ChatAction.NavigateToManageUser(state.otherUser.id)) }
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
                            onAction(ChatAction.SendMessage(messageText.text))
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
                ChatBubble(
                    message = message,
                    isCurrentUser = message.senderId == state.currentUser.id,
                    onDeleteMessage = { onAction(ChatAction.DeleteMessage(message.id)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            ChatScreen(
                state = ChatState(
                    currentUser = MockData.mockUsers[0],
                    otherUser = MockData.mockUsers[1],
                    messages = MockData.mockMessages,
                    isUserMuted = false
                ),
                onAction = {}
            )
        }
    }
}