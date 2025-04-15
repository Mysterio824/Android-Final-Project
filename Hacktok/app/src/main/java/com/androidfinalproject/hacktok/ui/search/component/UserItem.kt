package com.androidfinalproject.hacktok.ui.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.User

@Composable
fun UserItem(
    user: User,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable (onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp) // Padding giống Instagram
    ) {
        // Hiển thị tên người dùng (giống Instagram: tên nổi bật)
        Text(
            text = user.username ?: "Unknown User",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        // Hiển thị ID (nhỏ hơn, giống thông tin phụ)
        Text(
            text = "ID: ${user.id?.toString()?.take(8) ?: "N/A"}", // Cắt ngắn ID để gọn gàng
            fontSize = 12.sp,
            color = androidx.compose.ui.graphics.Color.Gray
        )
    }
}