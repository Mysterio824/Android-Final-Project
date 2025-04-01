package com.androidfinalproject.hacktok.ui.post

sealed class PostDetailAction {
    object ToggleLike : PostDetailAction()
    object Share : PostDetailAction()
    data class UpdateCommentText(val text: String) : PostDetailAction()
    object SubmitComment : PostDetailAction()
    data class OnUserClick(val userId: String?) : PostDetailAction()
    object ToggleCommentInputFocus : PostDetailAction()
    data class SetCommentFocus(val focused: Boolean) : PostDetailAction()
    data class LikeComment(val commentId: String?) : PostDetailAction()
    data class LoadPost(val postId: String?) : PostDetailAction()
    object LoadComments : PostDetailAction()
    object NavigateBack : PostDetailAction()
}
