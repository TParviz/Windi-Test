package ru.winditest.presentation.viewmodel.state.auth

import ru.winditest.core.countries.CountryData

data class AuthenticationScreenState(
    val selectedCountry: CountryData,
    val phoneNumber: String = "",
    val hasRequestedCode: Boolean = false,
    val isRequestingCode: Boolean = false,
    val authenticationCode: String = "",

    val isCheckingAuthCode: Boolean = false
) {
    val fullPhoneNumber: String =
        "${selectedCountry.countryPhoneCode}$phoneNumber"
}