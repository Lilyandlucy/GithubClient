package com.github.client.data.repository

import com.github.client.data.api.GitHubService
import com.github.client.data.model.RepositoryResponse
import com.github.client.data.model.SearchRepositoriesResponse
import com.github.client.data.model.UserResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GitHubRepositoryImplTest {

    @Test
    fun `searchRepositories should return SearchRepositoriesResponse when API call is successful`() =
        runTest {
            val mockGitHubService = mock(GitHubService::class.java)
            val repository = GitHubRepositoryImpl(mockGitHubService)

            val mockResponse = SearchRepositoriesResponse(
                total_count = 1,
                incomplete_results = false,
                items = listOf(
                    RepositoryResponse(
                        id = 1,
                        name = "testRepo",
                        full_name = "testOwner/testRepo",
                        owner = UserResponse("testOwner", 1, "url", "url", "url", "url", "User"),
                        html_url = "url",
                        description = "testDescription",
                        fork = false,
                        url = "url",
                        created_at = "date",
                        updated_at = "date",
                        pushed_at = "date",
                        homepage = null,
                        size = 100,
                        stargazers_count = 10,
                        watchers_count = 10,
                        language = "Kotlin",
                        forks_count = 5,
                        open_issues_count = 2,
                        license = null,
                        topics = null,
                        default_branch = "main"
                    )
                )
            )

            `when`(
                mockGitHubService.searchRepositories(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyInt(),
                    anyInt(),
                )
            ).thenReturn(mockResponse)

            // Act
            repository.searchRepositories("testQuery").collect {
                println(it)
                assertNotNull(it)
                assertEquals(1, it.total_count)
                assertEquals("testRepo", it.items[0].name)
            }

            verify(mockGitHubService).searchRepositories("testQuery", "stars", "desc", 1)
        }

    @Test
    fun `searchRepositories should throw exception when API call fails`() = runTest {
        // Arrange
        val mockGitHubService = mock(GitHubService::class.java)
        val repository = GitHubRepositoryImpl(mockGitHubService)

        `when`(
            mockGitHubService.searchRepositories(
                anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyInt()
            )
        ).thenThrow(RuntimeException("Test error"))

        try {
            repository.searchRepositories("testQuery").collect {}
            fail("Expected RuntimeException to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Test error", e.message)
        }

        verify(mockGitHubService).searchRepositories("testQuery", "stars", "desc", 1)
    }

}