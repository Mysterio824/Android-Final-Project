package com.androidfinalproject.hacktok.ui.currentProfile

import org.bson.types.ObjectId

sealed class CurrentProfileAction {
//    display user information
//    manage posts
//    setting to change information, password
//    log out button
    data object GoToEditing : CurrentProfileAction()
}