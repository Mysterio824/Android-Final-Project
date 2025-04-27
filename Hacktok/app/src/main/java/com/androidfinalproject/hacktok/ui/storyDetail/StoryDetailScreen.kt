package com.androidfinalproject.hacktok.ui.storydetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoryDetailScreen(
    state: StoryDetailState,
    onAction: (StoryDetailAction) -> Unit
) {
    val context = LocalContext.current

    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    var showReportDialog by remember { mutableStateOf(false) }

    // Handle success message and navigation
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            onAction(StoryDetailAction.CloseStory)
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Report Story",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Do you want to report this story?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(StoryDetailAction.ReportStory)
                        showReportDialog = false
                    }
                ) {
                    Text(
                        text = "Report",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Story Content
        state.story?.let { story ->
            AsyncImage(
                model = story.media.url,
                contentDescription = "Story",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Story Progress Bar - at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(state.storyProgress)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onAction(StoryDetailAction.NavigateToUserProfile(state.story?.userId)) }
                ) {
                    if (state.story?.userAvatar != null) {
                        AsyncImage(
                            model = state.story.userAvatar,
                            contentDescription = "User Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder for null avatar
                        Icon(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize(),
                            imageVector = Icons.Default.Close,
                            contentDescription = "Default Avatar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // User Name and Timestamp
                Column {
                    Text(
                        text = state.story?.userName ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    state.story?.createdAt?.let { timestamp ->
                        Text(
                            text = formatTimestamp(timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            val isMyStory = state.currentUser.id == state.story?.userId

            IconButton(onClick = {
                showReportDialog = true
            }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

// Dialog xử lý report / delete
            if (showReportDialog) {
                AlertDialog(
                    onDismissRequest = { showReportDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(
                            text = if (isMyStory) "Delete Story" else "Report Story",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = if (isMyStory)
                                "Are you sure you want to delete this story?"
                            else
                                "Do you want to report this story?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (isMyStory) {
                                    onAction(StoryDetailAction.DeleteStory)
                                } else {
                                    onAction(StoryDetailAction.ReportStory)
                                }
                                showReportDialog = false
                            }
                        ) {
                            Text(
                                text = if (isMyStory) "Delete" else "Report",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showReportDialog = false }) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }

            IconButton(onClick = { onAction(StoryDetailAction.CloseStory) }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Touch areas for navigation
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 60.dp)
        ) {
            // Previous story area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onAction(StoryDetailAction.PauseStory) },
                            onTap = { onAction(StoryDetailAction.PreviousStory) },
                            onPress = { /* Do nothing */ }
                        )
                    }
            )

            // Next story area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onAction(StoryDetailAction.PauseStory) },
                            onTap = { onAction(StoryDetailAction.NextStory) },
                            onPress = { /* Do nothing */ }
                        )
                    }
            )
        }

        // Message Input - matching ChatScreen's MessageInput style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Message input field with rounded corners similar to Facebook
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = {
                    Text(
                        text = "Send message",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (messageText.text.isNotEmpty()) {
                                onAction(StoryDetailAction.SendMessage(messageText.text))
                                messageText = TextFieldValue("")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                maxLines = 1,
                singleLine = true
            )
        }

        // Bottom padding to match ChatScreen
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun formatTimestamp(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInMinutes = diffInMillis / (1000 * 60)

    return when {
        diffInMinutes < 60 -> "$diffInMinutes min ago"
        diffInMinutes < 24 * 60 -> "${diffInMinutes / 60} hours ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}

@Preview(showBackground = true)
@Composable
fun StoryDetailScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            StoryDetailScreen(
                state = StoryDetailState(
                    story = Story(
                        id = "1",
                        userId = "user2",
                        userName = "JohnDoe",
                        userAvatar = null,
                        media = Media(
                            type = "image",
                            url = "https://example.com/story.jpg",
                            thumbnailUrl = "https://example.com/story_thumb.jpg"
                        ),
                        createdAt = Date(),
                        expiresAt = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000),
                        viewerIds = listOf("user1", "user3")
                    ),
                    currentStoryIndex = 0,
                    totalStories = 3,
                    storyProgress = 0.7f
                ),
                onAction = {}
            )
        }
    }
}