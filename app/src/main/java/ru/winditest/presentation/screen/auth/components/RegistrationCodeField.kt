package ru.winditest.presentation.screen.auth.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.winditest.presentation.theme.WindiTestTheme

@Composable
fun RegistrationCodeField(
    modifier: Modifier = Modifier,
    text: String,
    codeLength: Int,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            if (it.length > codeLength) return@BasicTextField
            onTextChange(it)
        },
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(codeLength) { index ->
                    val char = text.getOrElse(index) { ' ' }
                    OtpUnit(
                        modifier = Modifier
                            .alpha(
                                if (enabled) 1f else .6f
                            ),
                        char = char,
                        isFocused = text.length == index
                    )
                }
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        enabled = enabled
    )
}

@Composable
private fun OtpUnit(
    modifier: Modifier = Modifier,
    char: Char,
    isFocused: Boolean
) {

    val backgroundColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surfaceColorAtElevation(
            elevation = if (isFocused) 16.dp else 4.dp
        )
    )

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent
    )

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.extraSmall
            )
            .clip(MaterialTheme.shapes.extraSmall)
            .border(
                width = 2.dp,
                shape = MaterialTheme.shapes.extraSmall,
                color = borderColor
            )
            .padding(8.dp)
            .defaultMinSize(minWidth = 20.dp, minHeight = 40.dp)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char.toString(),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview
@Composable
fun OtpPreview() {
    WindiTestTheme {

        var text by remember { mutableStateOf("") }

        Surface {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                RegistrationCodeField(
                    text = text,
                    codeLength = 7,
                    onTextChange = {
                        text = it
                    }
                )
            }
        }
    }
}