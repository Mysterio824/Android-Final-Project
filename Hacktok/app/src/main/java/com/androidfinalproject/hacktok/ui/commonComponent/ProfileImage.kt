package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.androidfinalproject.hacktok.R

@Composable
fun ProfileImage(
    imageUrl: String?,
    onClick: () -> Unit = {},
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
    ) {
        val painter = rememberAsyncImagePainter(
            model = imageUrl.takeIf { !it.isNullOrBlank() },
            error = painterResource(id = R.drawable.placeholder_profile),
            placeholder = painterResource(id = R.drawable.placeholder_profile),
            fallback = painterResource(id = R.drawable.placeholder_profile)
        )

        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop
        )
    }
}