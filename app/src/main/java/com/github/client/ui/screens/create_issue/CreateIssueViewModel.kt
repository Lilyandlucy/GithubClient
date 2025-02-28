package com.github.client.ui.screens.create_issue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.model.Issue
import com.github.client.domain.usecase.CreateIssueUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreateIssueViewModel(
    private val createIssueUseCase: CreateIssueUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateIssueUiState>(CreateIssueUiState.Initial)
    val uiState: StateFlow<CreateIssueUiState> = _uiState

    fun createIssue(
        owner: String,
        repo: String,
        title: String,
        body: String
    ) {
        if (title.isBlank()) {
            _uiState.value = CreateIssueUiState.Error("Title cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateIssueUiState.Loading

            createIssueUseCase(owner, repo, title, body)
                .catch { e ->
                    _uiState.value =
                        CreateIssueUiState.Error(e.localizedMessage ?: "Unknown error occurred")
                }
                .collectLatest { issue ->
                    _uiState.value = CreateIssueUiState.Success(issue)
                }
        }
    }

    fun resetState() {
        _uiState.value = CreateIssueUiState.Initial
    }
}

sealed class CreateIssueUiState {
    data object Initial : CreateIssueUiState()
    data object Loading : CreateIssueUiState()
    data class Success(val issue: Issue) : CreateIssueUiState()
    data class Error(val message: String) : CreateIssueUiState()
}