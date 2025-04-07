package com.androidfinalproject.hacktok.ui.chat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Message


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDeleteMessage: (String?) -> Unit
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