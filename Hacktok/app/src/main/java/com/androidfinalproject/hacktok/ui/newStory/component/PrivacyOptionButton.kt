package com.androidfinalproject.hacktok.ui.newStory.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY

@Composable
fun PrivacyOptionButton(
    selected: PRIVACY,
    onSelected: (PRIVACY) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row at the bottom start
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Privacy Options",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = selected.name.lowercase().replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Dropdown stays in box but opens from IconButton
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
        ) {
            PRIVACY.entries.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = option.name.lowercase().replaceFirstChar { it.uppercase() }
                        )
                    }
                )
            }
        }
    }
}
