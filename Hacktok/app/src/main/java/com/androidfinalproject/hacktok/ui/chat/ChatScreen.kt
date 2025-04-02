package com.androidfinalproject.hacktok.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Message
import java.util.*
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.currentProfile.component.MessageInput
import com.androidfinalproject.hacktok.ui.currentProfile.component.ChatTopBar

@Composable
fun ChatScreen(
    messages: List<Message> = emptyList(),
    otherUser: User = User(username = "User", email = "user@example.com"),
    currentUserId: String = "user1",
    onSendMessage: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onDeleteMessage: (String) -> Unit = {}
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    MainAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatTopBar(
                otherUser = otherUser,
                onBackClick = onBackClick,
                onInfoClick = onInfoClick
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(messages.sortedByDescending { it.createdAt }) { message ->
                    ChatBubble(
                        message = message,
                        isCurrentUser = message.senderId == currentUserId,
                        onDeleteMessage = onDeleteMessage
                    )
                }
            }

            MessageInput(
                text = messageText,
                onTextChanged = { messageText = it },
                onSendClicked = {
                    if (messageText.text.isNotEmpty()) {
                        onSendMessage(messageText.text)
                        messageText = TextFieldValue("")
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDeleteMessage: (String) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { showTime = !showTime }, // Nhấn để hiển thị thời gian
                onLongClick = { showMenu = true } // Nhấn giữ để mở menu
            ),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    color = Color.White
                )
                if (showTime) {
                    Text(
                        text = message.createdAt.toString(),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }

        // Toggle menu khi nhấn giữ tin nhắn
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    clipboardManager.setText(AnnotatedString(message.content)) // Copy tin nhắn
                    showMenu = false
                }
            ) {
                Text("Copy")
            }
            DropdownMenuItem(
                onClick = {
                    message.id?.let { onDeleteMessage(it) } // Xóa tin nhắn
                    showMenu = false
                }
            ) {
                Text("Delete", color = Color.Red)
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
            // Tạo dữ liệu mẫu cho preview
            val user1 = "user1"
            val user2 = "user2"
            val otherUser = User(username = "Hữu Minh", email = "minhanh@example.com")

            val demoMessages = listOf(
                Message(
                    id = "1",
                    senderId = user2,
                    content = "Chào bạn, bạn khỏe không?",
                    createdAt = Date(System.currentTimeMillis() - 3600000)
                ),
                Message(
                    id = "2",
                    senderId = user1,
                    content = "Mình khỏe, còn bạn thì sao?",
                    createdAt = Date(System.currentTimeMillis() - 3500000)
                ),
                Message(
                    id = "3",
                    senderId = user2,
                    content = "Mình cũng khỏe. Hôm nay bạn đã làm gì?",
                    createdAt = Date(System.currentTimeMillis() - 3400000)
                ),
                Message(
                    id = "4",
                    senderId = user1,
                    content = "Mình đang code một ứng dụng Android. Còn bạn?",
                    createdAt = Date(System.currentTimeMillis() - 3300000)
                ),
                Message(
                    id = "5",
                    senderId = user2,
                    content = "Mình đang chuẩn bị cho kỳ thi sắp tới. Hơi căng thẳng một chút.",
                    createdAt = Date(System.currentTimeMillis() - 3200000)
                ),
                Message(
                    id = "6",
                    senderId = user1,
                    content = "Cố lên bạn nhé! Mình tin bạn sẽ làm tốt đấy.",
                    createdAt = Date(System.currentTimeMillis() - 3100000)
                )
            )

            ChatScreen(
                messages = demoMessages,
                currentUserId = user1,
                otherUser = otherUser
            )
        }
    }
}