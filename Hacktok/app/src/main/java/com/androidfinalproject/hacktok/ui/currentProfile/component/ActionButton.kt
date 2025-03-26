package com.androidfinalproject.hacktok.ui.currentProfile.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ActionButton (
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF4b5a)
        ),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .width(160.dp)
            .height(48.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}