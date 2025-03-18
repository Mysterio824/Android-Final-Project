package com.androidfinalproject.hacktok.ui.forgotPassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.util.regex.Pattern

class ForgotPasswordViewModel : ViewModel() {
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
            is ForgotPasswordAction.UpdateVerificationCode -> updateVerificationCode(action.code)
            is ForgotPasswordAction.SendCode -> sendVerificationCode()
            is ForgotPasswordAction.ResendCode -> resendVerificationCode()
            is ForgotPasswordAction.VerifyCode -> verifyCode()
            is ForgotPasswordAction.NavigateBack -> {}
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

    private fun updateVerificationCode(code: String) {
        _state.update { currentState ->
            currentState.copy(
                verificationCode = code,
                verificationCodeError = if (code.isEmpty()) "Verification code is required" else null
            )
        }
    }

    private fun sendVerificationCode() {
        val currentState = _state.value
        val emailError = validateEmail(currentState.email)

        _state.update { it.copy(emailError = emailError) }

        if (emailError == null) {
            _state.update { it.copy(isLoading = true) }

            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)

                val emailExists = true

                if (emailExists) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isCodeSent = true
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            emailError = "Email not found"
                        )
                    }
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
                    isLoading = false,
                    verificationCode = ""
                )
            }
        }
    }

    private fun verifyCode() {
        val currentState = _state.value

        if (currentState.verificationCode.length != 6) {
            _state.update {
                it.copy(verificationCodeError = "Please enter a 6-digit code")
            }
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)

            val isCodeCorrect = true

            if (isCodeCorrect) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isCodeVerified = true
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        verificationCodeError = "Invalid code"
                    )
                }
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