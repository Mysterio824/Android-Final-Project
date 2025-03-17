package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.ui.auth.LoginAction
import com.androidfinalproject.hacktok.ui.auth.LoginState

@Composable
fun AuthToggle(state: LoginState, onAction: (LoginAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (state.isLoginMode) "Don't have an account?" else "Already have an account?",
            style = MaterialTheme.typography.bodyMedium
        )
        TextButton(onClick = { onAction(LoginAction.ToggleAuthMode) }) {
            Text(
                text = if (state.isLoginMode) "Sign Up" else "Sign In",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
