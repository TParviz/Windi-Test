package ru.winditest.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.winditest.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repo: UserRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repo.updateUserData()
    }
}