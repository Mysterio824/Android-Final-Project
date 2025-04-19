package com.androidfinalproject.hacktok.ui.mainDashboard.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Story

@Composable
fun StoriesSection(
    stories: List<Story>,
    onCreateStory: () -> Unit,
    onStoryClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CreateStoryItem(onCreateStory = onCreateStory)
                }

                items(stories, key = { it.id ?: it.userId }) { story ->
                    StoryItem(
                        story = story,
                        onClick = { story.id?.let { onStoryClick(it) } }
                    )
                }
            }
        }
    }
}
