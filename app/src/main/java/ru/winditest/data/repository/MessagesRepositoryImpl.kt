package ru.winditest.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import ru.winditest.core.ResponseHandler
import ru.winditest.data.local.messages.model.Chat
import ru.winditest.domain.local.AuthStateStorage
import ru.winditest.domain.local.MessagesDataSource
import ru.winditest.domain.repository.MessagesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val dataSource: MessagesDataSource,
    private val authStateStorage: AuthStateStorage,
    private val handler: ResponseHandler
) : MessagesRepository {
    override val currentUserId: Int = runBlocking {
        authStateStorage.authState.first()!!.userId!!
    }

    override suspend fun getChat(chatId: String): Chat =
        dataSource.getChats().find { it.id == chatId }!!

    override fun getChats(): Flow<List<Chat>> = flow {
        emit(dataSource.getChats())
    }

    // In reality this would be a WebSocket connection
    override fun getLatestMessages(chatId: String, limit: Int) = flow {
        while (true) {
            delay(300)
            emit(dataSource.getLatestMessages(chatId, limit))
        }
    }.distinctUntilChanged()

    override suspend fun sendMessage(chatId: String, text: String) = handler {
        dataSource.sendMessage(chatId, text)
    }
}