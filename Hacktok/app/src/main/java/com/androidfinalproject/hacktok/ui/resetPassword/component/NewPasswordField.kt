package com.androidfinalproject.hacktok.ui.resetPassword.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordAction
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordState

@Composable
fun NewPasswordField(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    OutlinedTextField(
        value = state.newPassword,
        onValueChange = { onAction(ResetPasswordAction.UpdateNewPassword(it)) },
        label = { Text("New Password") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (state.newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        trailingIcon = {
            IconButton(onClick = { onAction(ResetPasswordAction.ToggleNewPasswordVisibility) }) {
                Icon(
                    imageVector = if (state.newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (state.newPasswordVisible) "Hide password" else "Show password"
                )
            }
        },
        isError = state.newPasswordError != null,
        supportingText = {
            if (state.newPasswordError != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = state.newPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    )
}