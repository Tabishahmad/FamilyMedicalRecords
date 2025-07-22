package com.example.samplecompose.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.domain.model.Document
import com.example.presentation.documents.DocumentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    profileId: String,
    navController: NavController,
    viewModel: DocumentViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val documents by viewModel.documentList.collectAsState()

    LaunchedEffect(profileId) {
        viewModel.loadDocuments(profileId)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                navController.navigate(
                    "uploadDetailsScreen?uri=${Uri.encode(uri.toString())}&profileId=$profileId"
                )
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documents") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                launcher.launch(arrayOf("application/pdf", "image/*"))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Document")
            }
        }
    ) { padding ->
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                EmptyDocumentStateView()
            }
        } else {
            val filterState by viewModel.filterState.collectAsState()
            val documents by viewModel.filteredDocuments.collectAsState()

            val allDocuments by viewModel.documentList.collectAsState()
            val doctorOptions =
                allDocuments.map { it.doctorName }.filter { it.isNotBlank() }.distinct().sorted()
            val reportOptions =
                allDocuments.map { it.reportType }.filter { it.isNotBlank() }.distinct().sorted()

            val dateOptions = listOf("All", "This Month", "Last 3 Months")
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 1.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    FilterDropdown(
                        label = "Doctor",
                        options = doctorOptions,
                        selectedOption = filterState.selectedDoctor,
                        onOptionSelected = { viewModel.updateFilter(doctor = it) }
                    )

                    FilterDropdown(
                        label = "Type",
                        options = reportOptions,
                        selectedOption = filterState.selectedReportType,
                        onOptionSelected = { viewModel.updateFilter(reportType = it) }
                    )

                    FilterDropdown(
                        label = "Date",
                        options = dateOptions,
                        selectedOption = filterState.dateFilter,
                        onOptionSelected = {
                            if (it != null) viewModel.updateFilter(date = it)
                        }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(documents) { doc ->
                        DocumentCard(document = doc) {
                            navController.navigate("docDetail/${doc.id}/${doc.profileId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(document: Document, onClick: () -> Unit = {}) {
    val backgroundColor = when (document.type.lowercase()) {
        "prescription" -> Color(0xFFDEEAF6)
        "report" -> Color(0xFFE9F6DE)
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("\uD83D\uDCC4 ${document.type}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Text("\uD83D\uDC68\u200D⚕️ Doctor: ${document.doctorName}", color = Color.Black)
            Text("\uD83D\uDCC1 Type: ${document.type}", color = Color.Black)
            Text("\uD83D\uDDFE️ Report: ${document.reportType}", color = Color.Black)
            if (!document.notes.isNullOrBlank()) {
                Text("\uD83D\uDCDD Notes: ${document.notes}", color = Color.Black)
            }
        }
    }
}

@Composable
fun EmptyDocumentStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "No documents",
            modifier = Modifier.size(72.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Documents Yet",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You haven't uploaded any prescriptions, lab reports, or scans for this profile.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap the ➕ button below to upload your first medical document.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}
