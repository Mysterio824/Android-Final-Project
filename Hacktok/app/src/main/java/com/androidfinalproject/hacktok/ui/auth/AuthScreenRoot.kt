package com.androidfinalproject.hacktok.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import android.app.Activity
import android.content.Context
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.androidfinalproject.hacktok.R

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    onForgetPassword: () -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Track if initial locale was handled to prevent unnecessary recreation
    var initialLocaleHandled by remember { mutableStateOf(false) }

    // Track if login success has been handled to prevent multiple triggers
    var loginHandled by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // Handle authentication state changes
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess && !loginHandled) {
                loginHandled = true
                onLoginSuccess(uiState.isAdmin)

                viewModel.resetAfterLogin()
            }
        }

        // Handle UI errors separately
        LaunchedEffect(uiState.mainError) {
            if (!uiState.mainError.isNullOrEmpty()) {
                snackbarHostState.showSnackbar(uiState.mainError!!)
            }
        }

        // Apply locale change when language is selected
        LaunchedEffect(uiState.language) {
            if (initialLocaleHandled && activity != null) {
                updateAppLocale(uiState.language, context, activity)
            } else {
                initialLocaleHandled = true
            }
        }

        if(uiState.isFullInitial){
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
}

private fun updateAppLocale(language: String, context: Context, activity: Activity) {
    val locale = when (language) {
        context.getString(R.string.language_vietnamese) -> Locale("vi")
        else -> Locale("en")
    }

    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    activity.recreate()
}