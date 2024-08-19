package ru.winditest.domain.use_case

import ru.winditest.R
import ru.winditest.core.UiText
import javax.inject.Inject

class NameValidationUseCase @Inject constructor() {
    operator fun invoke(name: String): UiText? =
        if (name.isBlank()) UiText.StringResource(R.string.name_empty)
        else null
}