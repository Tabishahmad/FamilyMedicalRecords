package com.example.domain.repository

import android.net.Uri
import com.example.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    suspend fun insert(document: Document)
    fun getDocumentsForProfile(profileId: String): Flow<List<Document>>
    suspend fun delete(document: Document)
    suspend fun uploadUserFile(userId: String, fileUri: Uri, fileType: String)
}
