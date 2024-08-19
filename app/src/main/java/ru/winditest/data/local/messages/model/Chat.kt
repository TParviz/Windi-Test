package ru.winditest.data.local.messages.model

import ru.winditest.data.remote.api.model.user.ProfileData

data class ChatEntity(
    val id: String,
    val companionId: Int,
)

data class Chat(
    val id: String,
    val companion: ProfileData,
    val lastMessage: Message
)