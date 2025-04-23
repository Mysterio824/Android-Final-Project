package com.androidfinalproject.hacktok.ui.editProfile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                onAction(EditProfileAction.UpdateAvatar(uri))
            }
        }
    )

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
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(EditProfileAction.Cancel) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F2F5))
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(Color.White)
                    ) {
                        // Profile image container with edit icon
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .offset(y = (-10).dp)
                        ) {
                            // The ProfileImage is created outside, so we'll wrap it with our edit icon
                            Box {
                                ProfileImage(
                                    imageUrl = state.avatarUrl,
                                    size = 90.dp,
                                    onClick = onImageClick
                                )

                                // Pencil edit icon in bottom right corner
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1877F2))
                                        .border(2.dp, Color.White, CircleShape)
                                        .clickable(onClick = onImageClick)
                                        .zIndex(2f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile Picture",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Profile Information Form
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Profile Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            ProfileTextField(
                                label = "Username",
                                value = state.username,
                                onValueChange = { onAction(EditProfileAction.UpdateField("username", it)) },
                                isError = state.errorState["username"] == true
                            )

                            ProfileTextField(
                                label = "Full Name",
                                value = state.fullName,
                                onValueChange = { onAction(EditProfileAction.UpdateField("fullName", it)) },
                                isError = state.errorState["fullName"] == true
                            )

                            ProfileTextField(
                                label = "Email",
                                value = state.email,
                                onValueChange = { onAction(EditProfileAction.UpdateField("email", it)) },
                                isError = state.errorState["email"] == true
                            )

                            ProfileTextField(
                                label = "Bio",
                                value = state.bio,
                                onValueChange = { onAction(EditProfileAction.UpdateField("bio", it)) },
                                isError = state.errorState["bio"] == true,
                                singleLine = false,
                                maxLines = 6,  // Increased max lines
                                minHeight = 120.dp  // Added minimum height for the field
                            )

                            state.errorMessage?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedButton(
                                    onClick = { onAction(EditProfileAction.Cancel) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = { onAction(EditProfileAction.SaveProfile) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1877F2) // Facebook blue
                                    )
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                }

                // Loading overlay
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minHeight: Dp = 56.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1877F2),
                unfocusedBorderColor = Color.LightGray
            )
        )

        if (isError) {
            Text(
                text = "This field is required",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditUserPreview() {
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
                    errorState = emptyMap(),
                    isLoading = false,
                    avatarUrl = user.profileImage ?: ""
                ),
                onAction = {}
            )
        }
    }
}