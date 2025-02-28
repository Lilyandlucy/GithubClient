package com.github.client.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.client.domain.model.Repository
import com.github.client.domain.usecase.CheckAuthStatusUseCase
import com.github.client.domain.usecase.GetTrendingRepositoriesUseCase
import com.github.client.domain.usecase.RepositoriesResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingRepositoriesUseCase: GetTrendingRepositoriesUseCase,
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase
) : ViewModel() {

    // 主UI状态
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // 独立的刷新状态
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // 独立的加载更多状态
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private var currentPage = 1
    private val repositoriesList = mutableListOf<Repository>()
    private var hasMoreData = true

    init {
        loadTrendingRepositories(initialLoad = true)
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            checkAuthStatusUseCase().collectLatest { isAuthenticated ->
                _isLoggedIn.value = isAuthenticated
            }
        }
    }

    fun loadTrendingRepositories(initialLoad: Boolean = false, refresh: Boolean = false) {
        // 防止重复加载
        if ((!initialLoad && !refresh && _isLoadingMore.value) ||
            (refresh && _isRefreshing.value)) {
            return
        }

        if (initialLoad) {
            _uiState.value = HomeUiState.Loading
            currentPage = 1
            repositoriesList.clear()
        } else if (refresh) {
            _isRefreshing.value = true
            currentPage = 1
        } else {
            if (!hasMoreData) return
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            getTrendingRepositoriesUseCase(currentPage)
                .catch { e ->
                    Log.e("HomeViewModel", "Error loading trending: ${e.message}", e)

                    if (initialLoad) {
                        _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Unknown error occurred")
                    } else if (refresh) {
                        _isRefreshing.value = false
                    } else {
                        _isLoadingMore.value = false
                    }
                }
                .collectLatest { result ->
                    if (initialLoad) {
                        handleInitialLoad(result)
                    } else if (refresh) {
                        handleRefresh(result)
                    } else {
                        handleLoadMore(result)
                    }
                }
        }
    }

    private fun handleInitialLoad(result: RepositoriesResult) {
        repositoriesList.clear()
        repositoriesList.addAll(result.repositories)
        hasMoreData = result.hasMoreData

        if (repositoriesList.isEmpty()) {
            _uiState.value = HomeUiState.Empty
        } else {
            _uiState.value = HomeUiState.Success(repositoriesList.toList(), hasMoreData)
        }

        if (result.hasMoreData) currentPage++
    }

    private fun handleRefresh(result: RepositoriesResult) {
        _isRefreshing.value = false

        repositoriesList.clear()
        repositoriesList.addAll(result.repositories)
        hasMoreData = result.hasMoreData

        if (repositoriesList.isEmpty()) {
            _uiState.value = HomeUiState.Empty
        } else {
            _uiState.value = HomeUiState.Success(repositoriesList.toList(), hasMoreData)
        }

        if (result.hasMoreData) currentPage++
    }

    private fun handleLoadMore(result: RepositoriesResult) {
        _isLoadingMore.value = false

        repositoriesList.addAll(result.repositories)
        hasMoreData = result.hasMoreData

        // 更新现有的Success状态，保持列表可见
        if (repositoriesList.isNotEmpty()) {
            _uiState.value = HomeUiState.Success(repositoriesList.toList(), hasMoreData)
        }

        if (result.hasMoreData) currentPage++
    }

    fun loadNextPage() {
        if (!_isLoadingMore.value && hasMoreData) {
            loadTrendingRepositories(initialLoad = false, refresh = false)
        }
    }

    fun refresh() {
        loadTrendingRepositories(initialLoad = false, refresh = true)
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data object Empty : HomeUiState()
    data class Success(val repositories: List<Repository>, val hasMoreData: Boolean) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
