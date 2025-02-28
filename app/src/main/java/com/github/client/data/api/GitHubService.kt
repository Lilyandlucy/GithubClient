package com.github.client.data.api

import com.github.client.data.model.*
import retrofit2.http.*

interface GitHubService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchRepositoriesResponse

    @GET("repositories")
    suspend fun getPublicRepositories(
        @Query("since") since: Int? = null,
        @Query("per_page") perPage: Int = 30
    ): List<RepositoryResponse>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepositoryResponse

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): UserResponse

    @GET("user/repos")
    suspend fun getUserRepositories(
        @Header("Authorization") token: String,
        @Query("sort") sort: String = "updated",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): List<RepositoryResponse>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Header("Authorization") token: String,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: IssueRequest
    ): IssueResponse
}