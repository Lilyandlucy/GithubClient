package com.github.client.domain.usecase

import com.github.client.data.model.UserResponse
import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import com.github.client.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class LoginUseCaseTest {

    @Test
    fun `invoke should return true when all steps are successful`() = runBlocking {
        // Arrange
        val mockAuthRepository = mock(AuthRepository::class.java)
        val mockGitHubRepository = mock(GitHubRepository::class.java)
        val useCase = LoginUseCase(mockAuthRepository, mockGitHubRepository)

        `when`(mockAuthRepository.exchangeCodeForToken(anyString())).thenReturn(flowOf("testToken"))
        `when`(mockGitHubRepository.getCurrentUser(anyString())).thenReturn(
            flowOf(
                UserResponse(
                    "testUser",
                    1,
                    "url",
                    "url",
                    "url",
                    "url",
                    "User"
                )
            )
        )
        `when`(mockAuthRepository.saveAuthInfo(anyString(), anyString())).thenReturn(Unit)

        // Act
        useCase("testCode").collect {
            assertTrue(it)
        }

        // Assert
        verify(mockAuthRepository).exchangeCodeForToken("testCode")
        verify(mockGitHubRepository).getCurrentUser("testToken")
        verify(mockAuthRepository).saveAuthInfo("testToken", "testUser")
    }

    @Test
    fun `invoke should return false when exchangeCodeForToken throws exception`() = runTest {
        // Arrange
        val mockAuthRepository = mock(AuthRepository::class.java)
        val mockGitHubRepository = mock(GitHubRepository::class.java)
        val useCase = LoginUseCase(mockAuthRepository, mockGitHubRepository)

        `when`(mockAuthRepository.exchangeCodeForToken(anyString())).thenThrow(RuntimeException("Test error"))

        // Act
        val result = useCase("testCode").collect {
            println("=====")
            println(it)
            assertFalse(it)
        }


        // Assert
        verify(mockAuthRepository).exchangeCodeForToken("testCode")
        verify(mockGitHubRepository, never()).getCurrentUser(anyString())
        verify(mockAuthRepository, never()).saveAuthInfo(anyString(), anyString())
    }
}