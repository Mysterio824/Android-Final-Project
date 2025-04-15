package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.auth.AuthAction
import com.androidfinalproject.hacktok.ui.auth.AuthUiState

@Composable
fun SignUpForm(
    state: AuthUiState,
    onAction: (AuthAction) -> Unit,
    focusManager: FocusManager
) {
    Spacer(modifier = Modifier.height(30.dp))

    // Email field
    EmailField(
        value = state.email,
        error = state.emailError,
        updateEmail = { onAction(AuthAction.UpdateEmail(it)) }
    )

    // Password field
    PasswordInputField(
        value = state.password,
        errorText = state.passwordError,
        onValueChange = { onAction(AuthAction.UpdatePassword(it)) },
        imeAction = ImeAction.Next,
        isConfirm = false,
        onImeAction = {
            if (state.isLoginMode) {
                focusManager.clearFocus()
                onAction(AuthAction.Submit)
            }
        }
    )

    // Confirm Password field
    PasswordInputField(
        value = state.confirmPassword,
        errorText = state.confirmPasswordError,
        onValueChange = { onAction(AuthAction.UpdateConfirmPassword(it)) },
        imeAction = ImeAction.Done,
        isConfirm = true,
        onImeAction = {
            if (state.isLoginMode) {
                focusManager.clearFocus()
                onAction(AuthAction.Submit)
            }
        }
    )

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
            text = "Sign up",
            fontSize = 16.sp
        )
    }

}