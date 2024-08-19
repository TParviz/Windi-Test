@file:OptIn(ExperimentalMaterial3Api::class)

package ru.winditest.presentation.screen.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.winditest.R
import ru.winditest.core.countries.numberHint
import ru.winditest.presentation.components.MaskVisualTransformation
import ru.winditest.presentation.screen.auth.AuthenticationEvent
import ru.winditest.presentation.viewmodel.state.auth.AuthenticationScreenState

@Composable
fun PhoneNumberTextField(
    state: AuthenticationScreenState,
    onEvent: (AuthenticationEvent) -> Unit,
    onOpenCodeSelection: () -> Unit
) {

    val phoneNumber = state.phoneNumber

    val context = LocalContext.current

    val numberHint = remember(state.selectedCountry) {
        numberHint[state.selectedCountry.countryCode]?.let {
            context.getString(it)
        }
    }

    val maxPhoneNumberLength = remember(numberHint) {
        numberHint?.count { it.isDigit() } ?: 15 // Maximum phone number length as imposed by ITU
    }

    val visualTransformation = remember(numberHint) {
        if (numberHint == null) VisualTransformation.None
        else {
            MaskVisualTransformation(
                mask = numberHint.replace("\\d".toRegex(), "#")
            )
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 58.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    shape = MaterialTheme.shapes.extraSmall
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(
                    role = Role.Button,
                    onClick = onOpenCodeSelection,
                    enabled = !state.hasRequestedCode
                )
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .alpha(if (state.hasRequestedCode) .6f else 1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "${state.selectedCountry.countryEmoji} ${state.selectedCountry.countryPhoneCode}")
        }

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.length <= maxPhoneNumberLength) {
                    onEvent(AuthenticationEvent.PhoneNumberChanged(it))
                }
            },
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            maxLines = 1,
            singleLine = true,
            keyboardActions = KeyboardActions {
                onEvent(AuthenticationEvent.OnRequestCode)
            },
            label = {
                Text(text = stringResource(R.string.phone_number))
            },
            placeholder = {
                numberHint?.let {
                    Text(text = it)
                }
            },
            enabled = !state.hasRequestedCode
        )
    }
}