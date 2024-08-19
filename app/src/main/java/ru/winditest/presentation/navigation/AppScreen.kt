package ru.winditest.presentation.navigation

sealed class AppScreen(
    val route: String,
    val navLink: String = route.substringBefore("/{")
) {
    object Authentication: AppScreen("authentication") {
        const val CodeSelectionBottomSheet = "authentication/code_selection"
    }

    object Registration: AppScreen("registration")


    object Messages: AppScreen("messages")
    object Chat: AppScreen("messages/chat/{id}")


    object Profile: AppScreen("profile")
}
