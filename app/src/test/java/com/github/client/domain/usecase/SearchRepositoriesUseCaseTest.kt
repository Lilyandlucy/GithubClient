package com.github.client.domain.usecase

import com.github.client.data.model.LicenseResponse
import com.github.client.data.model.RepositoryResponse
import com.github.client.data.model.SearchRepositoriesResponse
import com.github.client.data.model.UserResponse
import com.github.client.domain.repository.GitHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class SearchRepositoriesUseCaseTest {

    @Test
    fun `invoke should return SearchResult with correct data`() = runTest {
        // Arrange
        val mockGitHubRepository = mock(GitHubRepository::class.java)
        val useCase = SearchRepositoriesUseCase(mockGitHubRepository)

        // 创建模拟的 RepositoryResponse 列表
        val mockRepositories = listOf(
            RepositoryResponse(
                id = 1,
                name = "Repo1",
                full_name = "Owner1/Repo1",
                owner = UserResponse(
                    login = "Owner1",
                    id = 1,
                    avatar_url = "url1",
                    url = "url1",
                    html_url = "url1",
                    repos_url = "url1",
                    type = "User"
                ),
                html_url = "html_url1",
                description = "Description1",
                fork = false,
                url = "url1",
                created_at = "date1",
                updated_at = "date1",
                pushed_at = "date1",
                homepage = null,
                size = 100,
                stargazers_count = 10,
                watchers_count = 10,
                language = "Kotlin",
                forks_count = 5,
                open_issues_count = 2,
                license = LicenseResponse("MIT", "MIT License", "url"),
                topics = listOf("topic1", "topic2"),
                default_branch = "main"
            ),
            RepositoryResponse(
                id = 2,
                name = "Repo2",
                full_name = "Owner2/Repo2",
                owner = UserResponse(
                    login = "Owner2",
                    id = 2,
                    avatar_url = "url2",
                    url = "url2",
                    html_url = "url2",
                    repos_url = "url2",
                    type = "User"
                ),
                html_url = "html_url2",
                description = "Description2",
                fork = false,
                url = "url2",
                created_at = "date2",
                updated_at = "date2",
                pushed_at = "date2",
                homepage = null,
                size = 200,
                stargazers_count = 20,
                watchers_count = 20,
                language = "Java",
                forks_count = 10,
                open_issues_count = 4,
                license = LicenseResponse("Apache 2.0", "Apache 2.0 License", "url"),
                topics = listOf("topic3", "topic4"),
                default_branch = "master"
            )
        )

        val mockResponse = SearchRepositoriesResponse(
            total_count = 2,
            incomplete_results = false,
            items = mockRepositories
        )

        `when`(
            mockGitHubRepository.searchRepositories(
                anyString(),
                any(),
                anyString(),
                anyInt()
            )
        ).thenReturn(flowOf(mockResponse))

        // Act
        useCase("testQuery", null, 1).collect {
            // Assert
            assertNotNull(it)
            assertEquals(2, it.totalCount)
            assertEquals(2, it.items.size)
            assertEquals("Repo1", it.items[0].name)
        }
        verify(mockGitHubRepository).searchRepositories(anyString(), any(), anyString(), anyInt())
    }

    @Test
    fun `invoke should handle error and throw exception`() = runTest {
        // Arrange
        val mockGitHubRepository = mock(GitHubRepository::class.java)
        val useCase = SearchRepositoriesUseCase(mockGitHubRepository)

        // 设置 mockGitHubRepository 的行为，使其抛出异常
        `when`(
            mockGitHubRepository.searchRepositories(
                anyString(),
                any(),
                anyString(),
                anyInt()
            )
        ).thenThrow(RuntimeException("Test error"))

        // Act and Assert
        try {
            useCase("testQuery", null, 1).collect {}
            fail("Expected RuntimeException to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("Test error", e.message)
        }

        verify(mockGitHubRepository).searchRepositories(anyString(), any(), anyString(), anyInt())
    }
}
