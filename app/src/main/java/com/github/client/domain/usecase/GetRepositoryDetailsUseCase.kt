package com.github.client.domain.usecase

import com.github.client.data.model.RepositoryResponse
import com.github.client.domain.model.Repository
import com.github.client.domain.model.User
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRepositoryDetailsUseCase(private val gitHubRepository: GitHubRepository) {

    operator fun invoke(owner: String, repo: String): Flow<Repository> {
        return gitHubRepository.getRepositoryDetails(owner, repo)
            .map { it.toDomainModel() }
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
