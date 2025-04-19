package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.ui.newPost.NewPostAction
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun SharePostDialog(
    userName: String,
    userAvatar: Painter,
    onDismiss: () -> Unit,
    onSubmit: (String, PRIVACY) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedPrivacy by remember { mutableStateOf(PRIVACY.PUBLIC) }

    var caption by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Share", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = userAvatar,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(userName, fontWeight = FontWeight.Bold)
                        Row {
                            Box {
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
                                        painter = when (selectedPrivacy) {
                                            PRIVACY.PUBLIC -> painterResource(id = R.drawable.ic_public)
                                            PRIVACY.FRIENDS -> painterResource(id = R.drawable.ic_friends)
                                            PRIVACY.PRIVATE -> painterResource(id = R.drawable.ic_lock)
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        when (selectedPrivacy) {
                                            PRIVACY.PUBLIC -> "Public"
                                            PRIVACY.FRIENDS -> "Friends"
                                            PRIVACY.PRIVATE -> "Private"
                                        },
                                        style = MaterialTheme.typography.bodySmall
                                    )
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
                                                selectedPrivacy = option
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
                                                    Text(
                                                        when (option) {
                                                            PRIVACY.PUBLIC -> "Public"
                                                            PRIVACY.FRIENDS -> "Friends"
                                                            PRIVACY.PRIVATE -> "Private"
                                                        }
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp),
                    placeholder = { Text("Say something about this (optional)") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSubmit(caption, selectedPrivacy) }) {
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharePostDiaglogPreview() {
    MainAppTheme {
        SharePostDialog(
            userName = "Ronaldo > Pessi",
            userAvatar = painterResource(id = R.drawable.placeholder_image),
            onDismiss = {},
            onSubmit = { _, _ -> }
        )
    }
}