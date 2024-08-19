@file:OptIn(ExperimentalMaterial3Api::class)

package ru.winditest.presentation.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mxalbert.sharedelements.SharedElement
import ru.winditest.R
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.screen.auth.components.PhoneNumberTextField
import ru.winditest.presentation.util.collectIntoSnackbar
import ru.winditest.presentation.viewmodel.AuthViewModel
import ru.winditest.presentation.viewmodel.state.auth.AuthenticationScreenState
import ru.winditest.presentation.viewmodel.state.auth.RegistrationScreenState

@Composable
fun Registration(
    viewModel: AuthViewModel
) {

    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiMessages.collectIntoSnackbar(snackbarHostState)

    RegistrationContent(
        authState = viewModel.authenticationState,
        registrationState = viewModel.registrationState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun RegistrationContent(
    authState: AuthenticationScreenState,
    registrationState: RegistrationScreenState,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onEvent: (RegistrationEvent) -> Unit
) {

    val nameFocusRequester = remember {
        FocusRequester()
    }

    val usernameFocusRequester = remember { FocusRequester() }

    val scrollBehavior = pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tell_us_more)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = it.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            SharedElement(
                key = "phone",
                screenKey = AppScreen.Registration
            ) {
                PhoneNumberTextField(
                    state = authState,
                    onEvent = {},
                    onOpenCodeSelection = {}
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(nameFocusRequester)
                    .fillMaxWidth(),
                value = registrationState.name,
                onValueChange = { onEvent(RegistrationEvent.OnNameChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                label = {
                    Text(stringResource(R.string.name))
                },
                keyboardActions = KeyboardActions {
                    usernameFocusRequester.requestFocus()
                },
                isError = registrationState.nameError != null,
                supportingText = {
                    AnimatedVisibility(visible = registrationState.nameError != null) {
                        if (registrationState.nameError != null) {
                            Text(
                                text = registrationState.nameError.asString(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                enabled = !registrationState.isRegistering
            )

            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(usernameFocusRequester)
                    .fillMaxWidth(),
                value = registrationState.username,
                onValueChange = { onEvent(RegistrationEvent.OnUsernameChanged(it)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                label = {
                    Text(stringResource(R.string.user_name))
                },
                keyboardActions = KeyboardActions {
                    if (registrationState.usernameError == null) {
                        onEvent(RegistrationEvent.OnRegister)
                    }
                },
                isError = registrationState.usernameError != null,
                supportingText = {
                    AnimatedVisibility(visible = registrationState.usernameError != null) {
                        if (registrationState.usernameError != null) {
                            Text(
                                text = registrationState.usernameError.asString(),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                enabled = !registrationState.isRegistering
            )

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                onClick = { onEvent(RegistrationEvent.OnRegister) },
                enabled = registrationState.usernameError == null &&
                        registrationState.nameError == null &&
                        !registrationState.isRegistering
            ) {
                if (registrationState.isRegistering) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = stringResource(R.string.register)
                    )
                }
            }
        }
    }
}