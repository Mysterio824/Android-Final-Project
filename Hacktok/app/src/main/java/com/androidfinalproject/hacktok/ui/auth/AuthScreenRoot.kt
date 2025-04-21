package com.androidfinalproject.hacktok.ui.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import android.content.res.Configuration
import java.util.Locale
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    onLoginSuccess: (isAdmin: Boolean) -> Unit, // Change parameter type back
    onForgetPassword: () -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var initialLocaleHandled by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // Show auth errors or navigate on success
        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Error -> {
                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                }
                is AuthState.Success -> {
                    onLoginSuccess((authState as AuthState.Success).isAdmin)
                }
                else -> {}
            }
        }
        // Apply locale change when language is selected
        LaunchedEffect(uiState.language) {
            if (initialLocaleHandled) {
                val locale = when (uiState.language) {
                    "Français" -> Locale("fr")
                    "Español" -> Locale("es")
                    else -> Locale("en")
                }
                Locale.setDefault(locale)
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
                activity?.recreate()
            } else {
                initialLocaleHandled = true
            }
        }
        Box(modifier = Modifier.padding(paddingValues)) {
            AuthScreen(
                state = uiState,
                onGoogleSignInClicked = onGoogleSignInClicked,
                onAction = { action ->
                    if (action is AuthAction.ForgotPassword) onForgetPassword()
                    viewModel.onAction(action)
                }
            )
        }
    }
}