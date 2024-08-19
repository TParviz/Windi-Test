@file:OptIn(ExperimentalMaterialNavigationApi::class)

package ru.winditest.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import ru.winditest.presentation.screen.app.messages.ChatScreen
import ru.winditest.presentation.screen.app.messages.Messages
import ru.winditest.presentation.screen.app.profile.Profile

@Composable
fun AppNavigation() {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = MaterialTheme.shapes.large,
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            sheetContentColor = MaterialTheme.colorScheme.onSurface
        ) {

            NavHost(
                navController = navController,
                startDestination = "messages_flow"
            ) {
                messages()
                profile()
            }
        }
    }
}

fun NavGraphBuilder.messages() {
    navigation(
        route = "messages_flow",
        startDestination = AppScreen.Messages.route
    ) {
        composable(
            route = AppScreen.Messages.route
        ) {
            Messages()
        }

        composable(
            route = AppScreen.Chat.route,
            arguments = listOf(
                navArgument(
                    name = "id"
                ) {
                    type = NavType.StringType
                }
            )
        ) {
            ChatScreen()
        }
    }
}

fun NavGraphBuilder.profile() {
    navigation(
        route = "profile_flow",
        startDestination = AppScreen.Profile.route
    ) {
        composable(
            route = AppScreen.Profile.route
        ) {
            Profile()
        }
    }
}