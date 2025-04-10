package com.androidfinalproject.hacktok.ui.mainDashboard.home.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun WhatsNewBar(
    query: String,
    onQueryChange: (String) -> Unit,
    upload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = query,
                onValueChange = { onQueryChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                decorationBox = { innerTextField ->
                    Column {
                        innerTextField()
                        if (query.isEmpty()) {
                            Text(
                                text = "What's happening?",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp
                        )
                    }
                },
                singleLine = true
            )

            IconButton(
                onClick = { if (query.isNotEmpty()) upload() },
                enabled = query.isNotEmpty(),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Post",
                    tint = if (query.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 2.dp,
        )
    }
}