package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.FullReaction

private val TOTAL_TAB_KEY = R.string.total

@Composable
fun LikeListContent(
    listEmotions: List<FullReaction>,
    onUserClick: (String) -> Unit,
){
    if (listEmotions.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No reactions yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val uniqueReactionTypes = listEmotions
        .map { it.emoji }
        .distinct()
        .ifEmpty { listOf("👍") }

    val totalTabLabel = stringResource(TOTAL_TAB_KEY)
    val reactionTypes = listOf(totalTabLabel) + uniqueReactionTypes

    var selectedTab by remember { mutableStateOf(reactionTypes.first()) }

    val filteredReactions = when (selectedTab) {
        totalTabLabel -> listEmotions
        else -> listEmotions.filter {
            it.emoji == selectedTab
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 24.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = reactionTypes.indexOf(selectedTab).coerceAtLeast(0), // Ensure index is never negative
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            reactionTypes.forEach { tabKey ->
                if (tabKey == totalTabLabel) {
                    Tab(
                        selected = tabKey == selectedTab,
                        onClick = { selectedTab = tabKey }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "$totalTabLabel (${listEmotions.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (tabKey == selectedTab) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }
                } else {
                    // Reaction tabs - with icons
                    ReactionTab(
                        selected = tabKey == selectedTab,
                        onClick = { selectedTab = tabKey },
                        count = listEmotions.count { it.emoji == tabKey },
                        reactionType = tabKey
                    )
                }
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredReactions, key = { it.user.id ?: it.user.username ?: "" }) { item ->
                LikeItem(
                    username = item.user.username ?: "",
                    imageUrl = item.user.profileImage,
                    reactionType = item.emoji,
                    onClick = { item.user.id?.let { onUserClick(it) } }
                )
            }
        }
    }
}


@Composable
private fun ReactionTab(
    selected: Boolean,
    onClick: () -> Unit,
    count: Int,
    reactionType: String
) {
    Tab(
        selected = selected,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp).background(MaterialTheme.colorScheme.background)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val iconTint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Gray
            }

            Image(
                imageVector = getReactionIcon(reactionType),
                contentDescription = "Reaction $reactionType",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(iconTint)
            )

            Text(
                text = count.toString(),
                fontSize = 14.sp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}