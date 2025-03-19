package com.androidfinalproject.hacktok.ui.search.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.Post

@Composable
fun PostItem(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Khoảng cách giống Threads
    ) {
        // Tên người dùng (username) in đậm
        Text(
            text = post.user.username,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Nội dung bài đăng (content)
        Text(
            text = post.content,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Dòng tương tác (like, comment, repost, share)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Số lượt reply và like (giả lập)
            Text(
                text = "0 replies · 0 likes",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.Gray,
                modifier = Modifier.weight(1f) // Chiếm phần còn lại của dòng
            )

            // Các biểu tượng tương tác
            Row {
                IconButton(onClick = { /* Xử lý like */ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                IconButton(onClick = { /* Xử lý comment */ }) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                IconButton(onClick = { /* Xử lý repost */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = "Repost",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                IconButton(onClick = { /* Xử lý share */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "Share",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}