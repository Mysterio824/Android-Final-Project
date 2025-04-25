package com.androidfinalproject.hacktok.ui.secretCrush

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.secretCrush.SecretCrushAction
import com.androidfinalproject.hacktok.ui.secretCrush.SecretCrushState
import com.androidfinalproject.hacktok.ui.secretCrush.SelectedCrush
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretCrushScreen(
    state: SecretCrushState,
    onAction: (SecretCrushAction) -> Unit
) {
    var showMessageDialog by remember { mutableStateOf<User?>(null) }
    var messageText by remember { mutableStateOf("") }
    var showRemoveConfirmation by remember { mutableStateOf<String?>(null) }
    var showUsersList by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        onAction(SecretCrushAction.LoadCrushData)
    }

    val filteredUsers = if (searchQuery.text.isEmpty()) {
        state.availableUsers
    } else {
        state.availableUsers.filter {
            it.username?.contains(searchQuery.text, ignoreCase = true) ?: false ||
                    it.fullName?.contains(searchQuery.text, ignoreCase = true) ?: false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secret Crush") },
                navigationIcon = {
                    IconButton(onClick = { onAction(SecretCrushAction.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Intro card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Secret Crush",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Secret Crush",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Secretly like someone? Add them as your crush! If they like you back, we'll let you both know it's a match!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // People who like you counter
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "People who like you",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.peopleWhoLikeYou.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Selected crushes section
                Text(
                    text = "Your Secret Crushes (${state.selectedCrushes.size}/5)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Start
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add button (if less than 5 crushes)
                    if (state.selectedCrushes.size < 5) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { showUsersList = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add crush",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Existing crushes
                    items(state.selectedCrushes) { crush ->
                        Box(
                            modifier = Modifier.width(80.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { showRemoveConfirmation = crush.user.id }
                                ) {
                                    // User avatar
                                    AsyncImage(
                                        model = crush.user.profileImage ?: "",
                                        contentDescription = "Crush avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = crush.user.username ?: "User",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Users List Dialog
                if (showUsersList) {
                    Dialog(onDismissRequest = { showUsersList = false }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                // Search bar
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Search users...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Users list
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredUsers) { user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onAction(SecretCrushAction.SelectUser(user))
                                                    showUsersList = false
                                                }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // User avatar
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                AsyncImage(
                                                    model = user.profileImage ?: "",
                                                    contentDescription = "User avatar",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            // User info
                                            Column {
                                                Text(
                                                    text = user.username ?: "User",
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = user.fullName ?: "",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Show button only when user list is not visible
                if (!showUsersList) {
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { showUsersList = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedCrushes.size < 5
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Select crush")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Your Secret Crush")
                    }
                }
            }
        }
    }

    // Message dialog for new crush
    if (showMessageDialog != null) {
        Dialog(onDismissRequest = { showMessageDialog = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Send a message to ${showMessageDialog?.username}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = { Text("Your message (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showMessageDialog = null }) {
                            Text("Cancel")
                        }

                        Button(onClick = {
                            showMessageDialog?.id?.let { userId ->
                                onAction(SecretCrushAction.SendMessage(userId, messageText))
                                onAction(SecretCrushAction.SelectUser(showMessageDialog!!))
                            }
                            messageText = ""
                            showMessageDialog = null
                        }) {
                            Text("Add Crush")
                        }
                    }
                }
            }
        }
    }

    // Remove crush confirmation dialog
    if (showRemoveConfirmation != null) {
        val crushToRemove = state.selectedCrushes.find { it.user.id == showRemoveConfirmation }

        if (crushToRemove != null) {
            AlertDialog(
                onDismissRequest = { showRemoveConfirmation = null },
                title = { Text("Remove Secret Crush") },
                text = { Text("Do you want to remove ${crushToRemove.user.username} from your secret crushes?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onAction(SecretCrushAction.UnselectUser(showRemoveConfirmation!!))
                            showRemoveConfirmation = null
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRemoveConfirmation = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUserClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            AsyncImage(
                model = user.profileImage,
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column {
                Text(
                    text = user.fullName ?: "User",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "@${user.username ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SecretCrushScreenPreview() {
    MainAppTheme {
        SecretCrushScreen(
            state = SecretCrushState(
                currentUser = MockData.mockUsers[0],
                selectedCrushes = listOf(
                    SelectedCrush(
                        user = MockData.mockUsers[1],
                        message = "I've always admired you!"
                    ),
                    SelectedCrush(
                        user = MockData.mockUsers[2],
                        message = null
                    )
                ),
                availableUsers = MockData.mockUsers,
                peopleWhoLikeYou = 3,
                isLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SecretCrushScreenEmptyPreview() {
    MainAppTheme {
        SecretCrushScreen(
            state = SecretCrushState(
                currentUser = MockData.mockUsers[0],
                selectedCrushes = emptyList(),
                availableUsers = MockData.mockUsers,
                peopleWhoLikeYou = 0,
                isLoading = false
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SecretCrushScreenLoadingPreview() {
    MainAppTheme {
        SecretCrushScreen(
            state = SecretCrushState(
                isLoading = true
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SecretCrushScreenFullPreview() {
    // Create a list of 5 selected crushes using the mock users
    val selectedCrushes = MockData.mockUsers.take(5).mapIndexed { index, user ->
        SelectedCrush(
            user = user,
            message = if (index % 2 == 0) "Hey there! I like you!" else null
        )
    }

    MainAppTheme {
        SecretCrushScreen(
            state = SecretCrushState(
                currentUser = MockData.mockUsers[0],
                selectedCrushes = selectedCrushes,
                availableUsers = MockData.mockUsers,
                peopleWhoLikeYou = 7,
                isLoading = false
            ),
            onAction = {}
        )
    }
}