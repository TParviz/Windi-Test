package ru.winditest.presentation.screen.app.profile.components

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.winditest.R

@Composable
fun BoxScope.UploadImageButton(
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    isVisible: Boolean
) {
    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.Center),
        visible = isVisible
    ) {
        Button(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                    .6f
                ),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = stringResource(R.string.upload_photo))
        }
    }
}