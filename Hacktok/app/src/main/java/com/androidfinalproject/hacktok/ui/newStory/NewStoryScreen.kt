package com.androidfinalproject.hacktok.ui.newStory

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun NewStoryScreen(
    images: List<Uri>,
    state: NewStoryState,
    onAction: (NewStoryAction) -> Unit,
) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Centered Title
            Text(
                text = "Create story",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Left-aligned Close Button
            IconButton(
                onClick = { onAction(NewStoryAction.NavigateBack) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }

        // Tool Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ToolButton(text = "Text Only", onClick = { onAction(NewStoryAction.NewTextStory) })
        }

        // Selection Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Camera roll", color = Color.White)
        }

        // Grid of Images
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(images) { uri ->
                Box(modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        selectedImage = if (selectedImage == uri) null else uri
                    }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uri)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (selectedImage == uri) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    onAction(NewStoryAction.GoToImageEditor(uri))
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit), // use a pen/edit icon
                                    contentDescription = "Edit image",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolButton(
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text.take(1), color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, color = Color.White, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun NewStoryScreenPreview() {
    MainAppTheme {
        NewStoryScreen(
            images = listOf(
            ),
            onAction = {},
            state = NewStoryState(),
        )
    }
}

