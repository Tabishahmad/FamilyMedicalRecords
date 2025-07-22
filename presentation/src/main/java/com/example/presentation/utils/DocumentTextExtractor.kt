package com.example.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DocumentTextExtractor(
    private val context: Context
) {
    suspend fun extractTextFromFile(uri: Uri): String {
        val isPdf = uri.toString().endsWith(".pdf", ignoreCase = true)
        return if (isPdf) {
            extractTextFromPdf(uri)
        } else {
            extractTextFromImage(uri)
        }
    }

    private suspend fun extractTextFromImage(uri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputImage = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = Tasks.await(recognizer.process(inputImage))
            Log.d("OCR_RESULT", result.text)
            if (result.text.isBlank()) "No text found" else result.text

            result.text
        } catch (e: Exception) {
            Log.e("OCR", "Failed to read image", e)
            "OCR failed: ${e.message}"
        }
    }

    private suspend fun extractTextFromPdf(uri: Uri): String = withContext(Dispatchers.IO) {
        val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext ""
        val renderer = PdfRenderer(fileDescriptor)

        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val textBuilder = StringBuilder()

        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val result = Tasks.await(recognizer.process(inputImage))
            textBuilder.appendLine(result.text)
        }

        renderer.close()
        fileDescriptor.close()
        return@withContext textBuilder.toString()
    }
}
