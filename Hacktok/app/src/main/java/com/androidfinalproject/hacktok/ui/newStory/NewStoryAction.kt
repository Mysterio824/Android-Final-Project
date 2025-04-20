package com.androidfinalproject.hacktok.ui.newStory

import android.net.Uri
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class NewStoryAction {
    data class GoToImageEditor(val imageUri: Uri?) : NewStoryAction()
    data object NewTextStory : NewStoryAction()
    data class UpdatePrivacy(val privacy: PRIVACY) : NewStoryAction()
    data class UpdateText(val text: String) : NewStoryAction()
    data class CreateImageStory(val imageUri: Uri?, val privacy: PRIVACY) : NewStoryAction()
    data class CreateTextStory(val text: String, val privacy: PRIVACY) : NewStoryAction()
    data object NavigateBack : NewStoryAction()
    object ResetState : NewStoryAction()
}