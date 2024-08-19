package ru.winditest.presentation.util

import androidx.compose.runtime.compositionLocalOf
import androidx.fragment.app.FragmentActivity

val LocalActivity = compositionLocalOf<FragmentActivity> {
    error("Why in the world would you access an Activity now?")
}