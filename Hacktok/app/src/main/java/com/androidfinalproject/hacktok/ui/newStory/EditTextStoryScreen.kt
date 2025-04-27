package com.androidfinalproject.hacktok.ui.newStory

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.androidfinalproject.hacktok.ui.newStory.component.StoryEditorScaffold
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditTextStoryScreen(
    viewModel: NewStoryViewModel,
    onClose: () -> Unit
) {
    var privacy by remember { mutableStateOf(PRIVACY.PUBLIC) }
    var inputText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Observe state for successful story creation
    LaunchedEffect(state.isStoryCreated) {
        Log.d("EditTextStoryScreen", "isStoryCreated changed to: ${state.isStoryCreated}")
        if (state.isStoryCreated) {
            Log.d("EditTextStoryScreen", "Showing success toast and navigating back")
            // Show success notification
            Toast.makeText(context, "Create story successfully", Toast.LENGTH_SHORT).show()
            onClose()
            viewModel.resetState()
        }
    }

    // Log state changes
    LaunchedEffect(state) {
        Log.d("EditTextStoryScreen", "State updated: isLoading=${state.isLoading}, isStoryCreated=${state.isStoryCreated}, error=${state.error}")
    }

    StoryEditorScaffold(
        privacy = privacy,
        onPrivacyChange = {
            privacy = it
            viewModel.onAction(NewStoryAction.UpdatePrivacy(it))
        },
        onClose = onClose,
        onSend = {
            viewModel.onAction(NewStoryAction.CreateTextStory(inputText, privacy))
        },
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
                onValueChange = {
                    inputText = it
                    viewModel.onAction(NewStoryAction.UpdateText(it))
                },
                placeholder = {
                    Text(
                        text = "Start typing",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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