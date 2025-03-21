package com.androidfinalproject.hacktok.ui.post

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.friendList.FriendListAction
import org.bson.types.ObjectId


sealed class PostDetailAction {
    object ToggleCommentSection : PostDetailAction()
    object ToggleLike : PostDetailAction()
    object Share : PostDetailAction()
    data class UpdateCommentText(val text: String) : PostDetailAction()
    object SubmitComment : PostDetailAction()
    data class OnUserClick(val user: User) : PostDetailAction()
    object KeyboardShown : PostDetailAction()
    object KeyboardHidden : PostDetailAction()
    data class LoadPost(val postId: ObjectId?) : PostDetailAction()
    object LoadComments : PostDetailAction()
    data object NavigateBack : PostDetailAction()
}
