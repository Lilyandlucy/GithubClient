@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.github.client.ui.screens.repository_details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.client.R
import com.github.client.domain.model.Repository
import com.github.client.ui.components.ErrorView
import com.github.client.ui.components.LanguageBadge
import com.github.client.ui.components.LoadingView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailsScreen(
    owner: String,
    repo: String,
    navigateToCreateIssue: (owner: String, repo: String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: RepositoryDetailsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = owner, key2 = repo) {
        viewModel.loadRepositoryDetails(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$owner/$repo") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is RepositoryDetailsUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = { navigateToCreateIssue(owner, repo) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Create Issue") },
                    text = { Text("New Issue") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is RepositoryDetailsUiState.Loading -> {
                    LoadingView()
                }

                is RepositoryDetailsUiState.Success -> {
                    RepositoryDetailsContent(
                        repository = state.repository,
                        onOpenInBrowser = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(state.repository.htmlUrl))
                            context.startActivity(intent)
                        }
                    )
                }

                is RepositoryDetailsUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadRepositoryDetails(owner, repo) }
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoryDetailsContent(
    repository: Repository,
    onOpenInBrowser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Repository header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = repository.owner.avatarUrl,
                contentDescription = "Owner avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "by ${repository.owner.login}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        repository.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Repository Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        icon = Icons.Default.Star,
                        label = "Stars",
                        value = repository.starsCount.toString()
                    )

                    StatItem(
                        icon = Icons.Default.Send,
                        label = "Forks",
                        value = repository.forksCount.toString()
                    )

                    StatItem(
                        icon = Icons.Default.Edit,
                        label = "Issues",
                        value = repository.issuesCount.toString()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language and topics
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Language: ",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            if (repository.language != null) {
                LanguageBadge(language = repository.language)
            } else {
                Text(
                    text = "Not specified",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Topics
        if (!repository.topics.isNullOrEmpty()) {
            Text(
                text = "Topics:",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repository.topics.forEach { topic ->
                    SuggestionChip(
                        onClick = { /* No action */ },
                        label = { Text(topic) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //TODO Open in browser button
        Button(
            onClick = onOpenInBrowser,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Open in browser"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "View on GitHub")
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}