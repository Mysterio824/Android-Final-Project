package com.androidfinalproject.hacktok.ui.editProfile

import android.net.Uri

sealed class EditProfileAction {
    data class UpdateField(val field: String, val value: String) : EditProfileAction()
    data class UpdateAvatar(val uri: Uri) : EditProfileAction()
    object SaveProfile : EditProfileAction()
    object Cancel : EditProfileAction()
}