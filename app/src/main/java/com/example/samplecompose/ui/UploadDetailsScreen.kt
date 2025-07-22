package com.example.samplecompose.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.example.presentation.documents.DocumentViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.presentation.profile.UserViewModel
import com.example.presentation.utils.DocumentTextExtractor
import com.example.presentation.utils.FileUtils
import com.example.samplecompose.R
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDetailsScreen(
    fileUri: Uri,
    profileId: String?, // Now nullable
    viewModel: DocumentViewModel,
    userViewModel: UserViewModel, // For user profile dropdown
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userProfiles by userViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    var selectedProfileId by remember { mutableStateOf(profileId ?: "") }
    var doctorName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Prescription") }
    var reportType by remember { mutableStateOf("Blood Report") }
    var notes by remember { mutableStateOf("") }

    val fileName = FileUtils.getFileNameFromUri(context, fileUri)
    val savedPath = FileUtils.copyFileToInternalStorage(context, fileUri, fileName)
    val uriForUpload = Uri.fromFile(File(savedPath))

    val typeOptions = listOf("Prescription", "Report")
    val reportOptions = listOf("Blood Report", "X-Ray", "MRI", "Doctor Prescription", "Other")
    val mimeType = remember(fileUri) {
        context.contentResolver.getType(fileUri) ?: ""
    }
    if (isLoading) {
//        CircularProgressIndicator(
//            modifier = Modifier
//                .align(Alignment.Vertical())
//                .padding(top = 16.dp)
//        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Upload Details") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 👇 Show image or PDF preview
            if (mimeType == "application/pdf") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pdf),
                        contentDescription = "PDF Icon",
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                AsyncImage(
                    model = fileUri,
                    contentDescription = "Image Preview",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // 👤 Dropdown for selecting user if no profileId was passed
            if (profileId.isNullOrBlank()) {
                val profiles = userProfiles.userProfiles
                ExposedDropdownMenuBoxExpanded(
                    label = "Select Patient",
                    options = profiles.map { "${it.name} (ID: ${it.id})" },
                    selected = profiles.firstOrNull { it.id == selectedProfileId }
                        ?.let { "${it.name} (ID: ${it.id})" } ?: "Select Patient",
                    onSelect = { selected ->
                        selectedProfileId = profiles.firstOrNull {
                            "${it.name} (ID: ${it.id})" == selected
                        }?.id ?: ""
                    }
                )
            }

            // Doctor Name
            OutlinedTextField(
                value = doctorName,
                onValueChange = { doctorName = it },
                label = { Text("Doctor Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Type Dropdown
            ExposedDropdownMenuBoxExpanded(
                label = "Type",
                options = typeOptions,
                selected = type,
                onSelect = { type = it }
            )

            // Report Type Dropdown
            ExposedDropdownMenuBoxExpanded(
                label = "Report Type",
                options = reportOptions,
                selected = reportType,
                onSelect = { reportType = it }
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Upload Button
            Button(
                onClick = {
                    if (selectedProfileId.isNotBlank()) {
                        viewModel.addDocument(
                            context = context,
                            uri = fileUri,
                            profileId = selectedProfileId,
                            doctorName = doctorName,
                            type = type,
                            reportType = reportType,
                            notes = notes
                        )
                        viewModel.uploadDocument(
                            userId = selectedProfileId,
                            fileUri = uriForUpload,
                            fileType = FileUtils.determineFileType(fileUri)
                        )
                        onBack()
                    }
                },
                enabled = selectedProfileId.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload")
            }

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val extractor = DocumentTextExtractor(context)
                        val extractedText = extractor.extractTextFromFile(fileUri)
                        isLoading = false
                        extractedText.chunked(500).forEachIndexed { i, chunk ->
                            Log.d("OCR_PART_$i", chunk)
                        }
                        val encodedExtractedText = Uri.encode(extractedText)
                        val defaultPrompt = Uri.encode("Please explain this in simple terms for a patient.")
                        navController.navigate("aiPromptScreen?text=$encodedExtractedText&prompt=$defaultPrompt")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Read Document")
            }
        }
    }
}
