package com.androidfinalproject.hacktok.ui.forgotPassword.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordAction
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordState

@Composable
fun SubmitButton(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    Button(
        onClick = {
            if (state.isCodeSent) {
                onAction(ForgotPasswordAction.VerifyCode)
            } else {
                onAction(ForgotPasswordAction.SendCode)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = !state.isLoading && (
                (!state.isCodeSent && state.email.isNotEmpty()) ||
                        (state.isCodeSent && state.verificationCode.length == 6)
                ),
        shape = MaterialTheme.shapes.small
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = if (state.isCodeSent) "Verify Code" else "Send Code",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}