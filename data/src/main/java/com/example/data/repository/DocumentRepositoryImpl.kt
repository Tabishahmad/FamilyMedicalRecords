package com.example.data.repository

import android.net.Uri
import com.example.data.document.FirebaseDocumentUploader
import com.example.data.local.LocalDocumentDataSource
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Document
import com.example.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentRepositoryImpl(
    private val localDataSource: LocalDocumentDataSource,
    private val firebaseUploader: FirebaseDocumentUploader
) : DocumentRepository {

    override suspend fun insert(document: Document) {
        localDataSource.insert(document.toEntity())
    }

    override fun getDocumentsForProfile(profileId: String): Flow<List<Document>> {
        return localDataSource.getDocuments(profileId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun delete(document: Document) {
        localDataSource.delete(document.toEntity())
    }
    override suspend fun uploadUserFile(userId: String, fileUri: Uri, fileType: String) {
        // Optionally: save locally or show uploading state
        firebaseUploader.uploadDocumentForUser(userId, fileUri, fileType)
    }
}
