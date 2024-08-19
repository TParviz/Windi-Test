package ru.winditest.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.winditest.domain.repository.MessagesRepository
import ru.winditest.presentation.screen.app.messages.ChatEvent
import ru.winditest.presentation.viewmodel.state.messages.ChatScreenState
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repo: MessagesRepository
): ViewModel() {
    private val chatId: String = savedState["id"]!!
    val currentUserId = repo.currentUserId

    private val _state = MutableStateFlow(ChatScreenState())

    private val messages = repo.getLatestMessages(
        chatId = chatId,
        limit = 10
    )

    val state = _state.combine(messages) { state, messages ->
        state.copy(
            messages = messages,
            chat = repo.getChat(chatId)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatScreenState())

    fun onEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.OnSendMessage -> {
                viewModelScope.launch {
                    repo.sendMessage(
                        chatId = chatId,
                        text = _state.value.messageText
                    ).handle(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    messageText = ""
                                )
                            }
                        }
                    )
                }
            }
            is ChatEvent.OnTypeMessage -> {
                _state.update {
                    it.copy(
                        messageText = event.text
                    )
                }
            }
        }
    }
}