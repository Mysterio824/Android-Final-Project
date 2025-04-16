package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
    imageUrl: String,
    onClick: () -> Unit,
    size: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        val painter = rememberAsyncImagePainter(
            model = imageUrl.takeIf { it.isNotBlank() },
            error = painterResource(id = R.drawable.placeholder_profile),
            placeholder = painterResource(id = R.drawable.placeholder_profile),
            fallback = painterResource(id = R.drawable.placeholder_profile)
        )

        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}