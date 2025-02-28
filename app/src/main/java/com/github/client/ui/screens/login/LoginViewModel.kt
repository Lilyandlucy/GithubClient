package com.github.client.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.BuildConfig
import com.github.client.domain.usecase.CheckAuthStatusUseCase
import com.github.client.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            checkAuthStatusUseCase()
                .catch {
                    _uiState.value = LoginUiState.NotAuthenticated
                }
                .collectLatest { isAuthenticated ->
                    _uiState.value = if (isAuthenticated) {
                        LoginUiState.Authenticated
                    } else {
                        LoginUiState.NotAuthenticated
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
                .catch { e ->
                    _uiState.value = LoginUiState.Error(e.localizedMessage ?: "Error logging out")
                }
                .collectLatest { success ->
                    if (success) {
                        _uiState.value = LoginUiState.NotAuthenticated
                    } else {
                        _uiState.value = LoginUiState.Error("Failed to logout")
                    }
                }
        }
    }

    fun getAuthUrl(): String {
        val scopes = "repo,user"
        return "https://github.com/login/oauth/authorize" +
                "?client_id=${BuildConfig.GITHUB_CLIENT_ID}" +
                "&scope=$scopes" +
                "&redirect_uri=github://callback"
    }
}

sealed class LoginUiState {
    data object Loading : LoginUiState()
    data object Authenticated : LoginUiState()
    data object NotAuthenticated : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}