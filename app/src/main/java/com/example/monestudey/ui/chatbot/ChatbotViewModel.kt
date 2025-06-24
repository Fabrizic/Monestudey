package com.example.monestudey.ui.chatbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.monestudey.data.chatbot.ChatMessage
import com.example.monestudey.data.chatbot.DialogflowApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatbotViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val dialogflowApi = DialogflowApi(application)

    init {
        viewModelScope.launch {
            try {
                dialogflowApi.initialize()
            } catch (e: Exception) {
                _error.value = "Error al inicializar el chatbot: ${e.message}"
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Agregar mensaje del usuario
                val userMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = text,
                    isFromUser = true
                )
                _messages.value = _messages.value + userMessage

                // Enviar mensaje a Dialogflow
                val response = dialogflowApi.sendMessage(text)

                // Agregar respuesta del bot
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = response,
                    isFromUser = false
                )
                _messages.value = _messages.value + botMessage

            } catch (e: Exception) {
                _error.value = "Error al enviar mensaje: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // No necesitamos cerrar nada específico con la nueva implementación
    }
} 