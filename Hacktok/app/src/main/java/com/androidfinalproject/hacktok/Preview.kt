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
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordScreen
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordState
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordScreen
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordState
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    LoginAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            ResetPasswordScreen(
                state = ResetPasswordState(
                    email = "user@example.com",
                    verificationCode = "123456",
                ),
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    LoginAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            ForgotPasswordScreen(
                state = ForgotPasswordState(),
                onAction = {}
            )
        }
    }
}