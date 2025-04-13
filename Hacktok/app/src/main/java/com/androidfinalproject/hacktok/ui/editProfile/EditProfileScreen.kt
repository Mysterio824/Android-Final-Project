package com.androidfinalproject.hacktok.ui.editProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidfinalproject.hacktok.model.UserRole
import com.androidfinalproject.hacktok.ui.messageDashboard.component.ProfileImage
import com.androidfinalproject.hacktok.ui.editProfile.component.CustomTextField
import com.androidfinalproject.hacktok.ui.editProfile.component.DropdownField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun EditProfileScreen(
    state: EditProfileState,
    onAction: (EditProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Edit profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            ProfileImage(
                imageSize = 40.dp,
                modifier = Modifier,
                contentDescription = "Profile image",
                isActive = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            label = "Username",
            value = state.username,
            isError = state.errorState["username"] ?: false,
            onValueChange = { onAction(EditProfileAction.UpdateField("username", it)) }
        )

        CustomTextField(
            label = "Full Name",
            value = state.fullName,
            isError = state.errorState["fullName"] ?: false,
            onValueChange = { onAction(EditProfileAction.UpdateField("fullName", it)) }
        )

        CustomTextField(
            label = "Email",
            value = state.email,
            isError = state.errorState["email"] ?: false,
            onValueChange = { onAction(EditProfileAction.UpdateField("email", it)) }
        )

        CustomTextField(
            label = "Bio",
            value = state.bio,
            isError = state.errorState["bio"] ?: false,
            onValueChange = { onAction(EditProfileAction.UpdateField("bio", it)) }
        )

        DropdownField(
            label = "Role",
            selectedValue = state.role.name,
            options = UserRole.entries.map { it.name },
            onValueChange = { onAction(EditProfileAction.UpdateField("role", it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onAction(EditProfileAction.Cancel) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", color = Color.Black)
            }

            Button(
                onClick = { onAction(EditProfileAction.SaveProfile) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00)), // Orange
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUserPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val user = MockData.mockUsers.first()

            EditProfileScreen(
                state = EditProfileState(
                    username = user.username,
                    fullName = user.fullName ?: "Unknown",
                    email = user.email,
                    bio = user.bio ?: "",
                    role = user.role,
                    errorState = emptyMap()
                ),
                onAction = {}
            )
        }
    }
}