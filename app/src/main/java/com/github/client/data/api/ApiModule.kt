package com.github.client.data.api

import com.github.client.BuildConfig
import com.github.client.data.converter.GithubOAuthConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiModule {
    private val gson: Gson = GsonBuilder().create()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val githubRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.GITHUB_API_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val authRetrofit = Retrofit.Builder()
        .baseUrl("https://github.com/")
        .client(httpClient)
        .addConverterFactory(GithubOAuthConverterFactory())
        .build()

    val githubService: GitHubService = githubRetrofit.create(GitHubService::class.java)
    val authService: AuthService = authRetrofit.create(AuthService::class.java)
}
