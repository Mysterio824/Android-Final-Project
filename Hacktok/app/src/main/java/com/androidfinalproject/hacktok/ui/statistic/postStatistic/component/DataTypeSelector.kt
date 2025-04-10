package com.androidfinalproject.hacktok.ui.statistic.postStatistic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.statistic.postStatistic.PostDataType


@Composable
fun DataTypeSelector(
    selectedDataType: PostDataType,
    onDataTypeSelected: (PostDataType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Data Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { expanded = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedDataType) {
                            PostDataType.ALL_POSTS -> "All Posts"
                            PostDataType.BANNED_POSTS -> "Banned Posts"
                            PostDataType.BOTH -> "Both"
                        }
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select data type")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("All Posts") },
                        onClick = {
                            onDataTypeSelected(PostDataType.ALL_POSTS)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Banned Posts") },
                        onClick = {
                            onDataTypeSelected(PostDataType.BANNED_POSTS)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Both") },
                        onClick = {
                            onDataTypeSelected(PostDataType.BOTH)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}