package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.commonComponent.CommentLikeButton
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    comment: Comment,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    allComments: List<Comment>,
    onLikeComment: (String?, String) -> Unit,
    onUnLikeComment: (String?) -> Unit,
    onCommentLongPress: (String?) -> Unit,
    onUserClick: (String) -> Unit,
    onReplyClick: (String) -> Unit,
    onLikesClick: (String) -> Unit,
    currentUserId: String
) {
    val shouldHighlight = isSelected || isHighlighted

    val replies = allComments
        .filter { it.parentCommentId == comment.id }
        .sortedBy { it.createdAt }

    val user = comment.userSnapshot

    // Get top 3 emojis
    val topEmojis = comment.getTopEmojis(3)

    var showReplies by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(
                start = if (comment.parentCommentId == null) 16.dp else 32.dp, // Increased indent for replies
                end = 16.dp,
                top = 8.dp
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (shouldHighlight) Color(0xFFE0E0E0) else Color.Transparent
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            // User avatar
            ProfileImage(
                imageUrl = user.profileImage,
                onClick = { onUserClick(comment.userId) },
                size = 32.dp
            )

            // Comment content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F2F5))
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { onCommentLongPress(comment.id) }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth() // Make comment box take full width
                ) {
                    Column {
                        Text(
                            text = user.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable{ onUserClick(comment.userId) }
                        )
                        Text(
                            text = comment.content,
                            fontSize = 14.sp
                        )
                    }
                }

                // Comment actions
                Row(
                    modifier = Modifier
                        .padding(start = 0.dp, top = 0.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = formatDateShort(comment.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.widthIn(min = 45.dp, max = 45.dp)
                    )

                    CommentLikeButton(
                        itemId = comment.id!!,
                        existingReaction = comment.getEmoji(currentUserId),
                        onLike = { id, emoji -> onLikeComment(id, emoji) },
                        onUnlike = { onUnLikeComment(it) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = { onReplyClick(comment.parentCommentId ?: comment.id) },
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.reply),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (comment.getLikeCount() > 0) {
                        Box(
                            modifier = Modifier
                                .clickable { onLikesClick(comment.id) }
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F2F5))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (topEmojis.isNotEmpty()) {
                                    Row {
                                        topEmojis.forEach { emoji ->
                                            Text(
                                                text = emoji,
                                                fontSize = 14.sp,
                                                modifier = Modifier.padding(end = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }

                                Text(
                                    text = "${comment.getLikeCount()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                // Show "View replies" / "Hide replies" toggle for parent comments with replies
                if (comment.parentCommentId == null && replies.isNotEmpty()) {
                    TextButton(
                        onClick = { showReplies = !showReplies },
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Visual indicator for replies (like the line Facebook uses)
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(1.dp)
                                    .background(Color.Gray)
                            )
                            Text(
                                text = if (showReplies) stringResource(R.string.hide_replies)
                                        else stringResource(R.string.view_replies, replies.size),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showReplies,
                        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(150))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            replies.forEach { reply ->
                                CommentItem(
                                    comment = reply,
                                    allComments = allComments,
                                    onLikeComment = onLikeComment,
                                    onCommentLongPress = onCommentLongPress,
                                    onUserClick = onUserClick,
                                    onUnLikeComment = onUnLikeComment,
                                    onReplyClick = onReplyClick,
                                    currentUserId = currentUserId,
                                    onLikesClick = onLikesClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatDateShort(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInHours = diffInMillis / (1000 * 60 * 60)

    return when {
        diffInHours < 1 -> "${diffInMillis / (1000 * 60)}m"
        diffInHours < 24 -> "${diffInHours}h"
        diffInHours < 48 -> "Yesterday"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewComponent() {
    MainAppTheme {
        CommentItem(
            comment = MockData.mockComments.first(),
            onUserClick = {},
            onLikeComment = { _, _ -> },
            onCommentLongPress = {},
            onUnLikeComment = {},
            onReplyClick = {},
            allComments = emptyList(),
            currentUserId = "",
            onLikesClick = {}
        )
    }
}