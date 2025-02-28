package com.github.client.data.repository

import com.github.client.BuildConfig
import com.github.client.data.api.AuthService
import com.github.client.data.local.AuthPreferences
import com.github.client.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override fun getAccessToken(): Flow<String?> {
        return authPreferences.getAccessToken()
    }

    override fun getUserName(): Flow<String?> {
        return authPreferences.getUserName()
    }

    override fun isLoggedIn(): Flow<Boolean> = flow {
        val token = authPreferences.getAccessToken().firstOrNull()
        emit(token != null)
    }

    override fun exchangeCodeForToken(code: String): Flow<String> = flow {
        val response = authService.getAccessToken(
            BuildConfig.GITHUB_CLIENT_ID,
            BuildConfig.GITHUB_CLIENT_SECRET,
            code,
            "github://callback"
        )
        emit(response.access_token)
    }

    override suspend fun saveAuthInfo(accessToken: String, userName: String) {
        authPreferences.saveAuthInfo(accessToken, userName)
    }

    override suspend fun clearAuthInfo() {
        authPreferences.clearAuthInfo()
    }
}