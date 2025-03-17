package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.auth.component.*

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        val focusManager = LocalFocusManager.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LoginHeader(state)
                EmailField(state.email, state.emailError, onAction)
                PasswordInputField(value = state.password,
                    label = "Password",
                    isError = state.passwordError != null,
                    errorText = state.passwordError,
                    onValueChange = { onAction(LoginAction.UpdatePassword(it)) },
                    imeAction = if (state.isLoginMode) ImeAction.Done else ImeAction.Next,
                    onImeAction = {
                        if (state.isLoginMode) {
                            focusManager.clearFocus()
                            onAction(LoginAction.Submit)
                        }
                    })

                if (!state.isLoginMode) {
                    PasswordInputField(value = state.confirmPassword,
                        label = "Confirm Password",
                        isError = state.confirmPasswordError != null,
                        errorText = state.confirmPasswordError,
                        onValueChange = { onAction(LoginAction.UpdateConfirmPassword(it)) },
                        imeAction = ImeAction.Done,
                        onImeAction = {
                            focusManager.clearFocus()
                            onAction(LoginAction.Submit)
                        })
                }

                if (state.isLoginMode) ForgotPassword(onAction)
                LoginButton(state, onAction)
                //Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text("OR", modifier = Modifier.padding(horizontal = 16.dp))
                    Divider(modifier = Modifier.weight(1f))
                }

                GoogleSignInButton(
                    onClick = { onAction(LoginAction.GoogleSignIn) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                )
                AuthToggle(state, onAction)
            }
        }
    }
}