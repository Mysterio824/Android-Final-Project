package com.androidfinalproject.hacktok.ui.changePassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.changePassword.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    state: ChangePasswordState,
    onAction: (ChangePasswordAction) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ChangePasswordAction.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1877F2),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Description text
            Text(
                text = "Create a new password that is secure and easy to remember",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Current password field
            PasswordTextField(
                value = state.oldPassword,
                onValueChange = { onAction(ChangePasswordAction.UpdateOldPassword(it)) },
                label = "Current Password",
                isVisible = state.oldPasswordVisible,
                onVisibilityToggle = { onAction(ChangePasswordAction.ToggleOldPasswordVisibility) },
                errorMessage = state.oldPasswordError
            )

            // New password field
            PasswordTextField(
                value = state.newPassword,
                onValueChange = { onAction(ChangePasswordAction.UpdateNewPassword(it)) },
                label = "New Password",
                isVisible = state.newPasswordVisible,
                onVisibilityToggle = { onAction(ChangePasswordAction.ToggleNewPasswordVisibility) },
                errorMessage = state.newPasswordError
            )

            // Show password requirements
            if (state.newPassword.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Your password must include:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        PasswordRequirementsList(requirements = state.passwordRequirements)
                    }
                }
            }

            // Confirm password field
            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = { onAction(ChangePasswordAction.UpdateConfirmPassword(it)) },
                label = "Confirm New Password",
                isVisible = state.confirmPasswordVisible,
                onVisibilityToggle = { onAction(ChangePasswordAction.ToggleConfirmPasswordVisibility) },
                errorMessage = state.confirmPasswordError
            )

            // Error message
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Update password button
            Button(
                onClick = { onAction(ChangePasswordAction.ResetPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isFormValid && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2) // Facebook blue
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Update Password", modifier = Modifier.padding(vertical = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}