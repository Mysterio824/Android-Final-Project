package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentItem(
    comment: Comment,
    isSelected: Boolean = false,
    allComments: List<Comment>,
    onLikeComment: (String?) -> Unit,
    onCommentLongPress: (String?) -> Unit,
    onUserClick: (String) -> Unit,
    onReplyClick: (String) -> Unit
) {
    val replies = allComments.filter { it.parentCommentId == comment.id }
    val user = comment.userSnapshot

    var showReplies by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(
            start = if (comment.parentCommentId == null) 16.dp else 10.dp,
            end = 16.dp,
            top = 8.dp
        )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFFE0E0E0) else Color.Transparent
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.Top
        ) {
            // User avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            ) {
                val imageUrl = user.profileImage
                val painter = rememberAsyncImagePainter(
                    model = imageUrl.takeIf { !it.isNullOrBlank() },
                    error = painterResource(id = R.drawable.placeholder_profile),
                    placeholder = painterResource(id = R.drawable.placeholder_profile),
                    fallback = painterResource(id = R.drawable.placeholder_profile),
                )

                Image(
                    painter = painter,
                    contentDescription = "Profile picture of ${user.username}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onUserClick(comment.userId) },
                    contentScale = ContentScale.Crop
                )
            }

            // Comment content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
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
                ) {
                    Column {
                        Text(
                            text = "User ${user.username}",
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
                    modifier = Modifier.padding(start = 0.dp, top = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onLikeComment(comment.id) },
                        modifier = Modifier.height(20.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Like",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    if (comment.getLikeCount() > 0) {
                        Text(
                            text = "${comment.getLikeCount()}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = { onReplyClick(comment.id!!) },
                        modifier = Modifier.height(24.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)

                    ) {
                        Text(
                            text = "Reply",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formatDateShort(comment.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
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
                                text = if (showReplies) "Hide replies" else "View ${replies.size} replies",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                    }

                    // Display replies with animation
                    AnimatedVisibility(
                        visible = showReplies,
                        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150)),
                        exit = fadeOut(animationSpec = tween(150)) + shrinkVertically(animationSpec = tween(150))
                    ) {
                        Column {
                            replies.forEach { reply ->
                                CommentItem(
                                    comment = reply,
                                    allComments = allComments,
                                    onLikeComment = onLikeComment,
                                    onCommentLongPress = onCommentLongPress,
                                    onUserClick = onUserClick,
                                    onReplyClick = onReplyClick
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
            onLikeComment = {},
            onCommentLongPress = {},
            onReplyClick = {},
            allComments = emptyList()
        )
    }
}