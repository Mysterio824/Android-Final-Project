package com.androidfinalproject.hacktok.ui.groupChat.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Message

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupChatBubble(
    message: Message,
    senderName: String,
    isCurrentUser: Boolean,
    onDeleteMessage: (String?) -> Unit
) {
    var showTime by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    // Màu sắc theo đồng bộ với dashboard
    val bubbleColor = if (isCurrentUser) Color(0xFF72BF6A) else Color(0xFFECECEC)
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val timeColor = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else Color.DarkGray
    val senderNameColor = if (isCurrentUser) Color.White.copy(alpha = 0.9f) else Color(0xFF72BF6A)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .combinedClickable(
                onClick = { showTime = !showTime },
                onLongClick = { showMenu = true }
            ),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bubbleColor,
                    shape = MaterialTheme.shapes.large
                )
                .padding(12.dp)
        ) {
            Column {
                if (!isCurrentUser) {
                    Text(
                        text = senderName,
                        color = senderNameColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 16.sp
                )

                if (showTime) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.createdAt.toString(),
                        color = timeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Copy") },
                onClick = {
                    clipboardManager.setText(AnnotatedString(message.content))
                    showMenu = false
                }
            )

            if (isCurrentUser) {
                DropdownMenuItem(
                    text = { Text("Delete", color = Color.Red) },
                    onClick = {
                        message.id?.let { onDeleteMessage(it) }
                        showMenu = false
                    }
                )
            }
        }
    }
}