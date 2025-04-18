package com.androidfinalproject.hacktok.ui.storydetail

sealed class StoryDetailAction {
    data class SendMessage(val message: String) : StoryDetailAction()
    data object CloseStory : StoryDetailAction()
    data object LoadStoryDetails : StoryDetailAction()
    data object NextStory : StoryDetailAction()
    data object PreviousStory : StoryDetailAction()
    data object PauseStory : StoryDetailAction()
    data object ResumeStory : StoryDetailAction()
    data object ReportStory : StoryDetailAction()
    data object DeleteStory : StoryDetailAction()
    data object ViewStory : StoryDetailAction()
    data class NavigateToUserProfile(val userId: String?) : StoryDetailAction()
}