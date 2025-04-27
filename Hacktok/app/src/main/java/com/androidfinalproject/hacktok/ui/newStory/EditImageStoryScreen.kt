package com.androidfinalproject.hacktok.ui.newStory

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.androidfinalproject.hacktok.ui.newStory.component.StoryEditorScaffold

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditImageStoryScreen(
    viewModel: NewStoryViewModel,
    onClose: () -> Unit,
    imageUri: Uri?,
) {
    var privacy by remember { mutableStateOf(PRIVACY.PUBLIC) }
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Observe state for successful story creation
    LaunchedEffect(state.isStoryCreated) {
        Log.d("EditImageStoryScreen", "isStoryCreated changed to: ${state.isStoryCreated}")
        if (state.isStoryCreated) {
            Log.d("EditImageStoryScreen", "Showing success toast and navigating back")
            // Show success notification
            Toast.makeText(context, "Create story successfully", Toast.LENGTH_SHORT).show()
            onClose()
            viewModel.resetState()
        }
    }

    // Log state changes
    LaunchedEffect(state) {
        Log.d("EditImageStoryScreen", "State updated: isLoading=${state.isLoading}, isStoryCreated=${state.isStoryCreated}, error=${state.error}")
    }

    StoryEditorScaffold(
        privacy = privacy,
        onPrivacyChange = {
            privacy = it
            viewModel.onAction(NewStoryAction.UpdatePrivacy(it))
        },
        onClose = onClose,
        onSend = {
            viewModel.onAction(NewStoryAction.CreateImageStory(imageUri, privacy))
        }
    ) {
        imageUri?.let { uri ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}