package ru.winditest.domain.use_case

import ru.winditest.domain.repository.AuthenticationRepository
import javax.inject.Inject

class EndSessionUseCase @Inject constructor(
    private val authRepo: AuthenticationRepository
) {
    suspend operator fun invoke() = authRepo.endSession()
}