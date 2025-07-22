package com.example.presentation.documents

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Document
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class DocumentFilterState(
    val selectedDoctor: String? = null,
    val selectedReportType: String? = null,
    val dateFilter: String = "All"
)

class DocumentViewModel(
    private val documentRepository: DocumentRepository
) : ViewModel() {


    private val _documentList = MutableStateFlow<List<Document>>(emptyList())
    val documentList: StateFlow<List<Document>> = _documentList

    private val _filterState = MutableStateFlow(DocumentFilterState())
    val filterState: StateFlow<DocumentFilterState> = _filterState

    fun updateFilter(
        doctor: String? = filterState.value.selectedDoctor,
        reportType: String? = filterState.value.selectedReportType,
        date: String = filterState.value.dateFilter
    ) {
        _filterState.value = DocumentFilterState(doctor, reportType, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val filteredDocuments: StateFlow<List<Document>> =
        combine(documentList, filterState) { documents, filter ->
            documents.filter { doc ->

                val matchDoctor = filter.selectedDoctor.isNullOrBlank() || doc.doctorName == filter.selectedDoctor
                val matchType = filter.selectedReportType.isNullOrBlank() || doc.reportType == filter.selectedReportType

                val matchDate = when (filter.dateFilter) {
                    "This Month" -> {
                        val now = LocalDate.now()
                        val docDate = Instant.ofEpochMilli(doc.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        docDate.month == now.month && docDate.year == now.year
                    }

                    "Last 3 Months" -> {
                        val now = LocalDate.now()
                        val docDate = Instant.ofEpochMilli(doc.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        docDate.isAfter(now.minusMonths(3))
                    }

                    else -> true
                }

                matchDoctor && matchType && matchDate
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    fun loadDocuments(profileId: String) {
        viewModelScope.launch {
            documentRepository.getDocumentsForProfile(profileId).collect {
                _documentList.value = it
            }
        }
    }


    fun addDocument(
        context: Context,
        uri: Uri,
        profileId: String,
        doctorName: String,
        type: String,
        reportType: String,
        notes: String
    ) {
        val fileName = getFileNameFromUri(context, uri)
        val savedPath = copyFileToInternalStorage(context, uri, fileName)
        val fileType = if (fileName.endsWith(".pdf")) "pdf" else "image"

        val document = Document(
            profileId = profileId,
            fileName = fileName,
            fileType = fileType,
            fileUri = savedPath,
            doctorName = doctorName,
            type = type,
            reportType = reportType,
            notes = notes
        )

        viewModelScope.launch {
            documentRepository.insert(document)


        }
    }

    private fun copyFileToInternalStorage(context: Context, uri: Uri, fileName: String): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "docs")
        if (!file.exists()) file.mkdir()

        val outputFile = File(file, fileName)
        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        return outputFile.absolutePath
    }
//    private fun getFileNameFromUri(context: Context, uri: Uri): String {
//        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
//        returnCursor?.use {
//            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//            it.moveToFirst()
//            return it.getString(nameIndex)
//        }
//        return "unknown_file"
//    }
fun getFileNameFromUri(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = it.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.lastPathSegment ?: "file_${System.currentTimeMillis()}"
    }
    // Remove illegal characters like ':' and encode-safe issues
    return result!!.replace("[:%]".toRegex(), "_")
}

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.delete(document)
        }
    }
    fun uploadDocument(userId: String, fileUri: Uri, fileType: String) {
        viewModelScope.launch {
            documentRepository.uploadUserFile(userId, fileUri, fileType)
//            localDataSource.markDocumentAsSynced(userId, fileUri.toString())
        }
    }

}
