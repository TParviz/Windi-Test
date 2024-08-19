@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package ru.winditest.presentation.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mxalbert.sharedelements.SharedElement
import ru.winditest.R
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.navigation.LocalNavController
import ru.winditest.presentation.screen.auth.components.PhoneNumberTextField
import ru.winditest.presentation.screen.auth.components.RegistrationCodeField
import ru.winditest.presentation.util.UiEvent
import ru.winditest.presentation.util.collectIntoSnackbar
import ru.winditest.presentation.viewmodel.AuthViewModel
import ru.winditest.presentation.viewmodel.state.auth.AuthenticationScreenState

@Composable
fun Authentication(
    viewModel: AuthViewModel
) {
    val navController = LocalNavController.current

    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiMessages.collectIntoSnackbar(snackbarHostState)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    navController.navigate(it.route)
                }
            }
        }
    }

    AuthenticationContent(
        state = viewModel.authenticationState,
        onEvent = viewModel::onEvent,
        onOpenCodeSelection = {
            navController.navigate(AppScreen.Authentication.CodeSelectionBottomSheet) {
                popUpTo(AppScreen.Authentication.route) {
                    inclusive = true
                }
            }
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun AuthenticationContent(
    state: AuthenticationScreenState,
    onEvent: (AuthenticationEvent) -> Unit,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onOpenCodeSelection: () -> Unit
) {

    val codeInputFocusRequester = remember { FocusRequester() }

    val imeController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.please_sign_in))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = it.calculateTopPadding() + it.calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                SharedElement(
                    key = "phone",
                    screenKey = AppScreen.Authentication
                ) {
                    PhoneNumberTextField(
                        state = state,
                        onEvent = onEvent,
                        onOpenCodeSelection = onOpenCodeSelection
                    )
                }

                AnimatedVisibility(visible = !state.isRequestingCode && !state.hasRequestedCode) {
                    TextButton(
                        onClick = { onEvent(AuthenticationEvent.OnRequestCode) },
                        enabled = !state.isRequestingCode
                    ) {
                        if (state.isRequestingCode) {
                            CircularProgressIndicator()
                        } else {
                            Text(text = stringResource(R.string.reqest_auth_code))
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.hasRequestedCode,
                    enter = fadeIn() + slideInVertically { 2 * it }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.code_sent))

                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationCodeField(
                            modifier = Modifier
                                .focusRequester(codeInputFocusRequester),
                            text = state.authenticationCode,
                            codeLength = 6,
                            onTextChange = { it ->
                                if (it.length == 6) imeController?.hide()
                                onEvent(AuthenticationEvent.AuthCodeChanged(it))
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword
                            ),
                            enabled = !state.isCheckingAuthCode
                        )
                    }

                    LaunchedEffect(Unit) {
                        codeInputFocusRequester.requestFocus()
                    }
                }
            }

        }

    }
}