package com.androidfinalproject.hacktok.ui.newPost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun NewPostScreen(
    state: NewPostState,
    onAction: (NewPostAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val privacyIcon = when (state.privacy) {
        PRIVACY.PUBLIC -> R.drawable.ic_public
        PRIVACY.FRIENDS -> R.drawable.ic_friends
        PRIVACY.PRIVATE -> R.drawable.ic_lock
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Compose",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                IconButton(
                    onClick = { onAction(NewPostAction.Close) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = state.username.ifBlank { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart).offset(y = (-4).dp)
                    )

                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF4267B2),
                            contentColor = Color.White
                        ),
                        border = null,
                        modifier = Modifier
                            .height(24.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Icon(
                            painter = painterResource(id = privacyIcon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(state.privacy.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_drop_down),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PRIVACY.entries.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    onAction(NewPostAction.UpdatePrivacy(option))
                                    expanded = false
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = when (option) {
                                                PRIVACY.PUBLIC -> painterResource(id = R.drawable.ic_public)
                                                PRIVACY.FRIENDS -> painterResource(id = R.drawable.ic_friends)
                                                PRIVACY.PRIVATE -> painterResource(id = R.drawable.ic_lock)
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                                    }
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = state.caption,
                onValueChange = { onAction(NewPostAction.UpdateCaption(it)) },
                placeholder = {
                    Text(
                        text = "Hey ${state.username.ifBlank { "Unknown" }}, what is in your mind?",
                        textAlign = TextAlign.Start
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 80.dp),
                maxLines = Int.MAX_VALUE,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            if (state.imageUri == null) {
                IconButton(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                    onClick = { onAction(NewPostAction.UpdateImage) })
                {
                    Icon(painterResource(id = R.drawable.ic_add_photo), contentDescription = "Add an image")
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .matchParentSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { onAction(NewPostAction.RemoveImage) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove image",
                            tint = Color.White
                        )
                    }
                }
            }

            Button(
                onClick = { onAction(NewPostAction.SubmitPost) },
                enabled = state.caption.isNotBlank() || state.imageUri != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.caption.isNotBlank() || state.imageUri != null) Color(0xFF4267B2) else Color.LightGray,
                    contentColor = Color.White
                )
            ) {
                Text("Post")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageDashboardScreenPreview() {
    var state by remember { mutableStateOf(NewPostState()) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            state = state.copy(imageUri = uri)
        }
    }

    MainAppTheme {
        NewPostScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is NewPostAction.UpdateCaption -> state = state.copy(caption = action.caption)
                    is NewPostAction.UpdatePrivacy -> state = state.copy(privacy = action.privacy)
                    is NewPostAction.UpdateImage -> pickImageLauncher.launch("image/*")
                    else -> {}
                }
            }
        )
    }
}