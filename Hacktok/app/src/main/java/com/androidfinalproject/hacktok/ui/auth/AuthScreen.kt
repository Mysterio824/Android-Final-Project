package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.ui.auth.component.*
import androidx.compose.foundation.clickable

@Composable
fun AuthScreen(
    state: AuthUiState,
    onAction: (AuthAction) -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(enabled = state.isLoading) { /* Consume clicks when loading */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LanguageSelector(
                language = state.language,
                onLanguageSelect = { onAction(AuthAction.SelectLanguage(it)) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Facebook logo
            Icon(
                painter = painterResource(id = R.drawable.hacktok_logo),
                contentDescription = "HackTok Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF1877F2))
                    .padding(16.dp)
            )

            if(state.isLoginMode) {
                LoginForm(
                    state = state,
                    onAction = onAction,
                    focusManager = focusManager
                )
            } else {
                SignUpForm(
                    state = state,
                    onAction = onAction,
                    focusManager = focusManager
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "OR",
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            GoogleSignInButton(
                onClick = onGoogleSignInClicked,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Create new account button
            Button(
                onClick = { onAction(AuthAction.ToggleAuthMode) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42B72A),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (state.isLoginMode) "Create new account" else "Back to login",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Optional: Show a loading indicator overlay based on uiState.isLoading
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FacebookAuthScreenPreview() {
    MaterialTheme {
        AuthScreen(
            onAction = {},
            onGoogleSignInClicked = {},
            state = AuthUiState(
                isLoginMode = false,
                isLoading = true
            )
        )
    }
}