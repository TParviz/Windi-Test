@file:OptIn(ExperimentalMaterialNavigationApi::class)

package ru.winditest.presentation.screen.auth.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.navigation.LocalNavController
import ru.winditest.presentation.screen.auth.Authentication
import ru.winditest.presentation.screen.auth.CountrySelectionBottomSheet
import ru.winditest.presentation.screen.auth.Registration
import ru.winditest.presentation.viewmodel.AuthViewModel

@Composable
fun AuthenticationNavigation(
    authViewModel: AuthViewModel
) {

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)

    val authenticationGraphName = "auth"

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
                startDestination = authenticationGraphName
            ) {
                navigation(
                    route = authenticationGraphName,
                    startDestination = AppScreen.Authentication.route
                ) {

                    composable(
                        route = AppScreen.Authentication.route
                    ) {
                        Authentication(authViewModel)
                    }

                    composable(
                        route = AppScreen.Registration.route
                    ) {
                        Registration(authViewModel)
                    }

                    bottomSheet(
                        route = AppScreen.Authentication.CodeSelectionBottomSheet
                    ) {
                        CountrySelectionBottomSheet(authViewModel)
                    }
                }
            }
        }
    }
}