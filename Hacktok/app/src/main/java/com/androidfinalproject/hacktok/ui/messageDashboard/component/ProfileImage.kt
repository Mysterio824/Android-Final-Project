package com.androidfinalproject.hacktok.ui.messageDashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.R

@Composable
fun ProfileImage (
    imageSize: Dp,
    modifier: Modifier = Modifier,
    contentDescription: String,
    isActive: Boolean,
) {
    Box(
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .then(
                if (isActive) Modifier.border(2.dp, Color(0xFF72BF6A), CircleShape) else Modifier
            )
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        // Replace with profile images
        Image(
            painter = painterResource(id = R.drawable.placeholder_image), // Your placeholder image
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}