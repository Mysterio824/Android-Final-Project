package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.androidfinalproject.hacktok.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostLikeButton(
    existingReaction: String? = null,
    onLike: (String) -> Unit,
    onUnlike: () -> Unit,
) {
    var showEmojiMenu by remember { mutableStateOf(false) }
    val isLiked = existingReaction != null
    val color = if (isLiked) {
        when (existingReaction) {
            "👍" -> Color(0xFF1565C0)
            "❤️" -> Color(0xFFED4956) // Red for love
            "😆" -> Color(0xFFFFD700) // Yellow for haha
            "😮" -> Color(0xFF00BFFF) // Light blue for wow
            "😢" -> Color(0xFF9932CC) // Purple for sad
            "😠" -> Color(0xFFFF8C00) // Orange for angry
            else ->  MaterialTheme.colorScheme.primary
        }
    } else  MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .height(25.dp)
            .padding(vertical = 2.dp)
            .padding(horizontal = 24.dp)
            .combinedClickable(
                onClick = {
                    if (isLiked) {
                        onUnlike()
                    } else {
                        onLike("👍")
                    }
                },
                onLongClick = { showEmojiMenu = true }
            )
    ) {
            Icon(
                imageVector = if (isLiked) {
                    getReactionIcon(existingReaction!!)
                } else Icons.Default.ThumbUp,
                contentDescription = "Emoji Icon",
                tint = color
            )
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = if (isLiked) {
                    stringResource(id = when (existingReaction) {
                        "👍" -> R.string.like
                        "❤️" -> R.string.love
                        "😆" -> R.string.haha
                        "😮" -> R.string.wow
                        "😢" -> R.string.sad
                        "😠" -> R.string.angry
                        else -> R.string.like
                    })
                } else stringResource(id = R.string.like),
                color = color
            )
        }

    if (showEmojiMenu) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { showEmojiMenu = false },
            properties = PopupProperties(focusable = true)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmojiReaction("👍", "Like", Color.Blue) {
                    onLike("👍")
                    showEmojiMenu = false
                }
                EmojiReaction("❤️", "Love", Color(0xFFED4956)) {
                    onLike("❤️")
                    showEmojiMenu = false
                }
                EmojiReaction("😆", "Haha", Color(0xFFFFD700)) {
                    onLike("😆")
                    showEmojiMenu = false
                }
                EmojiReaction("😮", "Wow", Color(0xFF00BFFF)) {
                    onLike("😮")
                    showEmojiMenu = false
                }
                EmojiReaction("😢", "Sad", Color(0xFF9932CC)) {
                    onLike("😢")
                    showEmojiMenu = false
                }
                EmojiReaction("😠", "Angry", Color(0xFFFF8C00)) {
                    onLike("😠")
                    showEmojiMenu = false
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentLikeButton(
    itemId: String,
    existingReaction: String? = null,
    onLike: (String, String) -> Unit,
    onUnlike: (String) -> Unit
) {
    var showEmojiMenu by remember { mutableStateOf(false) }
    val isLiked = existingReaction != null

    Row (
        modifier = Modifier
            .height(24.dp)
            .combinedClickable(
                onClick = {
                    if (isLiked) {
                        onUnlike(itemId)
                    } else {
                        onLike(itemId, "👍")
                    }
                },
                onLongClick = { showEmojiMenu = true }
            )
            .padding(horizontal = 4.dp)
        ) {
            Text(
                text = if (isLiked) {
                    stringResource(id = when (existingReaction) {
                        "👍" -> R.string.like
                        "❤️" -> R.string.love
                        "😆" -> R.string.haha
                        "😮" -> R.string.wow
                        "😢" -> R.string.sad
                        "😠" -> R.string.angry
                        else -> R.string.like
                    })
                } else stringResource(id = R.string.like),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color =  when (existingReaction) {
                    "👍" -> Color(0xFF1565C0)
                    "❤️" -> Color(0xFFED4956) // Red for love
                    "😆" -> Color(0xFFFFD700) // Yellow for haha
                    "😮" -> Color(0xFF00BFFF) // Light blue for wow
                    "😢" -> Color(0xFF9932CC) // Purple for sad
                    "😠" -> Color(0xFFFF8C00) // Orange for angry
                    else -> Color.Gray
                }
            )
        }

        // Long press to show emoji menu
        TextButton(
            onClick = { showEmojiMenu = true },
            modifier = Modifier
                .height(20.dp)
                .padding(horizontal = 4.dp)
                .width(0.dp), // Zero width to overlap with the like button
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {}

        if (showEmojiMenu) {
            Popup(
                alignment = Alignment.BottomCenter,
                onDismissRequest = { showEmojiMenu = false },
                properties = PopupProperties(focusable = true)
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EmojiReaction("👍", "Like", Color.Blue) {
                        onLike(itemId, "👍")
                        showEmojiMenu = false
                    }
                    EmojiReaction("❤️", "Love", Color(0xFFED4956)) {
                        onLike(itemId, "❤️")
                        showEmojiMenu = false
                    }
                    EmojiReaction("😆", "Haha", Color(0xFFFFD700)) {
                        onLike(itemId, "😆")
                        showEmojiMenu = false
                    }
                    EmojiReaction("😮", "Wow", Color(0xFF00BFFF)) {
                        onLike(itemId, "😮")
                        showEmojiMenu = false
                    }
                    EmojiReaction("😢", "Sad", Color(0xFF9932CC)) {
                        onLike(itemId, "😢")
                        showEmojiMenu = false
                    }
                    EmojiReaction("😠", "Angry", Color(0xFFFF8C00)) {
                        onLike(itemId, "😠")
                        showEmojiMenu = false
                    }
                }
            }
        }
    }


@Composable
fun EmojiReaction(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}