package com.androidfinalproject.hacktok.ui.mainDashboard.messageDashboard.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: MutableState<String>,
    placeholderText: String = "Search",
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = Color.DarkGray
        )
    },
    textStyle: TextStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 18.sp)
) {
    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { searchQuery.value = it },
        placeholder = { Text(placeholderText, color = Color.DarkGray, fontSize = 18.sp) },
        leadingIcon = leadingIcon,
        modifier = modifier,
        shape = MaterialTheme.shapes.large, // Rounded corners
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.DarkGray,
            unfocusedContainerColor = Color(0xFFECECEC)
        ),
        textStyle = textStyle
    )
}