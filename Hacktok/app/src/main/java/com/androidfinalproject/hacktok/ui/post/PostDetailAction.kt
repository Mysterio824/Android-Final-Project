package com.androidfinalproject.hacktok.ui.post

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

sealed class PostDetailAction {
    data class OnUserClick(val userId: String?) : PostDetailAction()
    data class OnLikesShowClick(val targetId: String, val isPost: Boolean) : PostDetailAction()
    //post action
    data class LoadPost(val postId: String) : PostDetailAction()
    data object LoadComments : PostDetailAction()
    data object ToggleLike : PostDetailAction()
    data object UnLikePost : PostDetailAction()
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
    data class UnLikeComment(val commentId: String?) : PostDetailAction()
    data class SelectCommentToReply(val commentId: String) : PostDetailAction()
    data class DeleteComment(val commentId: String) : PostDetailAction()
    data class SelectCommentToHighlight(val commentId: String) : PostDetailAction()
    data class SetCommentsVisible(val visible: Boolean) : PostDetailAction()
    data object ShowShareDialog : PostDetailAction()
    data object DismissShareDialog : PostDetailAction()
    data class OnSharePost(val post: Post, val caption: String, val privacy: PRIVACY) : PostDetailAction()
    data object NavigateBack : PostDetailAction()
}