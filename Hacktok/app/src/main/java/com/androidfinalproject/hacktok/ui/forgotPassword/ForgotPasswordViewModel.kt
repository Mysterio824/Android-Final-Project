package com.androidfinalproject.hacktok.ui.forgotPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    // Email validation pattern
    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UpdateEmail -> updateEmail(action.email)
            is ForgotPasswordAction.SendCode -> sendVerificationCode()
            is ForgotPasswordAction.ResendCode -> resendVerificationCode()
            else -> {}
        }
    }

    private fun updateEmail(email: String) {
        _state.update { currentState ->
            currentState.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    private fun sendVerificationCode() {
        val currentState = _state.value
        val emailError = validateEmail(currentState.email)

        _state.update { it.copy(emailError = emailError) }

        if (emailError == null) {
            _state.update { it.copy(isLoading = true, isCodeSent = true, isEmailEditable = false) }

            viewModelScope.launch {
                try {
                    val res = authService.resetPassword(currentState.email)
                    if(res == "Send request successfully"){
                        _state.update { it.copy(isLoading = false, isEmailEditable = false) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = res, isEmailEditable = false) }
                    }
                } catch(e: Exception) {
                    _state.update { it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error with server",
                        isEmailEditable = false) }
                }
            }
        }
    }

    private fun resendVerificationCode() {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email cannot be empty"
            !emailPattern.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }
    }
}