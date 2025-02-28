package com.github.client.data.repository

import android.util.Log
import com.github.client.data.api.GitHubService
import com.github.client.data.model.*
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GitHubRepositoryImpl(private val gitHubService: GitHubService) : GitHubRepository {

    override fun searchRepositories(
        query: String,
        language: String?,
        sort: String,
        page: Int
    ): Flow<SearchRepositoriesResponse> = flow {
        val queryWithLanguage = if (language.isNullOrEmpty()) query
        else "$query language:$language"
        emit(gitHubService.searchRepositories(queryWithLanguage, sort, "desc", page))
    }

    override fun getPopularRepositories(page: Int): Flow<List<RepositoryResponse>> = flow {
        emit(gitHubService.getPublicRepositories(page * 30, 30))
    }

    override fun getRepositoryDetails(owner: String, repo: String): Flow<RepositoryResponse> =
        flow {
            emit(gitHubService.getRepository(owner, repo))
        }

    override fun getCurrentUser(token: String): Flow<UserResponse> = flow {
        emit(gitHubService.getCurrentUser("token $token"))
    }

    override fun getUserRepositories(token: String, page: Int): Flow<List<RepositoryResponse>> =
        flow {
            emit(gitHubService.getUserRepositories("token $token", "updated", page))
        }

    override fun createIssue(
        token: String,
        owner: String,
        repo: String,
        title: String,
        body: String
    ): Flow<IssueResponse> = flow {
        val issueRequest = IssueRequest(title, body)
        emit(gitHubService.createIssue("token $token", owner, repo, issueRequest))
    }

    override fun getTrendingRepositories(page: Int): Flow<SearchRepositoriesResponse> = flow {
        try {
            val response = gitHubService.searchRepositories("trending", page = page)
            emit(response)
        } catch (e: Exception) {
            Log.e("GitHubRepo", "Error fetching trending repos: ${e.message}")
            emit(SearchRepositoriesResponse(0, true, emptyList()))
        }
    }
}

