package com.example.domain.model

data class Document(
    val id: Int = 0,
    val profileId: String,
    val fileName: String,
    val fileType: String,
    val fileUri: String,
    val doctorName: String,
    val type: String, // "Prescription" or "Report"
    val reportType: String, // e.g., "Blood Report"
    val notes: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

