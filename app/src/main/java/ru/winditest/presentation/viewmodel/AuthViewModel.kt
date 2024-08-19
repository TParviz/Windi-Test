package ru.winditest.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.winditest.core.UiText
import ru.winditest.core.countries.countryCode
import ru.winditest.core.countries.countryDataMap
import ru.winditest.core.countries.russia
import ru.winditest.data.remote.api.model.UserAuthCode
import ru.winditest.data.remote.api.model.UserPhone
import ru.winditest.data.remote.api.model.UserRegistrationRequest
import ru.winditest.domain.repository.AuthenticationRepository
import ru.winditest.domain.use_case.NameValidationUseCase
import ru.winditest.domain.use_case.UsernameValidationUseCase
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.screen.auth.AuthenticationEvent
import ru.winditest.presentation.screen.auth.RegistrationEvent
import ru.winditest.presentation.util.UiEvent
import ru.winditest.presentation.viewmodel.state.auth.AuthenticationScreenState
import ru.winditest.presentation.viewmodel.state.auth.RegistrationScreenState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthenticationRepository,
    private val validateUsername: UsernameValidationUseCase,
    private val validateName: NameValidationUseCase,
    private val application: Application // ugh. Would love to know how I could've avoided this
): AndroidViewModel(application) {
    val authState = repo.authState

    var authenticationState by mutableStateOf(
        AuthenticationScreenState(
            selectedCountry = countryDataMap[application.countryCode] ?: russia
        )
    )
        private set

    var registrationState by mutableStateOf(
        RegistrationScreenState()
    )

    private val _uiMessages = MutableSharedFlow<UiText>()
    val uiMessages = _uiMessages.asSharedFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEvent(event: AuthenticationEvent) {
        when (event) {
            is AuthenticationEvent.OnCountrySelected -> {
                authenticationState = authenticationState.copy(
                    selectedCountry = event.country
                )
            }
            AuthenticationEvent.OnRequestCode -> {
                authenticationState = authenticationState.copy(
                    isRequestingCode = true
                )
                requestAuthCode()
            }
            is AuthenticationEvent.PhoneNumberChanged -> {
                authenticationState = authenticationState.copy(
                    phoneNumber = event.phoneNumber
                )
            }
            is AuthenticationEvent.AuthCodeChanged -> {
                if (event.authCode.length == 6) {
                    checkAuthCode()
                }
                authenticationState = authenticationState.copy(
                    authenticationCode = event.authCode
                )
            }
        }
    }

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnNameChanged -> {
                registrationState = registrationState.copy(
                    name = event.name,
                    nameError = validateName(event.name)
                )
            }
            is RegistrationEvent.OnUsernameChanged -> {
                registrationState = registrationState.copy(
                    username = event.username,
                    usernameError = validateUsername(event.username)
                )
            }
            RegistrationEvent.OnRegister -> {
                val nameValidationResult = validateName(registrationState.name)
                val usernameValidationResult = validateUsername(registrationState.username)

                if (nameValidationResult != null || usernameValidationResult != null) {
                    registrationState = registrationState.copy(
                        nameError = nameValidationResult,
                        usernameError = usernameValidationResult
                    )
                    return
                }
                registrationState = registrationState.copy(
                    isRegistering = true
                )
                register()
            }
        }
    }

    private fun requestAuthCode() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.requestAuthCode(
                UserPhone(
                    phone = "${authenticationState.selectedCountry.countryPhoneCode}${authenticationState.phoneNumber}"
                )
            ).handle(
                onSuccess = {
                    if (it.isSuccess) {
                        authenticationState = authenticationState.copy(
                            isRequestingCode = false,
                            hasRequestedCode = true
                        )
                    }
                },
                onError = {
                    _uiMessages.emit(it)
                    authenticationState = authenticationState.copy(
                        isRequestingCode = false,
                    )
                }
            )
        }
    }

    private fun checkAuthCode() = viewModelScope.launch(Dispatchers.IO) {
        authenticationState = authenticationState.copy(
            isCheckingAuthCode = true
        )
        repo.checkAuthCode(
            UserAuthCode(
                code = authenticationState.authenticationCode,
                phone = authenticationState.fullPhoneNumber
            )
        ).handle(
            onSuccess = {
                if (!it.doesUserExist) {
                    _uiEvents.emit(
                        UiEvent.Navigate(AppScreen.Registration.route)
                    )
                } else {
                    authenticationState = AuthenticationScreenState(
                        selectedCountry = countryDataMap[application.countryCode] ?: russia
                    )
                }
            },
            onError = {
                _uiMessages.emit(it)
                authenticationState = authenticationState.copy(
                    authenticationCode = ""
                )
            }
        )
        authenticationState = authenticationState.copy(
            isCheckingAuthCode = false
        )
    }

    private fun register() = viewModelScope.launch(Dispatchers.IO) {
        repo.registerUser(
            request = UserRegistrationRequest(
                name = registrationState.name,
                phone = authenticationState.fullPhoneNumber,
                userName = registrationState.username
            )
        ).handle(
            onError = {
                _uiMessages.emit(it)
            },
            onSuccess = {
                registrationState = RegistrationScreenState()
                authenticationState = AuthenticationScreenState(
                    selectedCountry = countryDataMap[application.countryCode] ?: russia
                )
            }
        )
        registrationState = registrationState.copy(
            isRegistering = false
        )
    }
}