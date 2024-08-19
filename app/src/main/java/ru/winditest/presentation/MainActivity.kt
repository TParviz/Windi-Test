package ru.winditest.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.mxalbert.sharedelements.SharedElementsRoot
import dagger.hilt.android.AndroidEntryPoint
import ru.winditest.presentation.navigation.AppNavigation
import ru.winditest.presentation.screen.auth.navigation.AuthenticationNavigation
import ru.winditest.presentation.theme.WindiTestTheme
import ru.winditest.presentation.util.LocalActivity
import ru.winditest.presentation.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WindiTestTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by authViewModel.authState.collectAsState(initial = null)

                    // There is no need to host those user flows under one nav graph
                    // since they do not interact at all
                    when (authState?.isAuthorized) {
                        true -> {
                            CompositionLocalProvider(
                                LocalActivity provides this
                            ) {
                                SharedElementsRoot {
                                    AppNavigation()
                                }
                            }
                        }
                        false -> {
                            SharedElementsRoot {
                                AuthenticationNavigation(authViewModel)
                            }
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}