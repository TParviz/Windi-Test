@file:OptIn(ExperimentalMaterial3Api::class)

package ru.winditest.presentation.screen.app.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mxalbert.sharedelements.SharedElement
import kotlinx.coroutines.launch
import ru.winditest.R
import ru.winditest.core.extentions.fromIso8601ToDateTime
import ru.winditest.data.local.messages.model.Chat
import ru.winditest.data.local.messages.model.Message
import ru.winditest.data.remote.api.model.user.ProfileData
import ru.winditest.presentation.navigation.AppScreen
import ru.winditest.presentation.navigation.LocalNavController
import ru.winditest.presentation.theme.WindiTestTheme
import ru.winditest.presentation.viewmodel.MessagesViewModel

@Composable
fun Messages(
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState()

    val navController = LocalNavController.current

    MessagesContent(
        chats = chats,
        onChatClicked = {
            navController.navigate(
                "${AppScreen.Chat.navLink}/${it.id}"
            )
        },
        onProfileClicked = {
            navController.navigate(AppScreen.Profile.route)
        }
    )
}

@Composable
private fun MessagesContent(
    chats: List<Chat>,
    onChatClicked: (Chat) -> Unit,
    onProfileClicked: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(stringResource(R.string.messages))
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = onProfileClicked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Account"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHost)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = chats,
                key = { it.id }
            ) {
                ChatItem(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onChatClicked(it)
                        }
                        .padding(4.dp),
                    chat = it
                )
            }

            items(10) {
                ChatItem(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            coroutineScope.launch {
                                snackbarHost.showSnackbar(
                                    message = context.getString(R.string.nothing_to_see)
                                )
                            }
                        }
                        .padding(4.dp),
                    chat = Chat(
                        id = "Whatever",
                        companion = ProfileData(
                            name = "TestNum3",
                            username = "test3",
                            birthday = null,
                            city = null,
                            vk = null,
                            instagram = null,
                            status = null,
                            avatar = null,
                            id = 69,
                            last = null,
                            online = true,
                            created = "2023-07-20T18:16:44+00:00",
                            phone = "+79999999999",
                            completedTask = 0,
                            avatars = null
                        ),
                        lastMessage = Message(
                            content = "This is a sample chat to fill the screen",
                            sentAt = "",
                            isRead = false,
                            authorId = 69
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun ChatItem(
    modifier: Modifier = Modifier,
    chat: Chat
) {
    val (_, time) = chat.lastMessage.sentAt.fromIso8601ToDateTime(LocalContext.current, false)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SharedElement(
            key = "${chat.id}/avatar",
            screenKey = AppScreen.Messages.route
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp),
                        shape = CircleShape
                    )
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chat.companion.name.first().toString(),
                    style = MaterialTheme.typography.displaySmall,
                    lineHeight = 0.sp
                )
            }
        }

        Column {
            Row {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    SharedElement(
                        key = "${chat.id}/name",
                        screenKey = AppScreen.Messages.route
                    ) {
                        Text(
                            text = chat.companion.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }

                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = chat.lastMessage.content,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                if (!chat.lastMessage.isRead) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatItemPreview() {
    WindiTestTheme {
        Surface {
            ChatItem(
                chat = Chat(
                    id = "asd",
                    companion = ProfileData(
                        name = "Александр Крупин",
                        username = "AlexK",
                        birthday = null,
                        city = null,
                        vk = null,
                        instagram = null,
                        status = null,
                        avatar = null,
                        id = 69,
                        last = null,
                        online = true,
                        created = "2023-07-20T18:16:44+0000",
                        phone = "+79999999999",
                        completedTask = 0,
                        avatars = null
                    ),
                    lastMessage = Message(
                        content = "Hello",
                        sentAt = "",
                        isRead = false,
                        authorId = 69
                    )
                )
            )
        }
    }
}