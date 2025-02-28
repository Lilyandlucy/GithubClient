package com.github.client.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.model.Repository
import com.github.client.domain.model.User
import com.github.client.domain.usecase.GetUserProfileUseCase
import com.github.client.domain.usecase.GetUserRepositoriesUseCase
import com.github.client.ui.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserRepositoriesUseCase: GetUserRepositoriesUseCase,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private var currentPage = 1

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val user = getUserProfileUseCase().collectLatest { user ->
                    // Load repositories after getting user profile
                    loadUserRepositories(user)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Unknown error occurred")
            }
        }
    }

    private fun loadUserRepositories(user: User, page: Int = 1) {
        viewModelScope.launch {
            getUserRepositoriesUseCase(page)
                .catch { e ->
                    _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Unknown error occurred")
                }
                .collectLatest { repositories ->
                    val currentRepos = if (page == 1) emptyList() else
                        (_uiState.value as? ProfileUiState.Success)?.repositories ?: emptyList()

                    val newList = currentRepos + repositories

                    _uiState.value = ProfileUiState.Success(
                        user = user,
                        repositories = newList,
                        hasMoreRepos = repositories.isNotEmpty()
                    )
                }
        }
    }

    fun loadMoreRepositories() {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success && currentState.hasMoreRepos) {
            currentPage++
            loadUserRepositories(currentState.user, currentPage)
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authViewModel.logout()
                _uiState.value = ProfileUiState.LoggedOut
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.localizedMessage ?: "Error logging out")  // **登出失败**
            }
        }
    }
}

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val user: User,
        val repositories: List<Repository>,
        val hasMoreRepos: Boolean
    ) : ProfileUiState()
    data object LoggedOut : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}