package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.ui.auth.LoginAction


@Composable
fun ForgotPassword(onAction: (LoginAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = { onAction(LoginAction.ForgotPassword) }) {
            Text("Forgot Password?")
        }
    }
}