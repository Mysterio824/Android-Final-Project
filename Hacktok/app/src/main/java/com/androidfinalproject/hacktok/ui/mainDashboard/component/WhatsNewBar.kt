package com.androidfinalproject.hacktok.ui.mainDashboard.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WhatsNewBar(
    onSearch: (String) -> Unit,
    onPickImage: () -> Unit,
    onTakePhoto: () -> Unit,
    onVoiceInput: () -> Unit,
    onLocation: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        BasicTextField( // ✅ Dùng BasicTextField để loại bỏ màu nền
            value = query,
            onValueChange = { query = it },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(8.dp)
                ) {
                    if (query.isEmpty()) {
                        Text("What's new...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onPickImage) {
                Icon(Icons.Filled.Image, contentDescription = "Chọn ảnh")
            }

            IconButton(onClick = onTakePhoto) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Chụp ảnh")
            }

            IconButton(onClick = onVoiceInput) {
                Icon(Icons.Filled.Mic, contentDescription = "Voice Input")
            }

            IconButton(onClick = onLocation) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Định vị")
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color.Gray.copy(alpha = 0.3f), // ✅ Màu xám nhạt
            thickness = 0.5.dp // ✅ Độ dày mỏng
        )
    }
}
