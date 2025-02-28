package com.github.client.data.model

data class RepositoryResponse(
    val id: Long,
    val name: String,
    val full_name: String,
    val owner: UserResponse,
    val html_url: String,
    val description: String?,
    val fork: Boolean,
    val url: String,
    val created_at: String,
    val updated_at: String,
    val pushed_at: String,
    val homepage: String?,
    val size: Int,
    val stargazers_count: Int,
    val watchers_count: Int,
    val language: String?,
    val forks_count: Int,
    val open_issues_count: Int,
    val license: LicenseResponse?,
    val topics: List<String>?,
    val default_branch: String
)

data class UserResponse(
    val login: String,
    val id: Long,
    val avatar_url: String,
    val url: String,
    val html_url: String,
    val repos_url: String,
    val type: String
)

data class LicenseResponse(
    val key: String,
    val name: String,
    val url: String?
)

data class SearchRepositoriesResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    val items: List<RepositoryResponse>
) {
    /**
     * 检查是否有下一页数据，判断条件要优化，GitHub API 返回最多1000个结果 先这么判断下用于加载更多
     */
    fun hasNextPage(): Boolean {
        return items.isNotEmpty() && total_count > items.size
    }
}

data class IssueRequest(
    val title: String,
    val body: String,
    val labels: List<String>? = null
)

data class IssueResponse(
    val id: Long,
    val number: Int,
    val title: String,
    val user: UserResponse,
    val state: String,
    val created_at: String,
    val body: String?
)

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String
)

data class ErrorResponse(
    val message: String,
    val documentation_url: String
)
