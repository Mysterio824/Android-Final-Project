package com.androidfinalproject.hacktok.ui.forgotPassword.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordAction
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordState

@Composable
fun VerificationCodeSection(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter the 6-digit verification code",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6-digit code input
        SixDigitCodeInput(
            code = state.verificationCode,
            onCodeChange = { onAction(ForgotPasswordAction.UpdateVerificationCode(it)) },
            error = state.verificationCodeError,
            enabled = !state.isLoading
        )

        // Resend timer
        ResendCodeTimer(state, onAction)
    }
}