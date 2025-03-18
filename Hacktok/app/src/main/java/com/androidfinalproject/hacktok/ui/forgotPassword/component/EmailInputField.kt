package com.androidfinalproject.hacktok.ui.forgotPassword.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordAction
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordState

@Composable
fun EmailInputField(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Enter your email address",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(ForgotPasswordAction.UpdateEmail(it)) },
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let { Text(it) }
            },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (!state.isCodeSent) {
                        onAction(ForgotPasswordAction.SendCode)
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isCodeSent || state.isEmailEditable
        )
    }
}