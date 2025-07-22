package com.example.data.document

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FirebaseDocumentUploader(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase,
    private val context: Context
) {

    suspend fun uploadDocumentForUser(
        userId: String,
        fileUri: Uri,
        fileType: String // "pdf" or "image"
    ) {
        val fileName = fileUri.lastPathSegment ?: UUID.randomUUID().toString()
        val storageRef = firebaseStorage.reference
            .child("user_docs/$userId/$fileName")

        Log.d("Upload", "File URI: $fileUri")
        Log.d("Upload", "Exists: ${File(fileUri.path ?: "").exists()}")

//        val savedPath = copyFileToInternalStorage(context,fileUri,fileName)
//        val fileUri = Uri.fromFile(File(savedPath))

//        Log.d("Upload", "savedPath: $savedPath")
//        Log.d("Upload", "fileUri after save : $fileUri")

        // 1. Upload file to Firebase Storage
        storageRef.putFile(fileUri).await()

        // 2. Get download URL
        val downloadUrl = storageRef.downloadUrl.await().toString()

        // 3. Prepare metadata
        val docMeta = mapOf(
            "fileName" to fileName,
            "fileUrl" to downloadUrl,
            "type" to fileType,
            "uploadedAt" to System.currentTimeMillis()
        )

        // 4. Save metadata to Realtime DB under user's document list
        firebaseDatabase.reference
            .child("users")
            .child(userId)
            .child("documents")
            .push() // auto-generated docId
            .setValue(docMeta)
            .await()
    }
    fun copyFileToInternalStorage(context: Context, uri: Uri, fileName: String): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }
}
