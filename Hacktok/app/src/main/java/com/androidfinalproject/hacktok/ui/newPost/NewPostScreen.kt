package com.androidfinalproject.hacktok.ui.newPost

import android.net.Uri
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun NewPostScreen() {
    var caption by remember { mutableStateOf(TextFieldValue("")) }
    var privacy by remember { mutableStateOf("Only me") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

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
            // Header
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Centered title
                Text(
                    text = "Compose",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Right-aligned icon
                IconButton(
                    onClick = { /* Close */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )

            // User info with privacy dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Name at top, button at bottom
                Box(
                    modifier = Modifier
                        .height(48.dp) // match avatar height
                        .weight(1f)
                ) {
                    Text(
                        text = "Harry Maguire",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart).offset(y = (-4).dp)
                    )

                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xff4267B2),
                            contentColor = Color.White
                        ),
                        border = null,
                        modifier = Modifier
                            .height(24.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(privacy, style = MaterialTheme.typography.bodySmall)
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
                        listOf("Public", "Friends", "Only me").forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    privacy = option
                                    expanded = false
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = when (option) {
                                                "Public" -> painterResource(id = R.drawable.ic_public)
                                                "Friends" -> painterResource(id = R.drawable.ic_friends)
                                                else -> painterResource(id = R.drawable.ic_lock)
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(option)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Caption
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = {
                    Text(
                        text = "Harry, what is in your mind?",
                        textAlign = TextAlign.Start
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 80.dp), // initial height
                maxLines = Int.MAX_VALUE, // allow expansion
                singleLine = false,       // multiline
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                )
            )

            // Add to your post section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* add image */ }) {
                    Icon(painterResource(id = R.drawable.ic_add_photo), contentDescription = "Add an image")
                }
            }

            // Post button (disabled if caption empty)
            Button(
                onClick = { /* Đăng bài viết */ },
                enabled = caption.text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (caption.text.isNotBlank()) Color(0xFF4267B2) else Color.LightGray,
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
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            NewPostScreen()
        }
    }
}