package com.androidfinalproject.hacktok.ui.post.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun CommentItem(
    comment: Comment,
    onUserClick: (String) -> Unit,
    onLikeClick: (Comment) -> Unit,
    isLikedByUser: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { onUserClick(comment.userId) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = MockData.mockUsers.first().username.first().toString().uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = MockData.mockUsers.first().username,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onUserClick(comment.userId) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isLikedByUser) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Likes",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onLikeClick(comment) },
                        tint = MaterialTheme.colorScheme.primary
                    )

                    if (comment.likeCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${comment.likeCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewComponent() {
    MainAppTheme {
        CommentItem(
            comment = MockData.mockComments.first().copy(likeCount = 1),
            onUserClick = {},
            onLikeClick = { true },
            isLikedByUser = true
        )
    }
}