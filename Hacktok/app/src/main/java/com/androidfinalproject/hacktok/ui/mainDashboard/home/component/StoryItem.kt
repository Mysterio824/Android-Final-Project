package com.androidfinalproject.hacktok.ui.mainDashboard.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.androidfinalproject.hacktok.R

@Composable
fun StoryItem(
    profileImageUrl: String,
    storyImageUrl: String,
    username: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Story background (would be an image in a real app)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF888888))
            )

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Profile picture with blue border (Facebook's active story indicator)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .padding(start = 5.dp, top = 5.dp)
            ) {
                val painter = rememberAsyncImagePainter(
                    model = profileImageUrl.takeIf { it.isNotBlank() },
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

            // Username at bottom
            Text(
                text = username,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
