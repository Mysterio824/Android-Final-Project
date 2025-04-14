package com.androidfinalproject.hacktok.ui.newStory

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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

@Composable
fun EditImageStoryScreen(
    onClose: () -> Unit,
    imageUri: Uri?,
) {
    var privacy by remember { mutableStateOf(PRIVACY.PUBLIC) }

    StoryEditorScaffold(
        privacy = privacy,
        onPrivacyChange = { privacy = it },
        onClose = onClose,
        onSend = { /* handle post */ }
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