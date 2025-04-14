package com.androidfinalproject.hacktok.ui.newStory

import android.net.Uri

sealed class NewStoryAction {
    data class GoToImageEditor(val imageUri: Uri?) : NewStoryAction()
    data object NewTextStory : NewStoryAction()
}