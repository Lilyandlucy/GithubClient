package com.github.client.domain.usecase

import com.github.client.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class CheckAuthStatusUseCase(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<Boolean> {
        return authRepository.isLoggedIn()
    }
}
