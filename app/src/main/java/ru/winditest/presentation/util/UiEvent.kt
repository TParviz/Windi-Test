package ru.winditest.presentation.util

sealed interface UiEvent {
    data class Navigate(val route: String): UiEvent
}