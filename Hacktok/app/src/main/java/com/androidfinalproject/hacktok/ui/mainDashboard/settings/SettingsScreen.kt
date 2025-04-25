package com.androidfinalproject.hacktok.ui.mainDashboard.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.commonComponent.ProfileImage

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsScreenAction) -> Unit
) {
    val scrollState = rememberScrollState()
    var showLanguageDropdown by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Japanese")

    Scaffold{ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Account Section
            SectionHeader("Account")

            // Profile Option
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Main Profile",
                imageUrl = state.currentUser?.profileImage,
                onClick = { onAction(SettingsScreenAction.OnCurrentProfileNavigate) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Edit Profile Option
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                onClick = { onAction(SettingsScreenAction.OnNavigateEdit) }
            )

            // Change Password Option
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Change Password",
                onClick = { onAction(SettingsScreenAction.OnChangePassword) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // App Settings Section
            SectionHeader("App Settings")

            // Language Settings with Dropdown
            Box {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = state.language.ifEmpty { "English" },
                    onClick = { showLanguageDropdown = true }
                )

                DropdownMenu(
                    expanded = showLanguageDropdown,
                    onDismissRequest = { showLanguageDropdown = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                onAction(SettingsScreenAction.OnChangeLanguage(language))
                                showLanguageDropdown = false
                            },
                            leadingIcon = {
                                if (language == state.language || (state.language.isEmpty() && language == "English")) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Logout Button
            Button(
                onClick = { onAction(SettingsScreenAction.OnLogout) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "Log Out",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            // Version info
            Text(
                text = "HackTok v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector? = null,
    title: String,
    imageUrl: String? = null,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                if(imageUrl != null) {
                    ProfileImage(
                        imageUrl = imageUrl,
                        size = 40.dp,
                        onClick = onClick
                    )
                } else {
                    Icon(
                        imageVector = icon!!,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}