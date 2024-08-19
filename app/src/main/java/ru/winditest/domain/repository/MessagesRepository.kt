package ru.winditest.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.winditest.core.Resource
import ru.winditest.data.local.messages.model.Chat
import ru.winditest.data.local.messages.model.Message

interface MessagesRepository {
    val currentUserId: Int
    suspend fun getChat(chatId: String): Chat
    fun getChats(): Flow<List<Chat>>
    fun getLatestMessages(chatId: String, limit: Int): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, text: String): Resource<Unit>
}