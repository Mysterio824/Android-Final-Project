package com.androidfinalproject.hacktok.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.utils.FcmDiagnosticTool
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val fcmService: FcmService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    // StateFlow for UI elements state
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Initialize by checking the current auth state
    init {
        checkAuthState()
    }

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun onAction(action: AuthAction) {
        Log.d("AuthViewModel", "onAction called: $action")
        when (action) {
            // Actions that modify UI state
            is AuthAction.UpdateEmail -> updateEmail(action.email)
            is AuthAction.UpdatePassword -> updatePassword(action.password)
            is AuthAction.UpdateConfirmPassword -> updateConfirmPassword(action.confirmPassword)
            is AuthAction.ToggleAuthMode -> toggleAuthMode()
            is AuthAction.Submit -> submitForm()
            is AuthAction.SelectLanguage -> updateLanguage(action.languageId)

            // Actions that trigger core auth logic
            is AuthAction.GoogleSignIn -> signInWithGoogle(action.idToken)

            // Actions related to navigation or higher-level state (handle where appropriate)
            is AuthAction.ForgotPassword -> { /* Trigger navigation? */ }
            is AuthAction.OnLoginSuccess -> { /* Handled by observing _authState */ }
        }
    }

    // --- Methods modifying UI State (_uiState) ---
    private fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = validateEmail(email)) }
    }

    private fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = validatePassword(password)) }
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = validateConfirmPassword(confirmPassword, _uiState.value.password)) }
    }

    private fun toggleAuthMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, email = "", password = "", confirmPassword = "", emailError = null, passwordError = null, confirmPasswordError = null) }
    }

    private fun updateLanguage(languageId: String) {
        _uiState.update { it.copy(language = languageId) }
    }

    fun resetAfterLogin() {
        viewModelScope.launch {
            _uiState.update {
                AuthUiState(
                    language = it.language,
                    isLoginSuccess = false,
                    isAdmin = false,
                    email = "",
                    password = "",
                    confirmPassword = "",
                    isLoginMode = true,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    mainError = null,
                    isLoading = false,
                    isFullInitial = true
                )
            }
        }
    }

    private fun submitForm() {
        val currentState = _uiState.value
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)
        val confirmPasswordError = if (!currentState.isLoginMode) validateConfirmPassword(currentState.confirmPassword, currentState.password) else null

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        if (emailError == null && passwordError == null && (currentState.isLoginMode || confirmPasswordError == null)) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                try {
                    if(currentState.isLoginMode){
                        val user = authService.signInWithEmail(currentState.email, currentState.password)
                        if (user != null) {
                            val isAdmin = authService.isUserAdmin(user.uid)
                            _uiState.update { it.copy(
                                isLoginSuccess = true,
                                isAdmin = isAdmin,
                                isLoading = false
                            )}

                            // Initialize FCM after login success
                            initializeFcm()
                        } else {
                            _uiState.update { it.copy(isLoading = false, mainError = "Wrong Email or Password") }
                        }
                        return@launch
                    }
                    val res = authService.signUp(currentState.email, currentState.password)
                    if(res == "Password changed successfully"){
                        return@launch
                    } else {
                        _uiState.update { it.copy(isLoading = false, mainError = res) }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, mainError = e.message ?: "Authentication failed") }
                }
            }
        }
    }

    // --- Methods modifying Core Auth State (_authState) ---
    private fun signInWithGoogle(idToken: String) {
        Log.d("AuthViewModel", "signInWithGoogle called with token: ${idToken.take(10)}...")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val user = authService.signInWithGoogle(idToken)
                if (user != null) {
                    Log.d("AuthViewModel", "Firebase Sign-In successful. User: ${user.uid}, Email: ${user.email}")
                    val isAdmin = authService.isUserAdmin(user.uid)
                    Log.d("AuthViewModel", "User admin status: $isAdmin")

                    // First update UI state, then auth state
                    _uiState.update { it.copy(
                        isLoginSuccess = true,
                        isAdmin = isAdmin,
                        isLoading = false
                    )}
                    initializeFcm()

                    Log.d("AuthViewModel", "Set AuthState to Success(isAdmin=$isAdmin)")
                } else {
                    Log.e("AuthViewModel", "Firebase Sign-In failed: User is null after repository call")
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "signInWithGoogle error", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkAuthState() {
        val currentUserId = authService.getCurrentUserIdSync()
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    val isAdmin = authService.isUserAdmin(currentUserId)
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true, isAdmin = isAdmin) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(mainError = "Failed to check user status: ${e.message}") }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false, isLoginSuccess = false, isFullInitial = true) }
        }
    }

    private fun initializeFcm() {
        Log.d("AuthViewModel", "Initializing FCM after successful login")

        // First initialize the FCM service normally
        fcmService.initialize()

        // Then run FCM diagnostic to ensure token is properly stored
        viewModelScope.launch {
            try {
                // Wait a bit to allow normal initialization to finish
                kotlinx.coroutines.delay(2000)

                // Run the diagnostic tool to verify and fix token issues
                FcmDiagnosticTool.runDiagnostic(context)

                Log.d("AuthViewModel", "FCM diagnostic scheduled")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error scheduling FCM diagnostic", e)
            }
        }
    }

    // --- Validation Methods --- (Keep as they are)
    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> context.getString(R.string.email_error_empty)
            !emailPattern.matcher(email).matches() -> context.getString(R.string.email_error_invalid)
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> context.getString(R.string.password_error_empty)
            password.length < 8 -> context.getString(R.string.password_error_length)
            !password.any { it.isDigit() } -> context.getString(R.string.password_error_number)
            !password.any { it.isUpperCase() } -> context.getString(R.string.password_error_uppercase)
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isEmpty() -> context.getString(R.string.confirm_password_error_empty)
            confirmPassword != password -> context.getString(R.string.confirm_password_error_mismatch)
            else -> null
        }
    }
}