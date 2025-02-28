package com.github.client.domain.usecase

import com.github.client.data.model.RepositoryResponse
import com.github.client.domain.model.Repository
import com.github.client.domain.model.SearchResult
import com.github.client.domain.model.User
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoriesUseCase(private val gitHubRepository: GitHubRepository) {

    operator fun invoke(
        query: String,
        language: String? = null,
        page: Int = 1
    ): Flow<SearchResult> {
        return gitHubRepository.searchRepositories(query, language, "stars", page)
            .map { response ->
                SearchResult(
                    totalCount = response.total_count,
                    items = response.items.map { it.toDomainModel() },
                    nextPage = if (response.items.isNotEmpty()) page + 1 else null
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
