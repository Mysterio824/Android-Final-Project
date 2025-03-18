package com.androidfinalproject.hacktok

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.auth.LoginScreen
import com.androidfinalproject.hacktok.ui.auth.LoginState
import com.androidfinalproject.hacktok.ui.passRecovery.ForgotPasswordAction
import com.androidfinalproject.hacktok.ui.passRecovery.ForgotPasswordScreen
import com.androidfinalproject.hacktok.ui.passRecovery.ForgotPasswordState

@Preview
@Composable
private fun LoginScreenPreview() {
    Box(
        modifier = Modifier
            .width(400.dp)
            .height(800.dp)
    ) {
        LoginScreen(
            state = LoginState(
                email = "abc@gmail.com",
                password = "abc12345"
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordScreenPreview() {
    Box(
        modifier = Modifier
            .width(400.dp)
            .height(800.dp)
    ) {
        ForgotPasswordScreen (
            state = ForgotPasswordState(
                email = "ac",
                isEmailEditable = false,
                isCodeSent = true,
            ),
            onAction = {},
            onNavigateBack = {}
        )
    }
}