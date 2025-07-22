package com.example.samplecompose.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.remote.GeminiApiService
import com.example.samplecompose.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun AiPromptScreen(
    initialText: String,
    initialPrompt: String
) {
    var inputText by remember { mutableStateOf(initialText) }
    var promptText by remember { mutableStateOf(initialPrompt) }
    var isLoading by remember { mutableStateOf(false) }
    var responseText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("📄 Medical Text", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Extracted Text") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = { Text("AI Prompt") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val apiKey = "AIzaSyA9U5TqkX7OhZkoYiLJrwy1Ac2UJneE89s"// BuildConfig.GEMINI_API_KEY
                        println("Ai Response apiKey -> " + apiKey)
                        val aiPrompt = "$promptText\n\n$inputText"
                        var geminiApiService = GeminiApiService(apiKey)
                        val response = geminiApiService.sendToGemini(aiPrompt)
                        responseText = response
                        println("Ai Response -> " + responseText)
                        Log.d("GEMINI_API_RESPONSE", responseText)

                    } catch (e: Exception) {
                        responseText = "Failed: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explain with AI")
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        if (responseText.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            Text("🧠 AI Response", style = MaterialTheme.typography.titleMedium)
            Text(responseText, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
