package com.example.chatbotapp

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {


    val messageList by lazy{
        mutableStateListOf<MessageModel>()
    }

    val generativeModel : GenerativeModel=GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey= Constants.apiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch{
            try {


                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) { text(it.Message) }
                    }.toList()
                )
                messageList.add(MessageModel(question, "user"))
                messageList.add(MessageModel("Typing....", "model"))
                val response = chat.sendMessage(question)
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel(response.text.toString(), "model"))
            }catch (e: Exception)
            {
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error:" +(e.message).toString(), "model"))
            }

        }
    }
}