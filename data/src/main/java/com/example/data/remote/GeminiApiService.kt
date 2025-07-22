package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiApiService(private val apiKey: String) {

    suspend fun sendToGemini(promptText: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro-002:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val body = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": ${JSONObject.quote(promptText)}
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            OutputStreamWriter(connection.outputStream).use { it.write(body) }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                val error = connection.errorStream?.bufferedReader()?.use { it.readText() }
                return@withContext "Gemini API Error ${connection.responseCode}: $error"
            }

            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            val summary = JSONObject(responseText)
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            return@withContext summary
        } catch (e: Exception) {
            return@withContext "Exception: ${e.localizedMessage}"
        }
    }
}
