//package com.androidfinalproject.hacktok.ui.adminManage
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import com.androidfinalproject.hacktok.model.MockData
//import com.androidfinalproject.hacktok.model.User
//
//class AdminManagementViewModel : ViewModel() {
//    var state by mutableStateOf(AdminManagementState(
//        users = MockData.mockUsers,
//        posts = MockData.mockPosts,
//        comments = MockData.mockComments
//    ))
//        private set
//
//    fun onAction(action: AdminManagementAction) {
//        when (action) {
//            is AdminManagementAction.UpdateUserRole -> {
//                state = state.copy(
//                    users = state.users.map { user ->
//                        if (user.id?.equals(action.userId) == true) user.copy(role = action.newRole) else user
//                    }
//                )
//            }
//            is AdminManagementAction.DeleteUser -> {
//                state = state.copy(
//                    users = state.users.filter { it.id?.equals(action.userId) == false }
//                )
//            }
//            is AdminManagementAction.CreatePost -> {
//                val newPost = MockData.mockPosts.first()
//                state = state.copy(
//                    posts = state.posts + newPost,
//                    isCreatePostDialogOpen = false
//                )
//            }
//            is AdminManagementAction.EditPost -> {
//                state = state.copy(
//                    posts = state.posts.map { post ->
//                        if (post.id?.equals(action.postId) == true) post.copy(content = action.newContent) else post
//                    },
//                    isEditPostDialogOpen = false,
//                    postToEdit = null
//                )
//            }
//            is AdminManagementAction.DeletePost -> {
//                state = state.copy(
//                    posts = state.posts.filter { it.id?.equals(action.postId) == false }
//                )
//            }
////            is AdminManagementAction.EditComment -> {
////                state = state.copy(
////                    comments = state.comments.map { comment ->
////                        if (comment.id?.equals(action.commentId) == true) comment.copy(comment = action.newContent) else comment
////                    },
////                    isEditCommentDialogOpen = false,
////                    commentToEdit = null
////                )
////            }
//            is AdminManagementAction.DeleteComment -> {
//                state = state.copy(
//                    comments = state.comments.filter { it.id?.equals(action.commentId) == false }
//                )
//            }
//            is AdminManagementAction.SelectTab -> {
//                state = state.copy(selectedTab = action.tabIndex)
//            }
//            is AdminManagementAction.OpenCreatePostDialog -> {
//                state = state.copy(isCreatePostDialogOpen = true)
//            }
//            is AdminManagementAction.CloseCreatePostDialog -> {
//                state = state.copy(isCreatePostDialogOpen = false)
//            }
//            is AdminManagementAction.OpenEditPostDialog -> {
//                state = state.copy(isEditPostDialogOpen = true, postToEdit = action.post)
//            }
//            is AdminManagementAction.CloseEditPostDialog -> {
//                state = state.copy(isEditPostDialogOpen = false, postToEdit = null)
//            }
//            is AdminManagementAction.OpenEditCommentDialog -> {
//                state = state.copy(isEditCommentDialogOpen = true, commentToEdit = action.comment)
//            }
//            is AdminManagementAction.CloseEditCommentDialog -> {
//                state = state.copy(isEditCommentDialogOpen = false, commentToEdit = null)
//            }
//        }
//    }
//}