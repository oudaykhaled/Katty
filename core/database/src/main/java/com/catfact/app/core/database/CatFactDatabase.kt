package com.catfact.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.entity.CatFactEntity

@Database(
    entities = [CatFactEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CatFactDatabase : RoomDatabase() {
    abstract fun catFactDao(): CatFactDao
}
