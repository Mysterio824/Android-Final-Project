package com.androidfinalproject.hacktok.ui.currentProfile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun StatColumn (
    count: Int,
    label: String
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text (
            text = count.toString(),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text (
            text = label,
            color = Color.DarkGray,
            fontSize = 16.sp
        )
    }
}