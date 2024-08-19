package ru.winditest.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.winditest.data.local.UserDataStorageImpl
import ru.winditest.data.local.auth_state.AuthStateStorageImpl
import ru.winditest.data.local.messages.MockMessagesDataSource
import ru.winditest.data.repository.AuthenticationRepositoryImpl
import ru.winditest.data.repository.MessagesRepositoryImpl
import ru.winditest.data.repository.UserRepositoryImpl
import ru.winditest.domain.local.AuthStateStorage
import ru.winditest.domain.local.MessagesDataSource
import ru.winditest.domain.local.UserDataStorage
import ru.winditest.domain.repository.AuthenticationRepository
import ru.winditest.domain.repository.MessagesRepository
import ru.winditest.domain.repository.UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    abstract fun bindAuthStateStorage(concretion: AuthStateStorageImpl): AuthStateStorage

    @Binds
    abstract fun bindUserDataStorage(concretion: UserDataStorageImpl): UserDataStorage

    @Binds
    abstract fun bindAuthRepository(concretion: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun bindMessagesDataSource(concretion: MockMessagesDataSource): MessagesDataSource

    @Binds
    abstract fun bindMessagesRepository(concretion: MessagesRepositoryImpl): MessagesRepository

    @Binds
    abstract fun bindUserDataRepository(concretion: UserRepositoryImpl): UserRepository
}