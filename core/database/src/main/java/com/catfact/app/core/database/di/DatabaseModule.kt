package com.catfact.app.core.database.di

import android.content.Context
import androidx.room.Room
import com.catfact.app.core.database.CatFactDatabase
import com.catfact.app.core.database.dao.CatFactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CatFactDatabase =
        Room.databaseBuilder(
            context,
            CatFactDatabase::class.java,
            "catfact.db"
        ).build()

    @Provides
    @Singleton
    fun provideCatFactDao(database: CatFactDatabase): CatFactDao =
        database.catFactDao()
}
