package com.androidfinalproject.hacktok.ui.secretcrush

import com.androidfinalproject.hacktok.model.User

sealed class SecretCrushAction {
    data object NavigateBack : SecretCrushAction()
    data object NavigateToSelectCrush : SecretCrushAction()
    data class SelectUser(val user: User) : SecretCrushAction()
    data class UnselectUser(val userId: String) : SecretCrushAction()
    data class SendMessage(val userId: String, val message: String) : SecretCrushAction()
    data object LoadCrushData : SecretCrushAction()
}