package com.github.client.domain.repository

import com.github.client.data.model.IssueResponse
import com.github.client.data.model.RepositoryResponse
import com.github.client.data.model.SearchRepositoriesResponse
import com.github.client.data.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun searchRepositories(
        query: String, language: String? = null, sort: String = "stars", page: Int = 1
    ): Flow<SearchRepositoriesResponse>

    fun getPopularRepositories(page: Int = 1): Flow<List<RepositoryResponse>>

    fun getRepositoryDetails(owner: String, repo: String): Flow<RepositoryResponse>

    fun getCurrentUser(token: String): Flow<UserResponse>

    fun getUserRepositories(token: String, page: Int = 1): Flow<List<RepositoryResponse>>

    fun createIssue(
        token: String, owner: String, repo: String, title: String, body: String
    ): Flow<IssueResponse>

    fun getTrendingRepositories(page: Int = 1): Flow<SearchRepositoriesResponse>
}
