package ru.winditest.domain.use_case

import ru.winditest.R
import ru.winditest.core.UiText
import javax.inject.Inject

class UsernameValidationUseCase @Inject constructor() {

    private val validationRegex = "^[A-Za-z0-9_-]*\$".toRegex()

    operator fun invoke(username: String): UiText? {

        if (username.isEmpty()) return UiText.StringResource(R.string.username_empty)

        return if (validationRegex.matches(username)) null
        else UiText.StringResource(R.string.username_invalid)
    }
}