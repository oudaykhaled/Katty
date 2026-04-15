package com.catfact.app.core.logging.di

import com.catfact.app.core.logging.Logger
import com.catfact.app.core.logging.TimberLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindLogger(impl: TimberLogger): Logger
}
