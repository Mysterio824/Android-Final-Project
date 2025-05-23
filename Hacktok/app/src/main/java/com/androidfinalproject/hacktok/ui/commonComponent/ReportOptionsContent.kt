package com.androidfinalproject.hacktok.ui.commonComponent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.model.enums.ReportCause

@Composable
fun ReportOptionsContent(
    onDismiss: () -> Unit,
    targetId: String,
    onReportCauseSelected: (String, ReportCause, ReportType) -> Unit,
    type: ReportType
) {
    fun withDismiss(action: () -> Unit): () -> Unit = {
        action()
        onDismiss()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Report Options",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )

        ReportCause.entries.filter { cause ->
            cause.applicableTypes.contains(type)
        }.forEach { cause ->
            OptionItem(
                title = cause.description,
                onClick = withDismiss { onReportCauseSelected(targetId, cause, type) }
            )
        }
    }
}