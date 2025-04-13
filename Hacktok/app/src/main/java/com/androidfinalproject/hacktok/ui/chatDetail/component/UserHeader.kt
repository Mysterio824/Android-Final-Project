package com.androidfinalproject.hacktok.ui.chatDetail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.User

@Composable
fun UserHeader(
    user: User,
    onViewProfileClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar (giả sử dùng placeholder)
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        ) {
            // Sử dụng placeholder thay vì ảnh thật
            // Trong ứng dụng thực, bạn có thể sử dụng Coil hay Glide để load ảnh
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tên người dùng
        Text(
            text = user.username,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email
        Text(
            text = user.email ?: "",
            fontSize = 14.sp,
            color = androidx.compose.ui.graphics.Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nút xem hồ sơ
        Button(onClick = onViewProfileClick) {
            Text("Xem hồ sơ")
        }
    }
}