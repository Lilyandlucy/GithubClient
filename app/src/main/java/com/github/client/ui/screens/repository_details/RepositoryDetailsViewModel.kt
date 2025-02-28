package com.github.client.ui.screens.repository_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.model.Repository
import com.github.client.domain.usecase.GetRepositoryDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryDetailsViewModel(
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepositoryDetailsUiState>(RepositoryDetailsUiState.Loading)
    val uiState: StateFlow<RepositoryDetailsUiState> = _uiState

    fun loadRepositoryDetails(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.value = RepositoryDetailsUiState.Loading

            getRepositoryDetailsUseCase(owner, repo)
                .catch { e ->
                    _uiState.value = RepositoryDetailsUiState.Error(e.localizedMessage ?: "Unknown error occurred")
                }
                .collectLatest { repository ->
                    _uiState.value = RepositoryDetailsUiState.Success(repository)
                }
        }
    }
}

sealed class RepositoryDetailsUiState {
    data object Loading : RepositoryDetailsUiState()
    data class Success(val repository: Repository) : RepositoryDetailsUiState()
    data class Error(val message: String) : RepositoryDetailsUiState()
}