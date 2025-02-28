package com.github.client.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.client.domain.model.Repository

@Composable
fun RepositoryItem(
    repository: Repository,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = repository.owner.avatarUrl,
                    contentDescription = "Owner avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = repository.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    repository.description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repository.language?.let {
                    LanguageBadge(language = it)
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Stars",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = repository.starsCount.toString(),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Forks: ${repository.forksCount}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun LanguageBadge(language: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = when (language.lowercase()) {
            "kotlin" -> MaterialTheme.colorScheme.primary
            "java" -> MaterialTheme.colorScheme.tertiary
            "python" -> MaterialTheme.colorScheme.secondary
            "javascript" -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outline
        }

        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(12.dp)
        ) {
            drawCircle(color = color)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = language,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
