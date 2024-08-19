package ru.winditest.data.local.messages.model

data class Message(
    val content: String,
    val sentAt: String,
    val isRead: Boolean,
    val authorId: Int
)
