package ru.winditest.presentation.util

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import ru.winditest.core.UiText

@SuppressLint("ComposableNaming")
@Composable
fun SharedFlow<UiText>.collectIntoSnackbar(
    snackbarHost: SnackbarHostState
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch {
            this@collectIntoSnackbar.collect { message ->
                snackbarHost.showSnackbar(
                    message = message.asString(context)
                )
            }
        }
        this@collectIntoSnackbar.collect {
            snackbarHost.currentSnackbarData?.dismiss()
        }
    }
}