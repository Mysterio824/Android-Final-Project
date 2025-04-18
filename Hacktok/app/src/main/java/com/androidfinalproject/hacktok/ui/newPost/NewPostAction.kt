package com.androidfinalproject.hacktok.ui.newPost

import android.net.Uri

sealed class NewPostAction {
    data class UpdateCaption(val caption: String) : NewPostAction()
    data class UpdatePrivacy(val privacy: PRIVACY) : NewPostAction()
    data object UpdateImage : NewPostAction()
    data class UpdateImageUri(val uri: Uri) : NewPostAction()
    data object ClearSubmissionState : NewPostAction()
    data object SubmitPost : NewPostAction()
    data object RemoveImage : NewPostAction()
    data object Close : NewPostAction()
}