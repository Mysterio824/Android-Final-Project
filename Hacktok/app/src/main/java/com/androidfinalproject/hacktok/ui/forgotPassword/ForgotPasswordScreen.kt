package com.androidfinalproject.hacktok.ui.forgotPassword

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    // Timer for cooldown period
    var remainingSeconds by remember { mutableStateOf(0) }
    var lastEmail by remember { mutableStateOf("") }

    // Start cooldown timer when code is sent
    LaunchedEffect(state.isCodeSent) {
        if (state.isCodeSent && state.email == lastEmail) {
            remainingSeconds = 60 // 1 minute cooldown
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }
        }
        if (state.isCodeSent) {
            lastEmail = state.email
        }
    }

    // Reset timer when email changes
    LaunchedEffect(state.email) {
        if (state.email != lastEmail && state.isCodeSent) {
            remainingSeconds = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Your Password", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onAction(ForgotPasswordAction.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Please enter your email address to search for your account.",
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(ForgotPasswordAction.UpdateEmail(it)) },
                label = { Text("Email") },
                placeholder = { Text("Enter your email address") },
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                enabled = state.isEmailEditable && !state.isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Error message
            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onAction(ForgotPasswordAction.NavigateBack) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text("Cancel")
                }

                val isButtonEnabled = !state.isLoading &&
                        state.emailError == null &&
                        state.email.isNotEmpty() &&
                        (remainingSeconds == 0 || state.email != lastEmail)

                Button(
                    onClick = {
                        if (state.isCodeSent) {
                            onAction(ForgotPasswordAction.ResendCode)
                        } else {
                            onAction(ForgotPasswordAction.SendCode)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isButtonEnabled
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (state.isCodeSent) {
                                if (remainingSeconds > 0) {
                                    "Resend (${formatTime(remainingSeconds)})"
                                } else {
                                    "Resend Code"
                                }
                            } else {
                                "Search"
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainderSeconds = seconds % 60
    return String.format("%01d:%02d", minutes, remainderSeconds)
}

@Preview
@Composable
private fun PreviewScreen(){
    MainAppTheme {
        Box{
            ForgotPasswordScreen(
                state = ForgotPasswordState(
                    isCodeSent = true
                ),
                onAction = {}
            )
        }
    }
}