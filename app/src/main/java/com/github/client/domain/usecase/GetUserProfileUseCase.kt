package com.github.client.domain.usecase

import com.github.client.data.model.UserResponse
import com.github.client.domain.model.User
import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetUserProfileUseCase(
    private val gitHubRepository: GitHubRepository,
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<User> = flow {
        val token = authRepository.getAccessToken().first()
        if (token != null) {
            gitHubRepository.getCurrentUser(token)
                .map { it.toDomainModel() }
                .collect { emit(it) }
        } else {
            throw IllegalStateException("User not authenticated")
        }
    }

    private fun UserResponse.toDomainModel(): User {
        return User(
            login = login,
            id = id,
            avatarUrl = avatar_url,
            htmlUrl = html_url
        )
    }
}
