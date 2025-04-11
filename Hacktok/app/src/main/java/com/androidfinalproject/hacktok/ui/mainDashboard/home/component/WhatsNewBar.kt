package com.androidfinalproject.hacktok.ui.mainDashboard.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.androidfinalproject.hacktok.R


@Composable
fun WhatsNewBar(
    profilePicUrl: String,
    onNewPostCLick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RectangleShape
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = profilePicUrl.takeIf { it.isNotBlank() },
                        error = painterResource(id = R.drawable.placeholder_profile),
                        placeholder = painterResource(id = R.drawable.placeholder_profile),
                        fallback = painterResource(id = R.drawable.placeholder_profile)
                    )

                    Image(
                        painter = painter,
                        contentDescription = "Blank Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF0F2F5)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { onNewPostCLick() }
                    ) {
                        Text(
                            text = "What's on your mind?",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}