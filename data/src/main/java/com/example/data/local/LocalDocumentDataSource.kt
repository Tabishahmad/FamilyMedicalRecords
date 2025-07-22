package com.example.data.local

import com.example.data.local.db.DocumentDao
import com.example.data.local.db.DocumentEntity
import kotlinx.coroutines.flow.Flow

class LocalDocumentDataSource(private val dao: DocumentDao) {

    suspend fun insert(document: DocumentEntity) = dao.insert(document)

    fun getDocuments(profileId: String): Flow<List<DocumentEntity>> = dao.getDocumentsForProfile(profileId)

    suspend fun delete(document: DocumentEntity) = dao.deleteDocument(document)
}
