package com.androidfinalproject.hacktok.ui.editProfile

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.editProfile.component.CustomTextField
import com.androidfinalproject.hacktok.ui.editProfile.component.DropdownField
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

private const val TAG = "EditProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // 📸 Image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                onAction(EditProfileAction.UpdateAvatar(uri))
            }
        }
    )

    // 🔐 Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            }
        }
    )

    val onImageClick = {
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { onAction(EditProfileAction.Cancel) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Edit profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Box {
                            ProfileImage(
                                imageUrl = state.avatarUrl,
                                size = 40.dp,
                                onClick = onImageClick
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(24.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                IconButton(
                                    onClick = onImageClick,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Upload avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        label = "Username",
                        value = state.username,
                        isError = state.errorState["username"] ?: false,
                        onValueChange = { onAction(EditProfileAction.UpdateField("username", it)) }
                    )

                    CustomTextField(
                        label = "Full Name",
                        value = state.fullName,
                        isError = state.errorState["fullName"] ?: false,
                        onValueChange = { onAction(EditProfileAction.UpdateField("fullName", it)) }
                    )

                    CustomTextField(
                        label = "Email",
                        value = state.email,
                        isError = state.errorState["email"] ?: false,
                        onValueChange = { onAction(EditProfileAction.UpdateField("email", it)) }
                    )

                    CustomTextField(
                        label = "Bio",
                        value = state.bio,
                        isError = state.errorState["bio"] ?: false,
                        onValueChange = { onAction(EditProfileAction.UpdateField("bio", it)) }
                    )

                    DropdownField(
                        label = "Role",
                        selectedValue = state.role.name,
                        options = UserRole.entries.map { it.name },
                        onValueChange = { onAction(EditProfileAction.UpdateField("role", it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onAction(EditProfileAction.Cancel) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Color.Black)
                        }

                        Button(
                            onClick = { onAction(EditProfileAction.SaveProfile) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)), // Orange
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            enabled = !state.isLoading
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUserPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val user = MockData.mockUsers.first()

            EditProfileScreen(
                state = EditProfileState(
                    username = user.username ?: "",
                    fullName = user.fullName ?: "Unknown",
                    email = user.email,
                    bio = user.bio ?: "",
                    role = user.role,
                    errorState = emptyMap(),
                    isLoading = false,
                    avatarUrl = user.profileImage ?: ""
                ),
                onAction = {}
            )
        }
    }
}