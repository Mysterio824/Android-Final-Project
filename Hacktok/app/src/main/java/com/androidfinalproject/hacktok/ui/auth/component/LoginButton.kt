package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.auth.LoginAction
import com.androidfinalproject.hacktok.ui.auth.LoginState


@Composable
fun LoginButton(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    Button(
        onClick = { onAction(LoginAction.Submit) },
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isLoading
    ) {
        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
        else Text(if (state.isLoginMode) "Sign In" else "Create Account")
    }
}