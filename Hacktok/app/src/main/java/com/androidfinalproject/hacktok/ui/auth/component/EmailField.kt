package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import com.androidfinalproject.hacktok.ui.auth.LoginAction

@Composable
fun EmailField(
    email: String,
    emailError: String?,
    onAction: (LoginAction) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = { onAction(LoginAction.UpdateEmail(it)) },
        label = { Text("Email") },
        isError = emailError != null,
        supportingText = { emailError?.let { Text(it) } },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}
