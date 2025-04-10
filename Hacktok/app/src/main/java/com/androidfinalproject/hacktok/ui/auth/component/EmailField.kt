package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.auth.AuthAction
import com.androidfinalproject.hacktok.ui.auth.AuthAction.*

@Composable
fun EmailField(
    value: String,
    error: String?,
    updateEmail: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { updateEmail(it) },
        placeholder = { Text("Email") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = error != null,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )

    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        )
    }
}
