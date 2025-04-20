package com.androidfinalproject.hacktok.ui.storydetail

import com.androidfinalproject.hacktok.ui.post.PostDetailAction

sealed class StoryDetailAction {
    object CloseStory : StoryDetailAction()
    data class OnUserClick(val userId: String?) : StoryDetailAction()
    data class LoadStoryDetails(val userId: String? = null) : StoryDetailAction()
    object NextStory : StoryDetailAction()
    object PreviousStory : StoryDetailAction()
    object PauseStory : StoryDetailAction()
    object ResumeStory : StoryDetailAction()
    object ReportStory : StoryDetailAction()
    data class NavigateToUserProfile(val userId: String?) : StoryDetailAction()
    object DeleteStory : StoryDetailAction()
    data class ViewStory(val storyId: String) : StoryDetailAction()
    data class SendMessage(val message: String) : StoryDetailAction()
}