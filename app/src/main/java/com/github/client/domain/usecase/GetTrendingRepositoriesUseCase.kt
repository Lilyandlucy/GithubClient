package com.github.client.domain.usecase

import com.github.client.data.model.RepositoryResponse
import com.github.client.domain.model.Repository
import com.github.client.domain.model.User
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTrendingRepositoriesUseCase(private val gitHubRepository: GitHubRepository) {

    operator fun invoke(page: Int = 1): Flow<RepositoriesResult> {
        return gitHubRepository.getTrendingRepositories(page)
            .map { result ->
                RepositoriesResult(
                    repositories = result.items.map { it.toDomainModel() },
                    hasMoreData = result.hasNextPage(),
                    totalCount = result.total_count,
                    nextPage = if (result.hasNextPage()) page + 1 else null
                )
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

data class RepositoriesResult(
    val repositories: List<Repository>,
    val hasMoreData: Boolean,
    val totalCount: Int,
    val nextPage: Int?
)
