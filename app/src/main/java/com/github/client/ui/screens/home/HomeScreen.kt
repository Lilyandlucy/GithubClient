package com.github.client.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.client.domain.model.Repository
import com.github.client.ui.AuthViewModel
import com.github.client.ui.components.ErrorView
import com.github.client.ui.components.LoadingView
import com.github.client.ui.components.RepositoryItem
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navigateToRepositoryDetails: (owner: String, repo: String) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    viewModel: HomeViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()

    // 下拉刷新状态
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    // 列表滚动状态用于监测上拉加载更多
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            if (uiState is HomeUiState.Success) {
                val layoutInfo = listState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

                lastVisibleItem >= totalItems - 3 // 当用户接近底部时加载更多
            } else false
        }.collectLatest { shouldLoadMore ->
            if (shouldLoadMore && uiState is HomeUiState.Success &&
                (uiState as HomeUiState.Success).hasMoreData && !isLoadingMore
            ) {
                viewModel.loadNextPage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "GitHub Client") },
                actions = {
                    IconButton(onClick = navigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = {
                        if (isLoggedIn) {
                            navigateToProfile()
                        } else {
                            navigateToLogin()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = if (isLoggedIn) "Profile" else "Login"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingView()
                }

                is HomeUiState.Success -> {
                    RepositoriesList(
                        repositories = state.repositories,
                        listState = listState,
                        isLoadingMore = isLoadingMore,
                        hasMoreData = state.hasMoreData,
                        onItemClick = navigateToRepositoryDetails
                    )
                }

                is HomeUiState.Empty -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                            Text(
                                text = "No trending repositories found",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { viewModel.refresh() },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Refresh")
                            }
                        }
                    }
                }

                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorView(
                            message = state.message,
                            onRetry = { viewModel.loadTrendingRepositories(initialLoad = true) }
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun RepositoriesList(
    repositories: List<Repository>,
    listState: LazyListState,
    isLoadingMore: Boolean,
    hasMoreData: Boolean,
    onItemClick: (owner: String, repo: String) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = repositories,
            key = { repo -> repo.id }
        ) { repository ->
            RepositoryItem(
                repository = repository,
                onClick = {
                    onItemClick(
                        repository.owner.login,
                        repository.name
                    )
                }
            )
        }

        // 加载更多指示器 - 只有当hasMoreData且isLoadingMore为true时才显示
        if (hasMoreData && isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}