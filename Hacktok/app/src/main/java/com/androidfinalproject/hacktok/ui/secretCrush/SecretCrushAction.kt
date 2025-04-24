package com.androidfinalproject.hacktok.ui.secretCrush

import com.androidfinalproject.hacktok.model.User

sealed class SecretCrushAction {
    object LoadCrushData : SecretCrushAction()
    object NavigateBack : SecretCrushAction()
    data class SelectUser(val user: User) : SecretCrushAction()
    data class UnselectUser(val userId: String) : SecretCrushAction()
    data class SendMessage(val userId: String, val message: String) : SecretCrushAction()
}