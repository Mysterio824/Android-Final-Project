package com.androidfinalproject.hacktok.ui.editProfile

sealed class EditProfileAction {
    data class UpdateField(val field: String, val value: String) : EditProfileAction()
    object SaveProfile : EditProfileAction()
    object Cancel : EditProfileAction()
}