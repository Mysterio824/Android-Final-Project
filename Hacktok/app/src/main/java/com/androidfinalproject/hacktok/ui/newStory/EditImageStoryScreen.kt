package com.androidfinalproject.hacktok.ui.newStory

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    // Observe state for successful story creation
    LaunchedEffect(viewModel.state.value.isStoryCreated) {
        if (viewModel.state.value.isStoryCreated) {
            onClose()
            viewModel.resetState()
        }
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