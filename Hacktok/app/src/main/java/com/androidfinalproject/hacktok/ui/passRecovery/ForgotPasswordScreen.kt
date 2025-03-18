package com.androidfinalproject.hacktok.ui.passRecovery

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

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
                // Header
                Text(
                    text = "Forgot Password",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email field (always visible)
                EmailInputField(state, onAction, focusManager)

                // Verification code section (appears after email submission)
                if (state.isCodeSent) {
                    VerificationCodeSection(state, onAction, focusManager)
                }

                // Submit button (changes based on state)
                SubmitButton(state, onAction)
            }
        }
    }
}

@Composable
private fun EmailInputField(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Enter your email address",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { onAction(ForgotPasswordAction.UpdateEmail(it)) },
            label = { Text("Email") },
            isError = state.emailError != null,
            supportingText = {
                state.emailError?.let { Text(it) }
            },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (!state.isCodeSent) {
                        onAction(ForgotPasswordAction.SendCode)
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !state.isCodeSent || state.isEmailEditable
        )
    }
}

@Composable
private fun VerificationCodeSection(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter the 6-digit verification code",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6-digit code input
        SixDigitCodeInput(
            code = state.verificationCode,
            onCodeChange = { onAction(ForgotPasswordAction.UpdateVerificationCode(it)) },
            error = state.verificationCodeError,
            enabled = !state.isLoading
        )

        // Resend timer
        ResendCodeTimer(state, onAction)
    }
}

@Composable
private fun SixDigitCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    error: String?,
    enabled: Boolean
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(300) // Small delay to ensure the UI is ready
        focusRequester.requestFocus()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Create 6 digit boxes
            repeat(6) { index ->
                val digit = if (index < code.length) code[index].toString() else ""
                val isFocused = code.length == index

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(
                            width = 2.dp,
                            color = when {
                                error != null -> MaterialTheme.colorScheme.error
                                isFocused -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.outline
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Hidden actual input field
        OutlinedTextField(
            value = code,
            onValueChange = { newValue ->
                // Only accept digits and limit to 6 characters
                if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                    onCodeChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .size(0.dp)
                .focusRequester(focusRequester),
            enabled = enabled
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ResendCodeTimer(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    // Timer for resend code
    val timeLeft = remember { mutableIntStateOf(60) }
    val canResend = remember { mutableStateOf(false) }

    LaunchedEffect(state.isCodeSent) {
        if (state.isCodeSent) {
            timeLeft.intValue = 60
            canResend.value = false
            while (timeLeft.intValue > 0) {
                delay(1000)
                timeLeft.intValue--
            }
            canResend.value = true
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!canResend.value) {
        Text(
            text = "Resend code in ${timeLeft.intValue} seconds",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Didn't receive code? ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { onAction(ForgotPasswordAction.ResendCode) },
                enabled = !state.isLoading,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Resend",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SubmitButton(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    Button(
        onClick = {
            if (state.isCodeSent) {
                onAction(ForgotPasswordAction.VerifyCode)
            } else {
                onAction(ForgotPasswordAction.SendCode)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = !state.isLoading && (
                (!state.isCodeSent && state.email.isNotEmpty()) ||
                        (state.isCodeSent && state.verificationCode.length == 6)
                ),
        shape = MaterialTheme.shapes.small
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = if (state.isCodeSent) "Verify Code" else "Send Code",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}