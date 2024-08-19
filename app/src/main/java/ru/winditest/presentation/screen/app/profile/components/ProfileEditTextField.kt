@file:OptIn(ExperimentalMaterial3Api::class)

package ru.winditest.presentation.screen.app.profile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ProfileEditTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: String,
    icon: ImageVector? = null,
    label: String,
    readOnly: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        leadingIcon = icon?.let {
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        },
        label = {
            Text(label)
        },
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null
                )
            }
        },
        readOnly = readOnly,
        colors = colors
    )
}
@Composable
fun ProfileEditTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: String,
    painter: Painter? = null,
    label: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        leadingIcon = painter?.let {
            {
                Icon(
                    painter = painter,
                    contentDescription = null
                )
            }
        },
        label = {
            Text(label)
        },
        trailingIcon = {
            IconButton(onClick = { onValueChange("") }) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null
                )
            }
        },
        readOnly = readOnly
    )
}