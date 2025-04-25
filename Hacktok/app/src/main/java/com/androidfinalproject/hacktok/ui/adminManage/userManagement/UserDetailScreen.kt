package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.R
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementAction
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.component.BanUserDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    modifier: Modifier = Modifier,
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToReportManagement: () -> Unit = {},
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadUserDetails(userId)
    }

    val userState by viewModel.userState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = userState) {
                is UserDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UserDetailState.Success -> {
                    UserDetailContent(
                        user = state.user,
                        reports = state.reports,
                        onNavigateToReportManagement = onNavigateToReportManagement,
                        viewModel = viewModel
                    )
                }
                is UserDetailState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }

    if (userState is UserDetailState.Success && (userState as UserDetailState.Success).showBanDialog) {
        BanUserDialog(
            userId = userId,
            onDismiss = { viewModel.dismissBanDialog() },
            onBanUser = { _, isPermanent, durationDays, reason ->
                viewModel.banUser(userId, isPermanent, durationDays, reason)
            }
        )
    }
}

@Composable
private fun UserDetailContent(
    modifier: Modifier = Modifier,
    user: User,
    reports: List<Report>,
    onNavigateToReportManagement: () -> Unit = {},
    viewModel: UserDetailViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Profile Image
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Profile Image",
                        placeholder = painterResource(R.drawable.placeholder_image),
                        error = painterResource(R.drawable.placeholder_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user.username ?: "No username",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Details
                DetailItem(label = "Full name", value = user.fullName ?: "Not provided")
                DetailItem(label = "Role", value = user.role.name)
                DetailItem(label = "Created at", value = user.createdAt.formatAs("MMM dd, yyyy - HH:mm"))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Reports list
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Reports",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = reports.size.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (reports.isEmpty()) {
                    Text(
                        text = "No reports found for this user.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        reports.forEach { report ->
                            ReportItem(report)
                        }
                    }
                }
            }
        }
        if (user.isCurrentlyBanned()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠️ This user is currently banned",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.showBanDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(Icons.Filled.Block, contentDescription = "Ban", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BAN")
                }

                Button(
                    onClick = onNavigateToReportManagement,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Icon(Icons.Filled.Block, contentDescription = "Go To Resolve", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESOLVE")
                }
            }
        }
    }
}

fun Date.formatAs(pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(this)
}

@Composable
fun ReportItem(report: Report) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LabeledRow(label = "Type", value = report.type?.name ?: "")

            LabeledRow(
                label = "Status",
                value = report.status.uppercase(),
                icon = when (report.status) {
                    "pending" -> "🕒"
                    "resolved" -> "✅"
                    else -> ""
                }
            )

            LabeledRow(
                label = "Reason",
                value = report.reason?.name ?: "No reason provided",
                valueColor = MaterialTheme.colorScheme.error
            )

            LabeledRow(
                label = "Reported at",
                value = report.createdAt.formatAs("MMM dd, yyyy HH:mm")
            )
        }
    }
}

@Composable
fun LabeledRow(
    label: String,
    value: String,
    icon: String = "",
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        if (icon.isNotEmpty()) {
            Text(
                text = "$icon $value",
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = valueColor
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailScreenPreview() {
    UserDetailScreen(
        userId = "123",
        onNavigateBack = {}
    )
}