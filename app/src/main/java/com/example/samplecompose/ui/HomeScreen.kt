package com.example.samplecompose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserProfile
import com.example.presentation.profile.UserViewModel

@Composable
fun HomeScreen(
    viewModel: UserViewModel,
    onAddProfileClick: () -> Unit,
    onEditProfileClick: (String) -> Unit,
    onProfileClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.syncProfilesOnResume()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProfileClick) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.userProfiles.isEmpty()) {
                item {
                    EmptyStateView(onAddClick = onAddProfileClick)
                }
            } else {
                items(state.userProfiles) { profile ->
                    Box(modifier = Modifier
                        .clickable { onEditProfileClick(profile.id) }
                    ) {
                        ProfileCard(
                            profile = profile,
                            onClick = { onProfileClick(profile.id) },
                            onEdit = { onEditProfileClick(profile.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: UserProfile,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("👤 ${profile.name}", style = MaterialTheme.typography.titleMedium)
                Text("🎂 Age: ${profile.age}")
                Text("🚻 Gender: ${profile.gender}")
                if (!profile.notes.isNullOrBlank()) {
                    Text("📝 Notes: ${profile.notes}")
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }
    }
}

@Composable
fun EmptyStateView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Optional: Replace with Lottie animation or Image
        // You must have this image in your drawable folder
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Replace with your own illustration
            contentDescription = "Empty State Illustration",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "No Profiles Yet",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This is your dashboard to manage medical records for yourself or your family. Add a profile to begin uploading prescriptions, reports, and more.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onAddClick) {
            Text("Add First Profile")
        }
    }
}
