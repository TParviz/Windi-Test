package ru.winditest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import ru.winditest.domain.repository.MessagesRepository
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    repo: MessagesRepository
): ViewModel() {

    val chats = repo.getChats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}