package com.github.client.domain.usecase

import com.github.client.data.model.IssueResponse
import com.github.client.domain.model.Issue
import com.github.client.domain.model.User
import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CreateIssueUseCase(
    private val gitHubRepository: GitHubRepository,
    private val authRepository: AuthRepository
) {

    operator fun invoke(
        owner: String,
        repo: String,
        title: String,
        body: String
    ): Flow<Issue> = flow {
        val token = authRepository.getAccessToken().first()
        if (token != null) {
            gitHubRepository.createIssue(token, owner, repo, title, body)
                .map { it.toDomainModel() }
                .collect { emit(it) }
        } else {
            throw IllegalStateException("User not authenticated")
        }
    }

    private fun IssueResponse.toDomainModel(): Issue {
        return Issue(
            id = id,
            number = number,
            title = title,
            user = User(
                login = user.login,
                id = user.id,
                avatarUrl = user.avatar_url,
                htmlUrl = user.html_url
            ),
            state = state,
            createdAt = created_at,
            body = body
        )
    }
}
