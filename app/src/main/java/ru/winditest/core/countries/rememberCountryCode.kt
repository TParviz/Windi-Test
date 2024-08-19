package ru.winditest.core.countries

import android.content.Context
import android.telephony.TelephonyManager

val Context.countryCode: String
    get() = getSystemService(TelephonyManager::class.java).simCountryIso