package com.example.chatbotapp

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChatViewModel: ViewModel() {

    private val TAG = "ChatViewModel"

    val messageList by lazy{
        mutableStateListOf<MessageModel>()
    }

    val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch{
            try {
                Log.d(TAG, "=== API CALL DEBUG INFO ===")
                Log.d(TAG, "Sending message: $question")
                Log.d(TAG, "API Key length: ${Constants.apiKey.length}")
                Log.d(TAG, "API Key starts with: ${Constants.apiKey.take(10)}...")
                Log.d(TAG, "Model name: gemini-2.5-flash")

                // Validate API key
                if (Constants.apiKey.isEmpty() || Constants.apiKey == "YOUR_API_KEY_HERE") {
                    messageList.add(
                        MessageModel(
                            "❌ Error: Please set a valid API key in Constants.kt",
                            "model"
                        )
                    )
                    return@launch
                }

                // Add user message and typing indicator
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("🤖 Typing...", "model"))

                // Test API connectivity with timeout
                Log.d(TAG, "Creating chat session...")
                val chat = generativeModel.startChat(
                    history = messageList.filter { it.Message != "🤖 Typing..." }.map {
                        content(it.role) { text(it.Message) }
                    }.toList()
                )

                Log.d(TAG, "Sending message to API...")

                // Add timeout to prevent hanging
                val response = withTimeout(30000L) { // 30 second timeout
                    chat.sendMessage(question)
                }

                // Remove typing indicator
                messageList.removeAt(messageList.lastIndex)

                val responseText = response.text
                Log.d(TAG, "Response received: ${responseText?.take(100)}...")

                if (responseText != null && responseText.isNotEmpty()) {
                    messageList.add(MessageModel(responseText, "model"))
                    Log.d(TAG, "✅ Response processed successfully")
                } else {
                    messageList.add(MessageModel("❌ Error: Empty response from API", "model"))
                    Log.e(TAG, "Empty response from API")
                }

            } catch (e: Exception) {
                Log.e(TAG, "=== API ERROR DETAILS ===")
                Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Exception message: ${e.message}")
                Log.e(TAG, "Exception cause: ${e.cause}")
                e.printStackTrace()

                // Remove typing indicator if it exists
                if (messageList.isNotEmpty() && messageList.last().Message == "🤖 Typing...") {
                    messageList.removeAt(messageList.lastIndex)
                }

                val errorMessage = when (e) {
                    is kotlinx.coroutines.TimeoutCancellationException -> {
                        "⏱️ Error: Request timed out. The API is taking too long to respond. Please try again."
                    }

                    is UnknownHostException -> {
                        "🌐 Error: Cannot reach Google AI servers. Check your internet connection."
                    }

                    is ConnectException -> {
                        "🔌 Error: Connection failed. Please check your internet connection and try again."
                    }

                    is SocketTimeoutException -> {
                        "⏱️ Error: Connection timed out. The server might be busy. Please try again."
                    }

                    else -> {
                        when {
                            e.message?.contains("API_KEY_INVALID") == true ||
                                    e.message?.contains("invalid_api_key") == true ->
                                "🔑 Error: Invalid API key. Please verify your Gemini API key in Constants.kt"

                            e.message?.contains("PERMISSION_DENIED") == true ||
                                    e.message?.contains("permission_denied") == true ->
                                "🚫 Error: Permission denied. Your API key may not have access to Gemini API."

                            e.message?.contains("QUOTA_EXCEEDED") == true ||
                                    e.message?.contains("quota_exceeded") == true ->
                                "📊 Error: API quota exceeded. Please wait or upgrade your plan."

                            e.message?.contains("RESOURCE_EXHAUSTED") == true ->
                                "⚡ Error: API resources exhausted. Please try again later."

                            e.message?.contains("model_not_found") == true ->
                                "🤖 Error: Model not found. The gemini-2.5-flash model may not be available."

                            e.message?.contains("UNAUTHENTICATED") == true ->
                                "🔐 Error: Authentication failed. Please check your API key."

                            e.message?.contains("INTERNAL") == true ->
                                "⚙️ Error: Internal server error. Google's servers may be experiencing issues."

                            e.message?.contains("UNAVAILABLE") == true ->
                                "🚧 Error: Service unavailable. Google AI service may be down."

                            else -> "❌ Error: ${e.message ?: "Unknown error occurred. Check logs for details."}"
                        }
                    }
                }

                messageList.add(MessageModel(errorMessage, "model"))
            }
        }
    }
}