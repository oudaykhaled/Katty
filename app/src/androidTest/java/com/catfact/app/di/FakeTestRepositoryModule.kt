package com.catfact.app.di

import com.catfact.app.core.database.di.RepositoryModule
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.testing.FakeCatFactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object FakeTestRepositoryModule {
    @Provides
    @Singleton
    fun provideRepository(): CatFactRepository = FakeCatFactRepository()
}
