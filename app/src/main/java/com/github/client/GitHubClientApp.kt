package com.github.client

import android.app.Application
import com.github.client.di.apiModule
import com.github.client.di.repositoryModule
import com.github.client.di.useCaseModule
import com.github.client.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class GitHubClientApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@GitHubClientApp)
            modules(
                apiModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
