package com.androidfinalproject.hacktok.ui.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            is AuthAction.UpdateVerificationCode -> updateVerificationCode(action.code)
            is AuthAction.UpdateUsername -> updateUsername(action.username)
            is AuthAction.ToggleAuthMode -> toggleAuthMode()
            is AuthAction.Submit -> submitForm()
            is AuthAction.SubmitVerificationCode -> submitVerificationCode()
            is AuthAction.SubmitUsername -> submitUsername()
            is AuthAction.ResendVerificationCode -> resendVerificationCode()
            is AuthAction.SelectLanguage -> updateLanguage(action.languageId)

            // Actions that trigger core auth logic
            is AuthAction.GoogleSignIn -> signInWithGoogle(action.idToken)

            // Actions related to navigation or higher-level state (handle where appropriate)
            else -> {}
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

    private fun updateVerificationCode(code: String) {
        _uiState.update { it.copy(verificationCode = code) }
    }
    
    private fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
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
                    
                    // Handle signup
                    val res = authService.signUp(currentState.email, currentState.password)
                    if(res.contains("Verification code sent")){
                        // Show verification screen
                        _uiState.update { it.copy(
                            isLoading = false,
                            isVerificationCodeSent = true,
                            mainError = null
                        )}
                    } else {
                        _uiState.update { it.copy(isLoading = false, mainError = res) }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, mainError = e.message ?: "Authentication failed") }
                }
            }
        }
    }
    
    private fun submitVerificationCode() {
        val currentState = _uiState.value
        
        if (currentState.verificationCode.length != 6) {
            _uiState.update { it.copy(mainError = "Please enter the 6-digit code") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, mainError = null) }
        
        viewModelScope.launch {
            try {
                val verified = authService.verifyCode(currentState.email, currentState.verificationCode)
                
                if (verified) {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isVerified = true,
                        mainError = null
                    )}
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        mainError = "Invalid verification code. Please try again."
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    mainError = "Error verifying code: ${e.message ?: "Unknown error"}"
                )}
            }
        }
    }
    
    private fun submitUsername() {
        val currentState = _uiState.value
        
        if (currentState.username.length < 3) {
            _uiState.update { it.copy(mainError = "Username must be at least 3 characters") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, mainError = null) }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true)}
                val success = authService.setUsername(currentState.email, currentState.username)
                
                if (success) {
                    val user = authService.signInWithEmail(currentState.email, currentState.password)
                    if (user != null) {
                        val isAdmin = authService.isUserAdmin(user.uid)
                        _uiState.update { it.copy(
                            isLoginSuccess = true,
                            isAdmin = isAdmin,
                            isLoading = false,
                            mainError = null
                        )}

                        // Initialize FCM after login success
                        initializeFcm()
                    } else {
                        _uiState.update { it.copy(isLoading = false, isVerified = true) }
                    }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false,
                        mainError = "Failed to set username. Please try again."
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    mainError = "Error setting username: ${e.message ?: "Unknown error"}"
                )}
            }
        }
    }
    
    private fun resendVerificationCode() {
        val currentState = _uiState.value
        
        _uiState.update { it.copy(isLoading = true, mainError = null) }
        
        viewModelScope.launch {
            try {
                val result = authService.resendVerificationCode(currentState.email)
                _uiState.update { it.copy(
                    isLoading = false,
                    mainError = result
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    mainError = "Error requesting new verification code: ${e.message ?: "Unknown error"}"
                )}
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