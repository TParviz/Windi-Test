@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)

package ru.winditest.presentation.screen.app.profile

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ru.winditest.BuildConfig
import ru.winditest.R
import ru.winditest.core.UiText
import ru.winditest.presentation.components.IconizedRow
import ru.winditest.presentation.components.backdrop.BackdropScaffold
import ru.winditest.presentation.components.backdrop.BackdropValue
import ru.winditest.presentation.components.backdrop.rememberBackdropScaffoldState
import ru.winditest.presentation.screen.app.profile.components.ProfileEditTextField
import ru.winditest.presentation.screen.app.profile.components.UploadImageButton
import ru.winditest.presentation.screen.app.profile.components.rememberBirthdayPicker
import ru.winditest.presentation.util.LocalActivity
import ru.winditest.presentation.util.collectIntoSnackbar
import ru.winditest.presentation.util.toBase64
import ru.winditest.presentation.viewmodel.UserViewModel
import ru.winditest.presentation.viewmodel.state.auth.EditModeState
import ru.winditest.presentation.viewmodel.state.auth.UserScreenState

@Composable
fun Profile(
    viewModel: UserViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isUpdating)

    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiMessages.collectIntoSnackbar(snackbarHostState)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.onEvent(ProfileEvent.OnUpdate) }
    ) {
        ProfileContent(
            state = state,
            editState = viewModel.editModeState,
            onEvent = viewModel::onEvent,
            snackbarHost = snackbarHostState
        )
    }
}

@Composable
private fun ProfileContent(
    state: UserScreenState,
    editState: EditModeState,
    onEvent: (ProfileEvent) -> Unit,
    snackbarHost: SnackbarHostState
) {
    val user = state.user

    val contentResolver = LocalContext.current.contentResolver

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onEvent(
                ProfileEvent.OnPhotoPicked(uri) {
                    uri?.let {
                        contentResolver.openInputStream(uri).use {
                            BitmapFactory.decodeStream(it).toBase64()
                        }
                    }
                }
            )
        }
    )

    val screenWidth = LocalView.current.width

    val screenHeightDp = with(LocalDensity.current) {
        LocalView.current.height.toDp()
    }
    val backdropScaffoldState = rememberBackdropScaffoldState(
        initialValue = BackdropValue.Peeking
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.profile))
                },
                actions = {

                    AnimatedContent(
                        targetState = state.isEditing
                    ) {
                        if (it) {
                            IconButton(
                                onClick = {
                                    onEvent(ProfileEvent.OnConfirmEdit)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save"
                                )
                            }
                        } else {

                            IconButton(
                                onClick = { onEvent(ProfileEvent.OnEdit) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.isEditing
                    ) {
                        IconButton(
                            onClick = { onEvent(ProfileEvent.OnCancelEdit) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHost)
        }
    ) {

        val offsetFactory: Density.() -> IntOffset = {
            IntOffset(
                x = 0,
                y = (
                        backdropScaffoldState.offset.value.toInt() -
                                Integer.min(
                                    screenWidth,
                                    screenHeightDp
                                        .toPx()
                                        .toInt()
                                )
                                + 16.dp
                            .toPx()
                            .toInt()
                        ) / 2
            )
        }

        BackdropScaffold(
            modifier = Modifier.padding(it),
            scaffoldState = backdropScaffoldState,
            backLayerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    user?.let { user ->
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .offset(offsetFactory),
                            model = state.pickedPhotoUri
                                ?: ImageRequest.Builder(LocalContext.current)
                                    .data("https://plannerok.ru/${user.avatars?.bigAvatar}")
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "Avatar",
                            contentScale = ContentScale.FillWidth,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(screenHeightDp / 3),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(screenHeightDp / 3)
                                ) {
                                    UploadImageButton(
                                        photoPickerLauncher = photoPickerLauncher,
                                        isVisible = state.isEditing
                                    )
                                }
                            },
                            success = {
                                SubcomposeAsyncImageContent(
                                    modifier = Modifier.fillMaxWidth()
                                )
                                UploadImageButton(
                                    photoPickerLauncher = photoPickerLauncher,
                                    isVisible = state.isEditing
                                )
                            }
                        )
                    }
                }
            },
            peekHeight = 0.01.dp,
            partialPeekHeight = screenHeightDp / 3,
            frontLayerContent = {
                user?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            UserInfo(
                                state = state,
                                editState = editState,
                                onEvent = onEvent
                            )
                        }
                    }
                } ?: FetchingError(
                    errorText = state.firstFetchError,
                    onRetry = { onEvent(ProfileEvent.OnUpdate) }
                )
            },
            frontLayerBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            frontLayerShape = MaterialTheme.shapes.large,
            frontLayerElevation = 16.dp
        )
    }
}

