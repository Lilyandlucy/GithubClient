package com.github.client.domain.usecase

import com.github.client.data.model.RepositoryResponse
import com.github.client.domain.model.Repository
import com.github.client.domain.model.User
import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetUserRepositoriesUseCase(
    private val gitHubRepository: GitHubRepository,
    private val authRepository: AuthRepository
) {

    operator fun invoke(page: Int = 1): Flow<List<Repository>> = flow {
        val token = authRepository.getAccessToken().first()
        if (token != null) {
            gitHubRepository.getUserRepositories(token, page)
                .map { repositories ->
                    repositories.map { it.toDomainModel() }
                }
                .collect { emit(it) }
        } else {
            throw IllegalStateException("User not authenticated")
        }
    }

    private fun RepositoryResponse.toDomainModel(): Repository {
        return Repository(
            id = id,
            name = name,
            fullName = full_name,
            owner = User(
                login = owner.login,
                id = owner.id,
                avatarUrl = owner.avatar_url,
                htmlUrl = owner.html_url
            ),
            description = description,
            language = language,
            starsCount = stargazers_count,
            forksCount = forks_count,
            issuesCount = open_issues_count,
            topics = topics,
            htmlUrl = html_url
        )
    }
}
