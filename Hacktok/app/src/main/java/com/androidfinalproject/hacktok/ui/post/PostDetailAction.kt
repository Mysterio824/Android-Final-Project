package com.androidfinalproject.hacktok.ui.post

import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType

sealed class PostDetailAction {
    data class OnUserClick(val userId: String?) : PostDetailAction()
    //post action
    data class LoadPost(val postId: String?) : PostDetailAction()
    data object LoadComments : PostDetailAction()
    data object ToggleLike : PostDetailAction()
    data object Share : PostDetailAction()
    data class SubmitReport(
        val reportedItemId: String,
        val reportType: ReportType,
        val reportCause: ReportCause
    ) : PostDetailAction()

    //input comment action
    data object ToggleCommentInputFocus : PostDetailAction()
    data class UpdateCommentText(val text: String) : PostDetailAction()
    data object SubmitComment : PostDetailAction()
    //comment action
    data class SetCommentFocus(val focused: Boolean) : PostDetailAction()
    data class LikeComment(val commentId: String?) : PostDetailAction()
    data class SelectCommentToReply(val commentId: String) : PostDetailAction()
    data class DeleteComment(val commentId: String) : PostDetailAction()

    data object NavigateBack : PostDetailAction()
}