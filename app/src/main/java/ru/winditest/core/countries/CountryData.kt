package ru.winditest.core.countries

data class CountryData(
    private val cCodes: String,
    val countryPhoneCode: String,
    private val cNames: String, // bookkeeping only
) {
    val countryCode: String = cCodes.lowercase()
    val countryEmoji: String
        get() = countryCode
            .uppercase()
            .map { char ->
                Character.toChars(
                    Character.codePointAt("$char", 0) - 0x41 + 0x1F1E6
                )
            }
            .joinToString(separator = "") { charArray ->
                String(charArray)
            }
}
