package com.example.data.mapper

import com.example.data.local.db.DocumentEntity
import com.example.domain.model.Document

fun DocumentEntity.toDomain(): Document = Document(
    id = id,
    profileId = profileId,
    fileName = fileName,
    fileType = fileType,
    fileUri = fileUri,
    doctorName = doctorName,
    type = type,
    reportType = reportType,
    notes = notes,
    timestamp = timestamp
)

fun Document.toEntity(): DocumentEntity = DocumentEntity(
    id = id,
    profileId = profileId,
    fileName = fileName,
    fileType = fileType,
    fileUri = fileUri,
    doctorName = doctorName,
    type = type,
    reportType = reportType,
    notes = notes,
    timestamp = timestamp
)
