package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PostContent(
    fullName: String? = null,
    post: Post,
    onPostClick: (String) -> Unit = {},
    onToggleLike: () -> Unit,
    onUnLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onOptionsClick: () -> Unit,
    onUserClick: () -> Unit,
    currentId: String,
    onLikesClick: (String) -> Unit = {}
) {
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
        if (post.reference == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ProfileImage(
                    imageUrl = post.user?.profileImage,
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = fullName ?: "",
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

            Text(
                text = post.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 16.sp
            )

            if (post.imageLink.isNotEmpty()) {
                AsyncImage(
                    model = post.imageLink,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .background(Color.White),
                    contentScale = ContentScale.FillHeight,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).clickable { onLikesClick(post.id!!) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Likes",
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.getLikeCount()}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

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
                    imageUrl = post.user?.profileImage,
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = fullName ?: "",
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

            Text(
                text = post.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 16.sp
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                ProfileImage(
                    imageUrl = post.reference.user?.profileImage,
                    size = 45.dp,
                    onClick = onUserClick
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = post.reference?.user?.fullName ?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = onUserClick)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDate(post.reference?.createdAt!!),
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

            Text(
                text = post.reference.content,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontSize = 16.sp
            )

            if (post.reference.imageLink.isNotEmpty()) {
                AsyncImage(
                    model = post.reference.imageLink,
                    contentDescription = "Post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .heightIn(max = 200.dp)
                        .background(Color.White),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Likes",
                        tint = Color(0xFF1877F2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${post.getLikeCount()}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

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

            TextButton(
                onClick = {
                    if (post.isLiked(currentId)) {
                        onUnLike()
                    } else {
                        onToggleLike()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (post.isLiked(currentId)) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like",
                    tint = if (post.isLiked(currentId)) Color(0xFF1565C0) else LocalContentColor.current
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Like",
                    color = if (post.isLiked(currentId)) Color(0xFF1565C0) else LocalContentColor.current
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

@Preview
@Composable
fun PostPreview(){
    MainAppTheme {
        Box{
            PostContent(
                post = MockData.mockPosts.first(),
                onShare = {},
                onComment = {},
                onToggleLike = {},
                onUserClick = {},
                onOptionsClick = {},
                onUnLike = {},
                currentId = ""
            )
        }
    }
}