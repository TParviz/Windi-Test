package ru.winditest.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.winditest.core.UiText
import ru.winditest.data.remote.api.model.user.Avatar
import ru.winditest.data.remote.api.model.user.EditUserInfoRequest
import ru.winditest.domain.repository.UserRepository
import ru.winditest.domain.use_case.EndSessionUseCase
import ru.winditest.presentation.screen.app.profile.ProfileEvent
import ru.winditest.presentation.viewmodel.state.auth.EditModeState
import ru.winditest.presentation.viewmodel.state.auth.UserScreenState
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UserRepository,
    private val endSession: EndSessionUseCase
): ViewModel() {

    private val _state = MutableStateFlow(UserScreenState())

    val state = _state.combine(repo.getUserData()) { state, user ->
        editModeState = EditModeState.fromUserData(user)
        state.copy(
            user = user
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserScreenState())

    private val _uiMessages = MutableSharedFlow<UiText>()
    val uiMessages = _uiMessages.asSharedFlow()

    private var pickedImageBase64: Deferred<String?>? = null

    var editModeState: EditModeState by mutableStateOf(EditModeState.fromUserData(state.value.user))
        private set

    private val ensuringUserNotNullJob: Job

    init {
        ensuringUserNotNullJob = viewModelScope.launch(Dispatchers.Default) {
            repo.getUserData().collect {
                if (it == null) {
                    updateUserData()
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.OnConfirmEdit -> {
                editUserData()
            }
            ProfileEvent.OnEdit -> {
                _state.update {
                    it.copy(
                        isEditing = true
                    )
                }
            }
            ProfileEvent.OnUpdate -> {
                viewModelScope.launch {
                    updateUserData()
                }
            }

            ProfileEvent.OnCancelEdit -> {
                _state.update {
                    it.copy(
                        isEditing = false,
                        pickedPhotoUri = null
                    )
                }
                pickedImageBase64 = null
            }
            is ProfileEvent.OnPhotoPicked -> {
                _state.update {
                    it.copy(
                        pickedPhotoUri = event.uri
                    )
                }
                pickedImageBase64 = viewModelScope.async(Dispatchers.Default) {
                     event.base64Factory()
                }
            }

            is ProfileEvent.OnCityChanged ->
                editModeState = editModeState.copy(city = event.city.ifEmpty { null })
            is ProfileEvent.OnInstagramChanged ->
                editModeState = editModeState.copy(instagram = event.inst.ifEmpty { null })
            is ProfileEvent.OnStatusChanged ->
                editModeState = editModeState.copy(status = event.status.ifEmpty { null })
            is ProfileEvent.OnVkChanged ->
                editModeState = editModeState.copy(vk = event.vk.ifEmpty { null })
            is ProfileEvent.OnBirthdayPicked ->
                editModeState = editModeState.copy(birthday = event.date)

            ProfileEvent.OnSignOut -> viewModelScope.launch {
                ensuringUserNotNullJob.cancel()
                endSession()
            }
        }
    }

    private suspend fun updateUserData() = withContext(Dispatchers.IO) {

        _state.update {
            it.copy(
                isUpdating = true
            )
        }

        repo.updateUserData().handle(
            onError = {
                _uiMessages.emit(it)
                _state.update { state ->
                    state.copy(
                        firstFetchError = it
                    )
                }
            }
        )

        _state.update {
            it.copy(
                isUpdating = false
            )
        }
    }

    private fun editUserData() = viewModelScope.launch(Dispatchers.IO) {

        _state.update {
            it.copy(
                isUpdating = true
            )
        }

        state.value.user?.let { user ->
            val avatar = pickedImageBase64?.await()
            repo.editUserData(
                data = EditUserInfoRequest(
                    name = user.name,
                    username = user.username,
                    birthday = editModeState.birthday?.toString(),
                    city = editModeState.city,
                    instagram = editModeState.instagram,
                    vk = editModeState.vk,
                    status = editModeState.status,
                    avatar = avatar?.let {
                        Avatar(
                            filename = it.take(10),
                            base64 = it
                        )
                    }
                )
            ).handle(
                onError = {
                    _uiMessages.emit(it)
                    _state.update {
                        it.copy(
                            isUpdating = false
                        )
                    }
                },
                onSuccess = {
                    pickedImageBase64 = null
                    editModeState = EditModeState.fromUserData(state.value.user)
                    _state.update {
                        it.copy(
                            isEditing = false,
                            pickedPhotoUri = null,
                            isUpdating = false
                        )
                    }
                }
            )
        }
    }
}