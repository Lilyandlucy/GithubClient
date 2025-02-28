package com.github.client.di

import com.github.client.data.api.ApiModule
import com.github.client.data.local.AuthPreferences
import com.github.client.data.repository.AuthRepositoryImpl
import com.github.client.data.repository.GitHubRepositoryImpl
import com.github.client.domain.repository.AuthRepository
import com.github.client.domain.repository.GitHubRepository
import com.github.client.domain.usecase.*
import com.github.client.ui.AuthViewModel
import com.github.client.ui.MainViewModel
import com.github.client.ui.screens.create_issue.CreateIssueViewModel
import com.github.client.ui.screens.home.HomeViewModel
import com.github.client.ui.screens.login.LoginViewModel
import com.github.client.ui.screens.profile.ProfileViewModel
import com.github.client.ui.screens.repository_details.RepositoryDetailsViewModel
import com.github.client.ui.screens.search.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val apiModule = module {
    single { ApiModule.githubService }
    single { ApiModule.authService }
    single { AuthPreferences(androidContext()) }
}

val repositoryModule = module {
    single<GitHubRepository> { GitHubRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}

val useCaseModule = module {
    factory { SearchRepositoriesUseCase(get()) }
    factory { GetRepositoryDetailsUseCase(get()) }
    factory { GetUserProfileUseCase(get(), get()) }
    factory { GetUserRepositoriesUseCase(get(), get()) }
    factory { CreateIssueUseCase(get(), get()) }
    factory { LoginUseCase(get(), get()) }
    factory { LogoutUseCase(get()) }
    factory { CheckAuthStatusUseCase(get()) }
    factory { GetTrendingRepositoriesUseCase(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), androidApplication()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { RepositoryDetailsViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { CreateIssueViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
}
