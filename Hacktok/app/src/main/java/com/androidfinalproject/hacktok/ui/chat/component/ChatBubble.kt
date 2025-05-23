package com.androidfinalproject.hacktok.ui.chat.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.Message
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDeleteMessage: (String?) -> Unit,
    isHighlighted: Boolean = false
) {
    var showTime by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState()

    // Use a different color for highlighted messages
    val bubbleColor = when {
        isHighlighted -> MaterialTheme.colorScheme.tertiaryContainer
        isCurrentUser -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val timeColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

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
                message.media?.let { media ->
                    if (media.type == "image") {
                        AsyncImage(
                            model = media.url,
                            contentDescription = "Message image",
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(200.dp)
                                .padding(bottom = 8.dp)
                                .clickable { showFullScreenImage = true },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (message.content.isNotEmpty()) {
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 16.sp
                    )
                }

                if (showTime) {
                    Text(
                        text = formatTimestamp(message.createdAt),
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
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    message.id?.let { onDeleteMessage(it) }
                    showMenu = false
                }
            )
        }
    }

    if (showFullScreenImage) {
        ModalBottomSheet(
            onDismissRequest = { showFullScreenImage = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                message.media?.let { media ->
                    if (media.type == "image") {
                        AsyncImage(
                            model = media.url,
                            contentDescription = "Full screen message image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInMinutes = diffInMillis / (1000 * 60)
    val diffInHours = diffInMinutes / 60
    val diffInDays = diffInHours / 24

    return when {
        diffInMinutes < 1 -> "Just now"
        diffInMinutes < 60 -> "$diffInMinutes min ago"
        diffInHours < 24 -> "$diffInHours h ago"
        diffInDays < 7 -> "$diffInDays d ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
    }
}