package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getEffectiveApiKey(customApiKey: String?): String {
        if (!customApiKey.isNullOrBlank()) {
            return customApiKey.trim()
        }
        val buildKey = BuildConfig.GEMINI_API_KEY
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey.trim()
        }
        return ""
    }

    suspend fun testApiKey(apiKey: String, model: String = "gemini-3.5-flash"): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val key = apiKey.trim()
        if (key.isBlank()) {
            return@withContext Pair(false, "API Key cannot be empty.")
        }
        val effectiveModel = if (model.isNotBlank()) model else "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$effectiveModel:generateContent?key=$key"

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", "Respond with a single word 'Connected' if this API key is active.") })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody).optJSONObject("error")
                    errJson?.optString("message") ?: "HTTP ${response.code}"
                } catch (_: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }
                return@withContext Pair(false, "Validation failed: $errorMsg")
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                return@withContext Pair(true, "API Key successfully validated! Model: $effectiveModel")
            }
            Pair(false, "Received unexpected response format from Gemini.")
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: e.message}")
        }
    }

    suspend fun askGemini(
        prompt: String,
        businessContext: String,
        customApiKey: String? = null,
        modelName: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            return@withContext "⚠️ Gemini API key is missing or not configured.\n\nPlease enter your Gemini API Key in 'Application Settings' or the 'Configure AI' panel above to enable live predictive intelligence."
        }

        val model = if (!modelName.isNullOrBlank()) modelName else "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val systemPrompt = """
            You are the expert retail business analyst and predictive inventory strategist for IBR SHOP Manager.
            Below is the real-time store performance data context:
            $businessContext
            
            Instructions:
            - Provide sharp, data-driven, actionable, and structured insights.
            - Reference specific monetary amounts and stock figures where relevant.
            - Keep tone professional, encouraging, and highly practical for a retail store manager.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", "$systemPrompt\n\nManager Question: $prompt") })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                val errorDetails = try {
                    val errObj = JSONObject(responseBody).optJSONObject("error")
                    val msg = errObj?.optString("message") ?: "Code ${response.code}"
                    val status = errObj?.optString("status") ?: ""
                    "Error ($status): $msg"
                } catch (_: Exception) {
                    "Request failed with HTTP status ${response.code}."
                }
                return@withContext "⚠️ AI Service Error:\n$errorDetails\n\nPlease verify your API key or network connection in Settings."
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext text.trim()
                    }
                }
            }
            "No output generated by the AI model. Please try refining your query."
        } catch (e: Exception) {
            "⚠️ Network Connection Error:\nCould not reach the Gemini API service (${e.localizedMessage ?: e.message}). Please check your internet connection."
        }
    }
}
