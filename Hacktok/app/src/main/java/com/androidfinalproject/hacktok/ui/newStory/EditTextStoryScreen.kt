package com.androidfinalproject.hacktok.ui.newStory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.androidfinalproject.hacktok.ui.newStory.component.StoryEditorScaffold
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun EditTextStoryScreen(
    onClose: () -> Unit
) {
    var privacy by remember { mutableStateOf(PRIVACY.PUBLIC) }
    var inputText by remember { mutableStateOf("") }

    StoryEditorScaffold(
        privacy = privacy,
        onPrivacyChange = { privacy = it },
        onClose = onClose,
        onSend = { /* handle post */ },
        background = {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF3A81F5), Color(0xFF2B60D9))
                        )
                    )
                    .matchParentSize()
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Start typing",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center, // ✅ Center placeholder text
                        modifier = Modifier.fillMaxWidth() // ✅ Ensure it spans full width to apply centering
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditTextStoryScreenPreview() {
    MainAppTheme {
        EditTextStoryScreen (
            onClose = {}
        )
    }
}