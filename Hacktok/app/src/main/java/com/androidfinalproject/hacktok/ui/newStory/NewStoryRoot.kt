package com.androidfinalproject.hacktok.ui.newStory

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.androidfinalproject.hacktok.ui.post.PostDetailAction

@Composable
fun NewStoryRoot(
    onNavigateBack: () -> Unit
    ) {
    val viewModel: NewStoryViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current
    val images = remember { mutableStateListOf<Uri>() }
    var permissionGranted by remember { mutableStateOf(false) }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        android.Manifest.permission.READ_MEDIA_IMAGES
    else
        android.Manifest.permission.READ_EXTERNAL_STORAGE

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        if (isGranted) {
            permissionGranted = true
        } else {
            permissionLauncher.launch(permission)
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            val uris = loadImagesFromDevice(context)
            images.clear()
            images.addAll(uris)
        }
    }

    NavHost(navController = navController, startDestination = "select") {
        composable("select") {
            NewStoryScreen(
                images = images,
                onAction = { action ->
                    when (action) {
                        is NewStoryAction.GoToImageEditor -> {
                            viewModel.onAction(action)
                            navController.navigate("edit_image?uri=${Uri.encode(action.imageUri.toString())}")
                        }
                        is NewStoryAction.NewTextStory -> {
                            viewModel.onAction(action)
                            navController.navigate("edit_text")
                        }
                        is NewStoryAction.NavigateBack -> onNavigateBack()
                        else -> {
                            viewModel.onAction(action)
                        }
                    }
                },
                state = state
            )
        }

        composable("edit_text") {
            EditTextStoryScreen(
                viewModel = viewModel,
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = "edit_image?uri={uri}",
            arguments = listOf(navArgument("uri") { type = NavType.StringType })
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("uri")
            val imageUri = uriString?.let { Uri.parse(it) }
            EditImageStoryScreen(
                viewModel = viewModel,
                imageUri = imageUri,
                onClose = { navController.popBackStack() }
            )
        }
    }
}

fun loadImagesFromDevice(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Images.Media._ID)
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    context.contentResolver.query(
        collection, projection, null, null, sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val uri = ContentUris.withAppendedId(collection, id)
            imageUris.add(uri)
        }
    }
    return imageUris
}