@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package ru.winditest.presentation.screen.app.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mxalbert.sharedelements.SharedElement
import ru.winditest.R
import ru.winditest.core.extentions.fromIso8601ToDateTime
import ru.winditest.data.local.messages.model.Message
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.navigation.LocalNavController
import ru.winditest.presentation.viewmodel.ChatViewModel
import ru.winditest.presentation.viewmodel.state.messages.ChatScreenState

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navContoller = LocalNavController.current

    ChatScreenContent(
        state = state,
        currentUserId = viewModel.currentUserId,
        onEvent = viewModel::onEvent,
        onNavigateBack = {
            navContoller.popBackStack()
        }
    )
}

@Composable
private fun ChatScreenContent(
    state: ChatScreenState,
    currentUserId: Int,
    onEvent: (ChatEvent) -> Unit,
    onNavigateBack: () -> Unit
) {

    state.chat ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SharedElement(
                            key = "${state.chat.id}/avatar",
                            screenKey = AppScreen.Chat.route
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                                        shape = CircleShape
                                    )
                                    .size(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.chat.companion.name.first().toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    lineHeight = 0.sp
                                )
                            }
                        }

                        SharedElement(
                            key = "${state.chat.id}/name",
                            screenKey = AppScreen.Chat.route
                        ) {
                            Text(
                                text = state.chat.companion.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .imeNestedScroll(),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                items(
                    items = state.messages
                ) {
                    MessageBubble(
                        message = it,
                        currentUserId = currentUserId
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                value = state.messageText,
                onValueChange = {
                    onEvent(ChatEvent.OnTypeMessage(it))
                },
                shape = RectangleShape,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                placeholder = {
                    Text(text = stringResource(R.string.message))
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onEvent(ChatEvent.OnSendMessage)
                        },
                        enabled = state.messageText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun MessageBubble(
    modifier: Modifier = Modifier,
    message: Message,
    currentUserId: Int
) {

    val (date, time) = message.sentAt.fromIso8601ToDateTime(LocalContext.current)

    val isCurrentUserAuthor = message.authorId == currentUserId

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                if (isCurrentUserAuthor) PaddingValues(start = 64.dp)
                else PaddingValues(end = 64.dp)
            )
            .then(modifier)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isCurrentUserAuthor) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp)
                .align(
                    if (isCurrentUserAuthor) Alignment.CenterEnd
                    else Alignment.CenterStart
                )
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                text = message.content
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = "$date ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(.8f)
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(.6f)
                )
            }
        }
    }
}