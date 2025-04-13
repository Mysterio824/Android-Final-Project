package com.androidfinalproject.hacktok.ui.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    onLoginSuccess: (isAdmin: Boolean) -> Unit, // Change parameter type back
    onForgetPassword: () -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    // Log when the composable recomposes
    Log.d("AuthScreenRoot", "AuthScreenRoot Recomposing")

    // Collect both states
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Log the state values on recomposition
    Log.d("AuthScreenRoot", "Recomposing with authState: $authState, uiState: $uiState")

    // Handle navigation based on the core auth state
    LaunchedEffect(authState) {
        val currentAuthState = authState
        Log.d("AuthScreenRoot", "LaunchedEffect triggered. Current AuthState: $currentAuthState") // Log state change
        
        when (currentAuthState) {
            is AuthState.Success -> {
                Log.d("AuthScreenRoot", "AuthState is Success. Calling onLoginSuccess with isAdmin=${currentAuthState.isAdmin}")
                onLoginSuccess(currentAuthState.isAdmin)
            }
            is AuthState.Error -> {
                Log.e("AuthScreenRoot", "AuthState is Error: ${currentAuthState.message}")
                // Show error message
            }
            is AuthState.Loading -> {
                Log.d("AuthScreenRoot", "AuthState is Loading")
            }
            is AuthState.Initial -> {
                Log.d("AuthScreenRoot", "AuthState is Initial")
            }
            is AuthState.SignedOut -> {
                Log.d("AuthScreenRoot", "AuthState is SignedOut")
            }
        }
    }

    // Pass the UI state down to the AuthScreen
    AuthScreen(
        state = uiState, // Pass AuthUiState here
        onGoogleSignInClicked = onGoogleSignInClicked,
        onAction = viewModel::onAction // Pass the onAction reference directly
    )
}