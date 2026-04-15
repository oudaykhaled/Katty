package com.catfact.app.core.database.di

import com.catfact.app.core.database.repository.CatFactRepositoryImpl
import com.catfact.app.core.domain.repository.CatFactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCatFactRepository(impl: CatFactRepositoryImpl): CatFactRepository
}
