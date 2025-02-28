package com.github.client.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.client.ui.components.ErrorView
import com.github.client.ui.components.LoadingView
import com.github.client.ui.components.RepositoryItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigateToRepositoryDetails: (owner: String, repo: String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<String?>(null) }

    val languageOptions = listOf(
        "All" to null,
        "Kotlin" to "kotlin",
        "Java" to "java",
        "Python" to "python",
        "JavaScript" to "javascript",
        "TypeScript" to "typescript"
    )

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.scrollToTopEvent.collect {
            coroutineScope.launch {
                kotlinx.coroutines.delay(100)
                listState.scrollToItem(0)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Repositories") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester),
                placeholder = { Text("Search repositories...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotEmpty()) {
                            viewModel.searchRepositories(searchQuery, selectedLanguage)
                            focusManager.clearFocus()
                        }
                    }
                )
            )

            // Language filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languageOptions.forEach { (name, value) ->
                    FilterChip(
                        selected = selectedLanguage == value,
                        onClick = {
                            selectedLanguage = value
                            if (searchQuery.isNotEmpty()) {
                                viewModel.searchRepositories(searchQuery, value)
                            }
                        },
                        label = { Text(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results
            when (val state = uiState) {
                is SearchUiState.Initial -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search for GitHub repositories",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is SearchUiState.Loading -> {
                    LoadingView()
                }

                is SearchUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No repositories found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is SearchUiState.Success -> {
                    Column {
                        Text(
                            text = "Found ${state.totalCount} repositories",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.repositories) { repo ->
                                RepositoryItem(
                                    repository = repo,
                                    onClick = {
                                        navigateToRepositoryDetails(repo.owner.login, repo.name)
                                    }
                                )
                            }

                            // 底部加载更多
                            item {
                                if (state.hasMoreData) {
                                    LaunchedEffect(Unit) {
                                        viewModel.loadNextPage()
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                is SearchUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = {
                            viewModel.searchRepositories(searchQuery, selectedLanguage)
                        }
                    )
                }
            }
        }
    }
}