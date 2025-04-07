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
import com.androidfinalproject.hacktok.ui.chat.component.ChatBubble
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.currentProfile.component.MessageInput
import com.androidfinalproject.hacktok.ui.currentProfile.component.ChatTopBar
import com.androidfinalproject.hacktok.ui.post.PostDetailAction
import com.androidfinalproject.hacktok.ui.post.PostDetailState

@Composable
fun ChatScreen(
    state :ChatState,
    onAction: (ChatAction) -> Unit,
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(state.messages) {
        onAction(ChatAction.LoadInitialMessages)
    }


    MainAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatTopBar(
                otherUser = state.currentUser,
                onBackClick = {onAction(ChatAction.NavigateBack)},
                onInfoClick = {onAction(ChatAction.NavigateToManageUser(state.otherUser.id))}
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
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

//            ChatScreen(
//                messages = demoMessages,
//                currentUserId = user1,
//                otherUser = otherUser,
//
//            )
        }
    }
}