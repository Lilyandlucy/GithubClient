package com.github.client.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.model.Repository
import com.github.client.domain.usecase.SearchRepositoriesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState

    // 添加滚动到顶部的事件流
    private val _scrollToTopEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val scrollToTopEvent: SharedFlow<Unit> = _scrollToTopEvent.asSharedFlow()

    private var currentPage = 1
    private var currentQuery = ""
    private var currentLanguage: String? = null

    fun searchRepositories(query: String, language: String? = null, resetPage: Boolean = true) {
        if (query.isBlank()) return

        if (resetPage) {
            currentPage = 1
            currentQuery = query
            currentLanguage = language

            _scrollToTopEvent.tryEmit(Unit)
        }

        viewModelScope.launch {
            _uiState.value = if (resetPage) SearchUiState.Loading else _uiState.value

            searchRepositoriesUseCase(currentQuery, currentLanguage, currentPage)
                .catch { e ->
                    _uiState.value =
                        SearchUiState.Error(e.localizedMessage ?: "Unknown error occurred")
                }
                .collectLatest { result ->
                    val currentList = if (resetPage) emptyList() else
                        (_uiState.value as? SearchUiState.Success)?.repositories ?: emptyList()

                    val newList = currentList + result.items

                    _uiState.value = if (newList.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(
                            repositories = newList,
                            hasMoreData = result.nextPage != null,
                            totalCount = result.totalCount
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        if ((_uiState.value as? SearchUiState.Success)?.hasMoreData == true) {
            currentPage++
            searchRepositories(currentQuery, currentLanguage, false)
        }
    }
}

sealed class SearchUiState {
    data object Initial : SearchUiState()
    data object Loading : SearchUiState()
    data object Empty : SearchUiState()
    data class Success(
        val repositories: List<Repository>,
        val hasMoreData: Boolean,
        val totalCount: Int
    ) : SearchUiState()

    data class Error(val message: String) : SearchUiState()
}