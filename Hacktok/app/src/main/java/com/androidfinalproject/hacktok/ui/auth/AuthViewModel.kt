package com.androidfinalproject.hacktok.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

// Define the sealed class for core authentication state
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState() // Loading for the core auth process (e.g., Google sign-in)
    data class Success(val isAdmin: Boolean) : AuthState()
    object SignedOut : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    // StateFlow for core authentication status
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

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
                    val user = if (currentState.isLoginMode) {
                        authRepository.signInWithEmail(currentState.email, currentState.password)
                    } else {
                        authRepository.createUserWithEmail(currentState.email, currentState.password)
                    }

                    if (user != null) {
                        val isAdmin = authRepository.isUserAdmin(user.uid)
                        _uiState.update { it.copy(
                            isLoginSuccess = true,
                            isAdmin = isAdmin,
                            isLoading = false
                        )}
                        _authState.value = AuthState.Success(isAdmin = isAdmin)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        _authState.value = AuthState.Error(
                            if (currentState.isLoginMode) "Sign in failed" else "Account creation failed"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false) }
                    _authState.value = AuthState.Error(e.message ?: "Authentication failed")
                }
            }
        }
    }
    
    // --- Methods modifying Core Auth State (_authState) ---
    private fun signInWithGoogle(idToken: String) {
        Log.d("AuthViewModel", "signInWithGoogle called with token: ${idToken.take(10)}...")
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading 
                _uiState.update { it.copy(isLoading = true) } 
                
                val user = authRepository.signInWithGoogle(idToken)
                if (user != null) {
                    Log.d("AuthViewModel", "Firebase Sign-In successful. User: ${user.uid}, Email: ${user.email}")
                    val isAdmin = authRepository.isUserAdmin(user.uid)
                    Log.d("AuthViewModel", "User admin status: $isAdmin")
                    
                    // First update UI state, then auth state
                    _uiState.update { it.copy(
                        isLoginSuccess = true,
                        isAdmin = isAdmin,
                        isLoading = false
                    )}
                    
                    // Important: Set auth state after UI state is updated
                    _authState.value = AuthState.Success(isAdmin = isAdmin)
                    Log.d("AuthViewModel", "Set AuthState to Success(isAdmin=$isAdmin)")
                } else {
                    Log.e("AuthViewModel", "Firebase Sign-In failed: User is null after repository call")
                    _authState.value = AuthState.Error("Google Sign-In failed: User is null")
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "signInWithGoogle error", e)
                _authState.value = AuthState.Error(e.message ?: "Unknown Google Sign-In error")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkAuthState() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
             viewModelScope.launch {
                // Consider showing loading state while checking admin status
                // _authState.value = AuthState.Loading 
                try {
                    val isAdmin = authRepository.isUserAdmin(currentUser.uid)
                    _authState.value = AuthState.Success(isAdmin = isAdmin)
                } catch (e: Exception) {
                     _authState.value = AuthState.Error("Failed to check user status: ${e.message}")
                }
             }
        } else {
            _authState.value = AuthState.SignedOut
        }
    }
    
    // --- Validation Methods --- (Keep as they are)
    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email cannot be empty"
            !emailPattern.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
}
