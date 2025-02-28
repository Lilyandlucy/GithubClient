package com.github.client.domain.usecase

import com.github.client.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LogoutUseCase(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<Boolean> = flow {
        try {
            authRepository.clearAuthInfo()
            emit(true)
        } catch (e: Exception) {
            emit(false)
        }
    }
}
