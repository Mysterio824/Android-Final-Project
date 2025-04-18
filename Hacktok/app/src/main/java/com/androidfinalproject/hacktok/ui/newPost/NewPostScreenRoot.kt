package com.androidfinalproject.hacktok.ui.newPost

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.Manifest
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun NewPostScreenRoot(
    postId: String? = null,
    viewModel: NewPostViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onPost: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(postId) {
        if (!postId.isNullOrBlank()) {
            viewModel.onAction(NewPostAction.LoadPostForEditing(postId))
        }
    }

    // 📸 Image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onAction(NewPostAction.UpdateImageUri(uri))
            }
        }
    )

    // 🔐 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // ✅ Observe submission status
    LaunchedEffect(state.postSubmitted) {
        if (state.postSubmitted) {
            onPost()
            viewModel.onAction(NewPostAction.ClearSubmissionState)
        }
    }

    NewPostScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is NewPostAction.Close -> onClose()
                is NewPostAction.SubmitPost -> viewModel.onAction(action)
                is NewPostAction.UpdateImage -> {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }

                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PermissionChecker.PERMISSION_GRANTED

                    if (hasPermission) {
                        pickImageLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permission)
                    }
                }
                is NewPostAction.RemoveImage -> viewModel.onAction(action)
                else -> viewModel.onAction(action)
            }
        }
    )
}