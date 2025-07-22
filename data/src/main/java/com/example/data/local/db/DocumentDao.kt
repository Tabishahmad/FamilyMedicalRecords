package com.example.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(document: DocumentEntity)

    @Query("SELECT * FROM documents WHERE profileId = :profileId")
    fun getDocumentsForProfile(profileId: String): Flow<List<DocumentEntity>>

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
}
