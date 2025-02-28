package com.github.client.domain.model

data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val owner: User,
    val description: String?,
    val language: String?,
    val starsCount: Int,
    val forksCount: Int,
    val issuesCount: Int,
    val topics: List<String>?,
    val htmlUrl: String
)

data class User(
    val login: String,
    val id: Long,
    val avatarUrl: String,
    val htmlUrl: String
)

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val user: User,
    val state: String,
    val createdAt: String,
    val body: String?
)

data class SearchResult(
    val totalCount: Int,
    val items: List<Repository>,
    val nextPage: Int?
)