@Composable
private fun FetchingError(
    errorText: UiText?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        errorText?.let {
            Text(text = it.asString())

            TextButton(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun UserInfo(
    state: UserScreenState,
    editState: EditModeState,
    onEvent: (ProfileEvent) -> Unit
) {
    val user = state.user!! // Cannot be null because caller ensures it
    val userZodiacSign = state.userZodiacSign?.asString()

    val activity = LocalActivity.current

    val birthdayPicker = rememberBirthdayPicker(
        initialDate = state.userBirthdayAsLocalDate,
        onDateSelected = {
            onEvent(ProfileEvent.OnBirthdayPicked(it))
        }
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${user.name} (${user.username})",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(0.dp))

        IconizedRow(
            imageVector = Icons.Default.PhoneEnabled,
            contentDescription = stringResource(R.string.phone_number)
        ) {
            Text(text = user.phone)
        }

        AnimatedContent(
            targetState = state.isEditing
        ) { isEditing ->
            if (isEditing) {
                ProfileEditTextField(
                    value = editState.city ?: "",
                    enabled = !state.isUpdating,
                    icon = Icons.Default.LocationCity,
                    label = stringResource(R.string.profile_ctiy),
                    onValueChange = { onEvent(ProfileEvent.OnCityChanged(it)) }
                )
            } else if (user.city != null) {
                IconizedRow(
                    imageVector = Icons.Default.LocationCity,
                    contentDescription = null,
                    opacity = .6f
                ) {
                    Text(text = user.city)
                }
            }
        }

        AnimatedContent(
            targetState = state.isEditing
        ) { isEditing ->
            if (isEditing) {
                ProfileEditTextField(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable {
                            if (birthdayPicker.isAdded) return@clickable
                            birthdayPicker.show(
                                activity.supportFragmentManager,
                                "Date picker"
                            )
                        },
                    value = editState.formattedBirthday ?: "",
                    enabled = false,
                    colors = if (state.isUpdating) {
                        TextFieldDefaults.outlinedTextFieldColors()
                    } else {
                        TextFieldDefaults.outlinedTextFieldColors(
                            disabledTextColor = LocalContentColor.current,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    readOnly = true,
                    icon = Icons.Default.Cake,
                    label = stringResource(R.string.user_birthday),
                    onValueChange = {
                        if (it.isEmpty()) onEvent(ProfileEvent.OnBirthdayPicked(null))
                    }
                )
            } else if (user.birthday != null || state.isEditing) {
                IconizedRow(
                    imageVector = Icons.Default.Cake,
                    opacity = .6f
                ) {
                    Text(text = "${state.formattedBirthday} ($userZodiacSign)")
                }
            }
        }


        AnimatedContent(
            targetState = state.isEditing
        ) { isEditing ->
            if (isEditing) {
                ProfileEditTextField(
                    value = editState.vk ?: "",
                    enabled = !state.isUpdating,
                    painter = painterResource(R.drawable.ic_vk),
                    label = stringResource(R.string.profile_vk),
                    onValueChange = { onEvent(ProfileEvent.OnVkChanged(it)) }
                )
            } else if (user.vk != null) {
                IconizedRow(
                    painter = painterResource(R.drawable.ic_vk),
                    opacity = .6f
                ) {
                    Text(text = user.vk)
                }
            }
        }

        AnimatedContent(
            targetState = state.isEditing
        ) { isEditing ->
            if (isEditing) {
                ProfileEditTextField(
                    value = editState.instagram ?: "",
                    enabled = !state.isUpdating,
                    painter = painterResource(R.drawable.ic_inst),
                    label = stringResource(R.string.profile_inst),
                    onValueChange = { onEvent(ProfileEvent.OnInstagramChanged(it)) }
                )
            } else if (user.instagram != null) {
                IconizedRow(
                    painter = painterResource(R.drawable.ic_inst),
                    opacity = .6f
                ) {
                    Text(text = user.instagram)
                }
            }
        }

        AnimatedContent(
            targetState = state.isEditing
        ) { isEditing ->
            if (isEditing) {
                ProfileEditTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editState.status ?: "",
                    enabled = !state.isUpdating,
                    icon = null,
                    label = stringResource(R.string.user_about),
                    onValueChange = { onEvent(ProfileEvent.OnStatusChanged(it)) }
                )
            } else if (user.status != null) {
                Column {
                    Text(
                        text = stringResource(R.string.user_about),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = user.status)
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            visible = !state.isEditing
        ) {
            TextButton(
                onClick = { onEvent(ProfileEvent.OnSignOut) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(R.string.log_out))
            }
        }
    }
}