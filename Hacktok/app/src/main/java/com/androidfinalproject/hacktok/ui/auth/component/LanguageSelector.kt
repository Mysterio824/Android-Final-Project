package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import com.androidfinalproject.hacktok.R

@Composable
fun LanguageSelector(
    language: String,
    onLanguageSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf(
        stringResource(R.string.language_english),
        stringResource(R.string.language_vietnamese)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        TextButton(onClick = { expanded = true }) {
            Text(text = language, color = Color.Gray)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Select language",
                tint = Color.Gray
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            languages.forEach { lang ->
                DropdownMenuItem(
                    text = { Text(text = lang) },
                    onClick = {
                        onLanguageSelect(lang)
                        expanded = false
                    }
                )
            }
        }
    }
}