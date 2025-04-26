package com.androidfinalproject.hacktok.ui.auth.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.R

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = { onClick() },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "G",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.continue_with_google),
                fontSize = 16.sp
            )
        }
    }
}