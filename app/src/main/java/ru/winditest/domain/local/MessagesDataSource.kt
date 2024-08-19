package ru.winditest.domain.local

import ru.winditest.data.local.messages.model.Chat
import ru.winditest.data.local.messages.model.Message

interface MessagesDataSource {
    suspend fun getChats(): List<Chat>
    suspend fun getLatestMessages(chatId: String, limit: Int): List<Message>
    suspend fun sendMessage(chatId: String, message: String)
}