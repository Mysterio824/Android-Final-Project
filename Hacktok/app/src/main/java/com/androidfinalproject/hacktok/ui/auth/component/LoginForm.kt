package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.auth.AuthAction
import com.androidfinalproject.hacktok.ui.auth.AuthUiState

@Composable
fun LoginForm (
    state : AuthUiState,
    onAction: (AuthAction) -> Unit,
    focusManager: FocusManager
) {
    Spacer(modifier = Modifier.height(40.dp))

    EmailField(
        value = state.email,
        error = state.emailError,
        updateEmail = { txt -> onAction(AuthAction.UpdateEmail(txt)) }
    )

    // Password field
    PasswordInputField(
        value = state.password,
        errorText = state.passwordError,
        onValueChange = { onAction(AuthAction.UpdatePassword(it)) },
        isConfirm = false,
        imeAction = ImeAction.Done,
        onImeAction = {
            focusManager.clearFocus()
            onAction(AuthAction.Submit)
        }
    )

    // Login button
    Button(
        onClick = { onAction(AuthAction.Submit) },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1877F2),
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Log in",
            fontSize = 16.sp
        )
    }

    // Forgot password link
    TextButton(
        onClick = { onAction(AuthAction.ForgotPassword) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Forgot password?",
            color = Color(0xFF1877F2),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}