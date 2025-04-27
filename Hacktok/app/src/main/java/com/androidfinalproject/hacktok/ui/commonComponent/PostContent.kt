package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostContent(
    user: User,
    post: Post,
    onPostClick: (String) -> Unit = {},
    onToggleLike: (String) -> Unit,
    onUnLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onOptionsClick: () -> Unit,
    onUserClick: () -> Unit,
    currentId: String,
    onImageClick: (String) -> Unit = {},
    onLikesClick: (String) -> Unit = {},
    referencePost: Post? = null,
    referenceUser: User? = null,
) {
    val topEmojis = post.getTopEmojis(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { post.id?.let { onPostClick(it) } },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        if (referencePost == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ProfileImage(
                    imageUrl = user.profileImage ?: "",
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = user.fullName ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = onUserClick)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(post.createdAt),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val privacyIcon = when (post.privacy) {
                            "PUBLIC" -> Icons.Default.Public
                            "FRIENDS" -> Icons.Default.Groups
                            "PRIVATE" -> Icons.Default.Lock
                            else -> Icons.Default.Public // fallback
                        }
                        Icon(
                            imageVector = privacyIcon,
                            contentDescription = "Privacy Setting",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                IconButton(onClick = onOptionsClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }

            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 16.sp
                )
            }


            if (post.imageLink.isNotEmpty()) {
                AsyncImage(
                    model = post.imageLink,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .background(Color.White)
                        .clickable {
                            onImageClick(post.imageLink)
                        },
                    contentScale = ContentScale.FillWidth,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (topEmojis.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLikesClick(post.id!!) }
                    ) {
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
                        Text(
                            text = "${post.getLikeCount()}",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${post.commentCount} comments",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ProfileImage(
                    imageUrl = user.profileImage ?: "",
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = user.fullName ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = onUserClick)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(post.createdAt),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Privacy: Public",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                IconButton(onClick = onOptionsClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }

            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 16.sp
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ProfileImage(
                    imageUrl = referenceUser?.profileImage ?: "",
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = referenceUser?.fullName ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = onUserClick)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(referencePost.createdAt),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Privacy: Public",
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            if (referencePost.content.isNotBlank()) {
                Text(
                    text = referencePost.content,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontSize = 16.sp
                )
            }

            if (referencePost.imageLink.isNotEmpty()) {
                AsyncImage(
                    model = referencePost.imageLink,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .background(Color.White)
                        .clickable {
                            onImageClick(referencePost.imageLink) // 👈 call navigate
                        },
                    contentScale = ContentScale.FillHeight,
                )
            } else {
                HorizontalDivider()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (topEmojis.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onLikesClick(post.id!!) }
                    ) {
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
                        Text(
                            text = "${post.getLikeCount()}",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${post.commentCount} comments",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }


        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier.weight(1f)
                    .padding(10.dp)
            ) {
                val currentEmoji = post.getEmoji(currentId)
                PostLikeButton(
                    existingReaction = currentEmoji,
                    onLike = { emoji ->
                        onToggleLike(emoji)
                    },
                    onUnlike = { onUnLike() }
                )
            }


            TextButton(
                onClick = onComment,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = "Comment"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Comment")
            }

            TextButton(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share")
            }
        }

    }
}

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("MMM d 'at' h:mm a", Locale.getDefault())
    return format.format(date)
}