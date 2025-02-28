package com.github.client.domain.usecase

import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val gitHubRepository: GitHubRepository
) {

    operator fun invoke(code: String): Flow<Boolean> = flow {
        try {
            var token: String? = null
            authRepository.exchangeCodeForToken(code).collect {
                token = it
            }

            if (token != null) {
                // Get user info - 同样使用 collect 收集
                var userName: String? = null
                gitHubRepository.getCurrentUser(token!!).collect { user ->
                    userName = user.login
                }

                if (userName != null) {
                    // Save auth info (token and username)
                    authRepository.saveAuthInfo(token!!, userName!!)
                    emit(true)
                } else {
                    emit(false)
                }
            } else {
                emit(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }
}
