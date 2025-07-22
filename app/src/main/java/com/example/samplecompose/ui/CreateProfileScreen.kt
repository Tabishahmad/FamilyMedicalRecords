package com.example.samplecompose.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserProfile
import com.example.presentation.profile.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    viewModel: UserViewModel,
    profileId: String?, // nullable
    onProfileCreated: () -> Unit,
    onCancel: () -> Unit
) {
    val existingProfile = remember(profileId) {
        profileId?.let { id -> viewModel.getProfileById(id) }
    }
    val isEditMode = existingProfile != null

    var name by remember { mutableStateOf(existingProfile?.name ?: "") }
    var age by remember { mutableStateOf(existingProfile?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(existingProfile?.gender ?: "Male") }
    var notes by remember { mutableStateOf(existingProfile?.notes ?: "") }


    val genderOptions = listOf("Male", "Female", "Other")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { it.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            // Gender Dropdown
            var genderExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (name.isNotBlank() && age.isNotBlank()) {
                            val profile = UserProfile(
                                id = existingProfile?.id ?: "", // reuse id if edit
                                name = name.trim(),
                                age = age.toInt(),
                                gender = gender,
                                notes = notes.takeIf { it.isNotBlank() }
                            )
                            viewModel.addProfile(profile)
                            onProfileCreated()
                        }
                    }
                ) {
                    Text("Save")
                }

                OutlinedButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }

            if (isEditMode && existingProfile != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.deleteProfile(existingProfile)
                        onProfileCreated()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            }

        }
    }
}
