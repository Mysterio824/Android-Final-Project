package com.androidfinalproject.hacktok.ui.forgotPassword.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordAction
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordState
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay


@Composable
fun ResendCodeTimer(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit
) {
    // Timer for resend code
    val timeLeft = remember { mutableIntStateOf(60) }
    val canResend = remember { mutableStateOf(false) }

    // This approach avoids using awaitFrame() which might be causing issues
    LaunchedEffect(state.isCodeSent) {
        if (state.isCodeSent) {
            // Start with canResend = false, timeLeft = 60
            canResend.value = false
            timeLeft.intValue = 60

            // Simple countdown timer
            while (timeLeft.intValue > 0) {
                delay(1000)
                timeLeft.intValue--
            }

            // Enable resend after countdown
            canResend.value = true
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    if (!canResend.value && timeLeft.intValue > 0) {
        Text(
            text = "Resend code in ${timeLeft.intValue} seconds",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Didn't receive code? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = { onAction(ForgotPasswordAction.ResendCode) },
                enabled = !state.isLoading && canResend.value,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Resend",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}