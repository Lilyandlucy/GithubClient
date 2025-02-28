package com.github.client.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.client.ui.AuthViewModel
import com.github.client.ui.components.ErrorView
import com.github.client.ui.components.LoadingView
import com.github.client.ui.components.RepositoryItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateToRepositoryDetails: (owner: String, repo: String) -> Unit,
    navigateToLogin: () -> Unit,
    navigateBack: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState() // **收集 isLoggedIn 状态**
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserProfile()
    }

    LaunchedEffect(key1 = uiState) {
        if (uiState is ProfileUiState.LoggedOut) {
            navigateToLogin()
        }
    }

    LaunchedEffect(key1 = isLoggedIn) {
        if (!isLoggedIn) {
            navigateToLogin()  // **关键：根据 isLoggedIn 状态导航**
        }
    }

    val showLogoutDialog = remember { mutableStateOf(false) }

    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout from your GitHub account?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog.value = false
                        viewModel.logout()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    LoadingView()
                }

                is ProfileUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // User profile header
                        item {
                            UserProfileHeader(
                                user = state.user,
                                onOpenGitHub = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.user.htmlUrl))
                                    context.startActivity(intent)
                                }
                            )

                            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                            Text(
                                text = "Repositories",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // Repositories list
                        items(state.repositories) { repo ->
                            RepositoryItem(
                                repository = repo,
                                onClick = {
                                    navigateToRepositoryDetails(repo.owner.login, repo.name)
                                }
                            )
                        }

                        // Load more repositories
                        if (state.hasMoreRepos) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    LaunchedEffect(Unit) {
                                        viewModel.loadMoreRepositories()
                                    }
                                }
                            }
                        }
                    }
                }

                is ProfileUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadUserProfile() }
                    )
                }

                is ProfileUiState.LoggedOut -> {
                    // Handled by LaunchedEffect
                }
            }
        }
    }
}

@Composable
fun UserProfileHeader(
    user: com.github.client.domain.model.User,
    onOpenGitHub: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "User avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user.login,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onOpenGitHub) {
            Icon(
//                imageVector = Icons.Default.OpenInBrowser,
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Open in GitHub"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View on GitHub")
        }
    }
}