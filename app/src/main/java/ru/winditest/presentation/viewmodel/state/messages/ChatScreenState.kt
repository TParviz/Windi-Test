package ru.winditest.presentation.viewmodel.state.messages

import androidx.compose.runtime.Immutable
import ru.winditest.data.local.messages.model.Chat
import ru.winditest.data.local.messages.model.Message

@Immutable
data class ChatScreenState(
    val messages: List<Message> = emptyList(),
    val messageText: String = "",
    val chat: Chat? = null
)
